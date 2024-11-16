from flask import Flask, request, jsonify
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

app = Flask(__name__)
app.config.from_object(Config)

db.init_app(app)
jwt = JWTManager(app)

blacklist = set()

@jwt.token_in_blocklist_loader
def check_if_token_in_blacklist(jwt_header, jwt_payload):
    jti = jwt_payload["jti"]  # Unique identifier for the JWT
    return jti in blacklist

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

if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run(host='0.0.0.0', debug=True)