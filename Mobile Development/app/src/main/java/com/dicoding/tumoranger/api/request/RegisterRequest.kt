package com.dicoding.tumoranger.api.request

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)