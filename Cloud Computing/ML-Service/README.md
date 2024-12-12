# **Tumoranger API Documentation**

## **URL**
* Tumoranger ML-SERVICE Base URL
    ```
    https://mlservice-740335101943.asia-southeast2.run.app/
    ```

---

## **Predict**
- **Endpoint:** `/predict`
- **Method:** `POST`
- **Description:** Model Predict to MRI Scan.
- **Request Body (JSON):**
    ```json
    {
        "file_url": "<string: MRI Scan File's Public Link>"
    }
    ```
- **Responses**:
    - **200 OK**: User created successfully.
        ```json
        {
            "prediction": "<string: Model's Classification>",
            "confidenceScore": <int: Model Confidence Score>
        }
        ```
    - **400 Bad Request**: Missing file_url in Request Body.
        ```json
        {
            "error": "file_url is required"
        }
        ```
    - **400 Bad request**: Failed to Download Image.
        ```json
        {
            "error": "Failed to download image: [error message]"
        }
        ```
    - **500 Internal Server Error**: Unexpected Error.
        ```json
        {
            "error": "An unexpected error occurred: [error message]"
        }
        ```
