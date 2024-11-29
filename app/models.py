from flask_sqlalchemy import SQLAlchemy
from datetime import datetime, timezone

db = SQLAlchemy()

# class User(db.Model):
#     id = db.Column(db.Integer, primary_key=True)
#     username = db.Column(db.String(50), unique=True, nullable=False)
#     email = db.Column(db.String(120), unique=True, nullable=False)
#     password = db.Column(db.String(60), nullable=False)

class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.String(255), primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    email = db.Column(db.String(100), nullable=False, unique=True)
    password_hash = db.Column(db.String(255), nullable=False)
    # role = db.Column(db.Enum('doctor', 'admin'), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

class Patient(db.Model):
    __tablename__ = 'patients'
    id = db.Column(db.String(255), primary_key=True)
    user_id = db.Column(db.String(255), db.ForeignKey('users.id'))
    name = db.Column(db.String(100), nullable=False)
    birthdate = db.Column(db.Date, nullable=False)
    gender = db.Column(db.Enum('male', 'female'), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.now(timezone.utc))

class MRIScan(db.Model):
    __tablename__ = 'mri_scans'
    id = db.Column(db.String(255), primary_key=True)
    patient_id = db.Column(db.String(255), db.ForeignKey('patients.id'))
    uploaded_by = db.Column(db.String(255), db.ForeignKey('users.id'))
    scan_date = db.Column(db.DateTime, nullable=False)
    file_path = db.Column(db.Text, nullable=False)
    # status = db.Column(db.Enum('pending', 'processed', 'error'), default='pending')
    created_at = db.Column(db.DateTime, default=datetime.now(timezone.utc))

class Diagnosis(db.Model):
    __tablename__ = 'diagnoses'
    id = db.Column(db.String(255), primary_key=True)
    scan_id = db.Column(db.String(255), db.ForeignKey('mri_scans.id'))
    result = db.Column(db.Text, nullable=False)
    confidence_score = db.Column(db.Float, nullable=False)
    diagnosed_by = db.Column(db.String(255), db.ForeignKey('users.id'))
    diagnosis_date = db.Column(db.DateTime, default=datetime.now(timezone.utc))