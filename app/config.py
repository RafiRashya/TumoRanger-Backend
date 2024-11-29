from dotenv import load_dotenv
import os

load_dotenv()

class Config:
    SECRET_KEY = os.urandom(24)
    SQLALCHEMY_DATABASE_URI = os.getenv('DATABASE_URI')
    GOOGLE_APPLIACTION_CREDENTIALS = os.getenv('CREDENTIALS')
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    JWT_SECRET_KEY = os.urandom(24)