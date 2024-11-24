import os
from werkzeug.utils import secure_filename
from flask import Flask, request, jsonify, send_from_directory
from flask_jwt_extended import (
    JWTManager,
    create_access_token,
    jwt_required,
    get_jwt,
    get_jwt_identity,
)
from werkzeug.security import generate_password_hash, check_password_hash
from models import User, db
from config import Config
from dicomweb_client import DICOMwebClient

# Initialize Flask app
app = Flask(__name__)
app.config.from_object(Config)

# Database setup
db.init_app(app)

# JWT Manager setup
jwt = JWTManager(app)

# Set to store blacklisted JWTs
blacklist = set()

# JWT token blacklist checker
@jwt.token_in_blocklist_loader
def check_if_token_in_blacklist(jwt_header, jwt_payload):
    jti = jwt_payload["jti"]  # Unique identifier for the JWT
    return jti in blacklist

# Directory to store MRI scan files
UPLOAD_FOLDER = 'uploads/mri_scans'
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['ALLOWED_EXTENSIONS'] = {'dcm', 'jpg', 'jpeg', 'png', 'gif'}

# Ensure the upload folder exists
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

# Helper function to check allowed file extensions
def allowed_file(filename):
    """Helper function to check file extension"""
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in app.config['ALLOWED_EXTENSIONS']


# Authentication Routes
@app.route("/auth/register", methods=["POST"])
def register():
    try:
        data = request.json

        username = data["username"]
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
        new_user = User(email=email, username=username, password=hashed_password)
        db.session.add(new_user)
        db.session.commit()

        response_data = {
            "status": 200,
            "message": "User created successfully",
            "data": {"account": {"username": username, "email": email}},
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

        if user is None or not check_password_hash(user.password, password):
            response_data = {
                "status": 401,
                "message": "Email or password is incorrect",
                "data": None,
            }
            return jsonify(response_data), 401

        token = create_access_token(
            identity={"username": user.username, "email": user.email}
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
                "message": f"Hello, {current_user['username']}! Welcome to your dashboard."
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


# MRI Scan Routes
@app.route("/mri/upload", methods=["POST"])
@jwt_required()
def upload_mri_scan():
    try:
        if 'file' not in request.files:
            response_data = {"status": 400, "message": "No file part"}
            return jsonify(response_data), 400

        file = request.files['file']
        if file.filename == '':
            response_data = {"status": 400, "message": "No selected file"}
            return jsonify(response_data), 400

        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            file.save(file_path)

            # Save metadata to the database if needed
            # For example, you could save the file path and user info to your database

            response_data = {
                "status": 200,
                "message": "MRI scan uploaded successfully",
                "data": {"filename": filename, "file_path": file_path},
            }
            return jsonify(response_data), 200

        else:
            response_data = {"status": 400, "message": "File type not allowed"}
            return jsonify(response_data), 400

    except Exception as e:
        response_data = {"status": 500, "message": f"Error Reason: {str(e)}"}
        return jsonify(response_data), 500


@app.route("/mri/download/<filename>", methods=["GET"])
@jwt_required()
def download_mri_scan(filename):
    try:
        file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        
        if os.path.exists(file_path):
            return send_from_directory(app.config['UPLOAD_FOLDER'], filename)
        else:
            response_data = {"status": 404, "message": "File not found"}
            return jsonify(response_data), 404

    except Exception as e:
        response_data = {"status": 500, "message": f"Error Reason: {str(e)}"}
        return jsonify(response_data), 500


@app.route("/mri/process/<filename>", methods=["POST"])
@jwt_required()
def process_mri_scan(filename):
    try:
        file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)

        if not os.path.exists(file_path):
            response_data = {"status": 404, "message": "MRI scan file not found"}
            return jsonify(response_data), 404
        
        # Example of processing - in a real case, this could involve analyzing the MRI scan
        # e.g., using machine learning or image processing algorithms.
        
        # For simplicity, let's assume the process involves just returning a dummy analysis result.
        # You can integrate real processing or ML models here.
        
        analysis_result = {
            "scan_id": filename,
            "analysis": "No abnormalities detected",  # Placeholder result
            "confidence": 98.7  # Example confidence level for analysis
        }

        response_data = {
            "status": 200,
            "message": "MRI scan processed successfully",
            "data": analysis_result,
        }
        return jsonify(response_data), 200

    except Exception as e:
        response_data = {"status": 500, "message": f"Error Reason: {str(e)}"}
        return jsonify(response_data), 500


# DICOMweb Client Integration Routes

# DICOMweb Client Setup
dicomweb_client = DICOMwebClient("https://your-dicomweb-server-url")

@app.route("/mri/dicomweb/upload", methods=["POST"])
@jwt_required()
def dicomweb_upload():
    try:
        if 'file' not in request.files:
            response_data = {"status": 400, "message": "No file part"}
            return jsonify(response_data), 400

        file = request.files['file']
        if file.filename == '':
            response_data = {"status": 400, "message": "No selected file"}
            return jsonify(response_data), 400

        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            dicomweb_client.store_instance(file)
            response_data = {
                "status": 200,
                "message": "DICOM file uploaded to DICOMweb server",
            }
            return jsonify(response_data), 200

        else:
            response_data = {"status": 400, "message": "File type not allowed"}
            return jsonify(response_data), 400

    except Exception as e:
        response_data = {"status": 500, "message": f"Error Reason: {str(e)}"}
        return jsonify(response_data), 500


@app.route("/mri/dicomweb/download/<instance_uid>", methods=["GET"])
@jwt_required()
def dicomweb_download(instance_uid):
    try:
        dicom_file = dicomweb_client.retrieve_instance(instance_uid)
        return dicom_file

    except Exception as e:
        response_data = {"status": 500, "message": f"Error Reason: {str(e)}"}
        return jsonify(response_data), 500


# Running the Flask app
if __name__ == '__main__':
    with app.app_context():
        db.create_all()  # Create the tables in the database
    app.run(host='0.0.0.0', debug=True)
