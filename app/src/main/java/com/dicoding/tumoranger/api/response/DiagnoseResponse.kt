package com.dicoding.tumoranger.api.response

data class DiagnoseResponse(
    val message: String,
    val diagnosis: Diagnosis?
)

data class Diagnosis(
    val prediction: String,
    val confidenceScore: Double
)