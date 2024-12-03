# Gunakan Python sebagai base image
FROM python:3.9-slim

# Tetapkan working directory di dalam container
WORKDIR /app

# Salin file requirements ke dalam container
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Salin seluruh source code aplikasi ke dalam container
COPY . .
COPY test-ml-tumoranger-22abe0f36aa2.json /app/

# Set environment variable agar Flask berjalan di production
ENV FLASK_ENV=production
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/test-ml-tumoranger-22abe0f36aa2.json
ENV DATABASE_URI=mysql+pymysql://root:123@db_container:3306/tumoranger_db

# Port yang akan digunakan oleh container
EXPOSE 8080

# Perintah untuk menjalankan aplikasi menggunakan Gunicorn
CMD ["gunicorn", "-b", "0.0.0.0:8080", "app.app:app"]
