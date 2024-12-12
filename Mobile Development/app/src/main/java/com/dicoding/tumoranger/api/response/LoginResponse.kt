package com.dicoding.tumoranger.api.response

data class LoginResponse(
    val status: Int,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val token: String
)