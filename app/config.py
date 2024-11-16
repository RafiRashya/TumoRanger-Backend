import os

class Config:
    SECRET_KEY = os.urandom(24)
    SQLALCHEMY_DATABASE_URI = "sqlite:///user.db"
    JWT_SECRET_KEY = os.urandom(24)