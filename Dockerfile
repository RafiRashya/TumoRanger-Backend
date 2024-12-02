# Gunakan image dasar Python
FROM python:3.10-slim

# Set environment variable
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Set working directory
WORKDIR /app

# Salin file requirements.txt
COPY requirements.txt /app/

# Instal dependensi
RUN pip install --no-cache-dir -r requirements.txt

# Salin semua file ke dalam container
COPY . /app/

# Ekspos port Flask
EXPOSE 8080

# Jalankan aplikasi
CMD ["gunicorn", "-w", "2", "-b", "0.0.0.0:8080", "app:app"]