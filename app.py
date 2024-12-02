from flask import Flask, request, jsonify
import numpy as np
from PIL import Image
import tensorflow as tf
import os
import requests
import io

os.environ['CUDA_VISIBLE_DEVICES'] = '0'

# Load the ML model
MODEL_PATH = "model/model_mri_scan.h5"
model = tf.keras.models.load_model(MODEL_PATH)

# Flask app initialization
app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Dapatkan public link gambar dari request JSON
        data = request.get_json()
        file_url = data.get('file_url')
        if not file_url:
            return jsonify({'error': 'file_url is required'}), 400

        # Download gambar dari public link
        try:
            response = requests.get(file_url, stream=True, timeout=10)
            response.raise_for_status()  # Raise exception for HTTP errors
        except requests.exceptions.RequestException as e:
            return jsonify({'error': f'Failed to download image: {str(e)}'}), 400

        try:
            img = Image.open(io.BytesIO(response.content)).convert('RGB')
        except Exception as e:
            return jsonify({'error': f'Failed to process image: {str(e)}'}), 400

        # Preprocessing gambar
        try:
            img = img.resize((299, 299))
            img = np.asarray(img)
            img = np.expand_dims(img, axis=0)
            img = img / 255
        except Exception as e:
            return jsonify({'error': f'Error during image preprocessing: {str(e)}'}), 500

        # Prediksi menggunakan model
        try:
            prediction = model.predict(img)
        except Exception as e:
            return jsonify({'error': f'Model prediction failed: {str(e)}'}), 500

        # Mendapatkan confidence score
        confidence_score = np.max(prediction)
        confidence_percentage = round(confidence_score * 100)

        # Mendapatkan predicted class
        class_labels = ['glioma', 'meningioma', 'notumor', 'pituitary']
        predicted_class = class_labels[np.argmax(prediction)]

        # Kembalikan hasil prediksi dalam format JSON
        return jsonify({'prediction': predicted_class, 'confidenceScore': confidence_percentage})

    except Exception as e:
        # Tangkap error yang tidak terduga
        return jsonify({'error': f'An unexpected error occurred: {str(e)}'}), 500