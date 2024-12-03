from dotenv import load_dotenv
import os

load_dotenv()

class Config:
    SECRET_KEY = "a6iICzsHcVhNvQx9zWP15O3ERiSYYYwQ"
    SQLALCHEMY_DATABASE_URI = os.getenv('DATABASE_URI')
    GOOGLE_APPLIACTION_CREDENTIALS = os.getenv('GOOGLE_APPLICATION_CREDENTIALS')
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    JWT_SECRET_KEY = os.urandom(24)