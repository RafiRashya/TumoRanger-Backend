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
- **Method:** `POST`
- **Description:** User Logout.
- **Successful Response:**
    ```json
    {
        "status": "success",
        "message": "Logout successful",
    }
    ```
- **Response Error:**
    ```json
    {
        "status": "internal server error",
        "message": "An internal error occurred. Please try again later.",
        
    }
    ```

---

## **Profile**
- **Endpoint:** `/profile`
- **Method:** `GET`
- **Description:** User Profile.
- **Response:**
    ```json
    {
        "status": "success",
        "result": "User profile retrieved successfully"
    }
    ```

---
## **History Diagnose**
- **Endpoint:** `/diagnose/history`
- **Description:** Diagnose History.
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
- **Method:** `POST`
- **Description:** Diagnose MRI.
- **Response:**
    ```json
    {
        "status": "200",
        "result": "MRI uploaded, analyzed, and saved successfully"
    }
    ```
- **Response Error:**
    ```json
    {
        "status": "Fail",
        "message": "Failed to analyze MRI using the ML service",
        
    }
    ```

