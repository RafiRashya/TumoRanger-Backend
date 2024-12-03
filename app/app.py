from flask import Flask, request, jsonify
from flask_migrate import Migrate
from flask_jwt_extended import (
    JWTManager,
    create_access_token,
    jwt_required,
    get_jwt,
    get_jwt_identity,
)
from google.cloud import storage
from werkzeug.security import generate_password_hash, check_password_hash
from .models import db, User, Patient, MRIScan, Diagnosis
from .config import Config
from datetime import datetime, timezone
import uuid
import requests

app = Flask(__name__)
app.config.from_object(Config)

db.init_app(app)
jwt = JWTManager(app)
migrate = Migrate(app, db)

storage_client = storage.Client()
bucket_name = "tumoranger-ml-test"

blacklist = set()

@jwt.token_in_blocklist_loader
def check_if_token_in_blacklist(jwt_header, jwt_payload):
    jti = jwt_payload["jti"]  # Unique identifier for the JWT
    return jti in blacklist

@app.route("/auth/register", methods=["POST"])
def register():
    try:
        data = request.json

        name = data["name"]
        email = data["email"]
        password = data["password"]

        user = User.query.filter(User.email == email).first()

        if user:
            response_data = {
                "status": 400,
                "message": "Email or user already exists",
            }
            return jsonify(response_data), 400

        hashed_password = generate_password_hash(password, method="pbkdf2:sha-256")
        new_user = User(id=str(uuid.uuid4()), name=name, email=email, password_hash=hashed_password)
        db.session.add(new_user)
        db.session.commit()

        response_data = {
            "status": 200,
            "message": "User created successfully",
            "data": {"account": {"name": name, "email": email}},
        }
        return jsonify(response_data), 200

    except Exception as e:
        response_data = {"status": 500, "message": f"Error Reason: {str(e)}"}
        return jsonify(response_data), 500


@app.route("/auth/login", methods=["POST"])
def login():
    try:
        data = request.json

        email = data["email"]
        password = data["password"]

        user = User.query.filter(User.email == email).first()

        if user is None or not check_password_hash(user.password_hash, password):
            response_data = {
                "status": 401,
                "message": "Email or password is incorrect",
                "data": None,
            }
            return jsonify(response_data), 401

        token = create_access_token(
            identity={"id": user.id, "name": user.name, "email": user.email}
        )

        response_data = {
            "status": 200,
            "message": "Login successful",
            "data": {"token": token},
        }
        return jsonify(response_data), 200

    except Exception as e:
        response_data = {"status": 500, "message": f"Error Reason: {str(e)}"}
        return jsonify(response_data), 500


@app.route("/dashboard", methods=["GET"])
@jwt_required()
def dashboard():
    current_user = get_jwt_identity()
    return (
        jsonify(
            {
                "message": f"Hello, {current_user['name']}! Welcome to your dashboard."
            }
        ),
        200,
    )

@app.route("/auth/logout", methods=["POST"])
@jwt_required()
def logout():
    try:
        jti = get_jwt()["jti"]
        blacklist.add(jti)
        response_data = {
            "status": 200,
            "message": "Logout successful",
            "data": None,
        }

        return jsonify(response_data), 200

    except Exception as e:
        app.logger.error(f"Error during logout: {str(e)}")
        response_data = {
            "status": 500,
            "message": "An internal error occurred. Please try again later.",
        }
        return jsonify(response_data), 500

@app.route("/profile", methods=["GET"])
@jwt_required()
def get_profile():
    try:
        # Get the current user's identity from the JWT token
        current_user = get_jwt_identity()

        # You can modify this to include more user information as needed
        user_data = {
            "username": current_user["username"],
            "email": current_user["email"]
        }

        response_data = {
            "status": 200,
            "message": "User profile retrieved successfully",
            "data": user_data
        }

        return jsonify(response_data), 200

    except Exception as e:
        response_data = {"status": 500, "message": f"Error Reason: {str(e)}"}
        return jsonify(response_data), 500

@app.route('/diagnose/history', methods=['GET'])
@jwt_required()
def get_diagnosis_history():
    current_user = get_jwt_identity()  # Get the identity of the current user
    user_id = current_user['id']  # Extract the user ID from the token

    # Query to get the relevant data
    results = db.session.query(
        Patient.name.label('patient_name'),
        Patient.gender,
        Patient.birthdate,
        Diagnosis.result,
        Diagnosis.confidence_score,
        MRIScan.file_path,
        Diagnosis.diagnosis_date
    ).join(MRIScan, MRIScan.patient_id == Patient.id) \
     .join(Diagnosis, Diagnosis.scan_id == MRIScan.id) \
     .filter(Diagnosis.diagnosed_by == user_id).all()


    # Format the results
    diagnosis_history = []
    for patient_name, gender, birthdate, result, confidence_score, file_path, diagnosis_date in results:
        diagnosis_history.append({
            'patient_name': patient_name,
            'gender': gender,
            'birthdate': str(birthdate),
            'result': result,
            'confidence_score': confidence_score,
            'file_path': file_path,
            'diagnosis_date': str(diagnosis_date)
        })

    return jsonify({
        'data': diagnosis_history,
        'message': 'Diagnosis Historical data has been successfully retrieved.'
    }), 200


@app.route('/diagnose', methods=['POST'])
@jwt_required()
def upload_mri():
    current_user = get_jwt_identity()

    try:
        # Retrieve file and patient data from request
        file = request.files['file']
        patient_name = request.form['patient_name']
        birthdate = request.form['birthdate']
        gender = request.form['gender']

        # Save patient data to the database
        patient = Patient.query.filter_by(name=patient_name, birthdate=birthdate).first()
        if not patient:
            patient = Patient(
                id=str(uuid.uuid4()),
                user_id=current_user['id'],
                name=patient_name,
                birthdate=birthdate,
                gender=gender
            )
            db.session.add(patient)
            db.session.commit()  # Commit the new patient here

        # Upload file to Google Cloud Storage
        bucket = storage_client.bucket(bucket_name)
        timestamp = datetime.now().strftime('%Y%m%d%H%M%S')
        unique_filename = f"{timestamp}_{file.filename}"
        blob = bucket.blob(f"mri_scans/{unique_filename}")
        blob.upload_from_string(file.read(), content_type=file.content_type)

        file_path = blob.public_url

        # Save MRI scan data to the database
        mri_scan = MRIScan(
            id=str(uuid.uuid4()),
            patient_id=patient.id,
            uploaded_by=current_user['id'],
            scan_date=datetime.now(timezone.utc),
            file_path=file_path
        )
        db.session.add(mri_scan)
        db.session.commit()  # Make sure to commit after adding MRI scan

        # Trigger ML analysis via external service
        ml_service_url = "https://tumoranger-mlservice-382554775575.asia-southeast2.run.app/predict"  # Fixed URL
        response = requests.post(
            ml_service_url,
            json={"file_url": file_path}
        )
        response.raise_for_status()  # Raise exception for HTTP errors
        ml_result = response.json()

        # Save diagnosis to the database
        diagnosis = Diagnosis(
            id=str(uuid.uuid4()),
            scan_id=mri_scan.id,
            result=ml_result['prediction'],
            confidence_score=ml_result['confidenceScore'],
            diagnosed_by=current_user['id'],
            diagnosis_date=datetime.now(timezone.utc)
        )
        db.session.add(diagnosis)
        db.session.commit()

        return jsonify({
            'message': 'MRI uploaded, analyzed, and saved successfully',
            'diagnosis': ml_result
        }), 200

    except requests.exceptions.RequestException as e:
        app.logger.error(f"ML service error: {e}")
        return jsonify({
            "error": "Failed to analyze MRI using the ML service",
            "details": str(e)
        }), 500

    except Exception as e:
        app.logger.error(f"Unexpected error: {e}")
        return jsonify({
            "error": "An unexpected error occurred",
            "details": str(e)
        }), 500


if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run(host='0.0.0.0', debug=True, port=5050)