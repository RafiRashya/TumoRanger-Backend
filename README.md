# **Tumoranger API Documentation**

## **URL**
* Tumoranger API Base URL
    ```
    http://34.101.146.92:8080
    ```

---

## **Table of Contents**
- [Register](#register)
- [Login](#login)
- [Logout](#logout)
- [Profile](#profile)
- [History Diagnose](#history-diagnose)
- [Diagnose](#diagnose)
---

## Authentication
All endpoints except for registration and login require a valid JWT token in the Authorization header.

### Example Header
Authorization: Bearer `<JWT_Token>`

## **Register**
- **Endpoint:** `/auth/register`
- **Method:** `POST`
- **Description:** Mendaftarkan user baru.
- **Request Body (JSON):**
    ```json
    {
        "name":"string",
        "email":"string",
        "password":"string"

    }
    ```
- **Responses**:
    - **200 OK**: User created successfully.
        ```json
        {
            "status": 200,
            "message": "User created successfully",
            "data": {
                "account": {
                    "name": "string>",
                    "email": "string"
                }
            }
        }
        ```
    - **400 Bad Request**: Email or user already exists.
        ```json
        {
            "status": 400,
            "message": "Email or user already exists"
        }
        ```
    - **500 Internal Server Error**: Error occurred during registration.
        ```json
        {
            "status": 500,
            "message": "Error Reason: <error-message>"
        }
        ```

---
## **LOGIN**
- **Endpoint:** `/auth/login`
- **Method:** `POST`
- **Description:** Akses login user.
- **Request Body**:
    ```json
    {
        "email": "string",
        "password": "string"
    }
    ```
- **Responses**:
    - **200 OK**: Login successful.
        ```json
        {
            "status": 200,
            "message": "Login successful",
            "data": {
                "token": "<JWT_Token>"
            }
        }
        ```
    - **401 Unauthorized**: Email or password is incorrect.
        ```json
        {
            "status": 401,
            "message": "Email or password is incorrect",
            "data": null
        }
        ```
    - **500 Internal Server Error**: Error occurred during login.
        ```json
        {
            "status": 500,
            "message": "Error Reason: <error-message>"
        }
        ```
---

## **Logout**
- **Endpoint:** `/auth/logout`
- **Description:** User Logout.
- **Method:** `POST`
- **Authentication**: Required
- **Responses**:
    - **200 OK**: Logout successful.
        ```json
        {
            "status": 200,
            "message": "Logout successful",
            "data": null
        }
        ```
    - **500 Internal Server Error**: Error occurred during logout.
        ```json
        {
            "status": 500,
            "message": "An internal error occurred. Please try again later."
        }
        ```

---

## **Profile**
- **Endpoint:** `/profile`
- **Description:** User Profile.
- **Method:** `GET`
- **Authentication**: Required
- **Responses**:
    - **200 OK**: User profile retrieved successfully.
        ```json
        {
            "status": 200,
            "message": "User profile retrieved successfully",
            "data": {
                "username": "<username>",
                "email": "<email>"
            }
        }
        ```
    - **500 Internal Server Error**: Error occurred while retrieving profile.
        ```json
        {
            "status": 500,
            "message": "Error Reason: <error-message>"
        }
        ```

---
## **History Diagnose**
- **Endpoint:** `/diagnose/history`
- **Description:** Doctor's diagnose histories.
- **Method:** `GET`
- **Authentication**: Required
- **Responses**:
    - **200 OK**: Diagnosis history retrieved successfully.
        ```json
        {
            "data": [
                {
                    "patient_name": "<patient-name>",
                    "gender": "<gender>",
                    "birthdate": "<birthdate>",
                    "result": "<result>",
                    "confidence_score": <confidence-score>,
                    "file_path": "<file-path>",
                    "diagnosis_date": "<diagnosis-date>"
                }
            ],
            "message": "Diagnosis Historical data has been successfully retrieved."
        }
        ```
## **Diagnose**
- **Endpoint:** `/diagnose`
- **Description:** Diagnose MRI.
- **Method:** `POST`
- **Authentication**: Required
- **Request Body**: (Form-data)
    - `file`: MRI file
    - `patient_name`: Name of the patient
    - `birthdate`: Patient's birthdate (YYYY-MM-DD)
    - `gender`: Patient's gender
- **Responses**:
    - **200 OK**: MRI uploaded and analyzed successfully.
        ```json
        {
            "message": "MRI uploaded, analyzed, and saved successfully",
            "diagnosis": {
                "prediction": "<prediction>",
                "confidenceScore": <confidence-score>
            }
        }
        ```
    - **500 Internal Server Error**: Error occurred during MRI upload.
        ```json
        {
            "error": "An unexpected error occurred",
            "details": "<error-message>"
        }
        ```

