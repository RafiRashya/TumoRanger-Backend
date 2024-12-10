package com.dicoding.tumoranger.api.response

data class DiagnosisHistoryResponse(
    val data: List<DiagnosisHistoryItem>,
    val message: String
)

data class DiagnosisHistoryItem(
    val patient_name: String,
    val gender: String,
    val birthdate: String,
    val result: String,
    val confidence_score: Float,
    val file_path: String,
    val diagnosis_date: String
)