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
- **Successful Response:**
    ```json
    {
        "status": "success",
        "message": "User created successfully",
    }
    ```
- **response when user already exists:**
    ```json
    {
        "status": "fail",
        "message": "Email or user already exists"
    }
    ```

---
## **LOGIN**
- **Endpoint:** `/auth/login`
- **Method:** `POST`
- **Description:** Akses login user.
- **Response:**
    ```json
    {
        "status": "success",
        "message": "Login successful"
    }
    ```
- **Login Failed Response:**
   ```json
    {
        "status": "fail",
        "message": "Email or password is incorrect"
    }
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
- **Method:** `GET`
- **Description:** Diagnose History.
- **Response:**
    ```json
    {
        "status": "200",
        "result": "string"
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

