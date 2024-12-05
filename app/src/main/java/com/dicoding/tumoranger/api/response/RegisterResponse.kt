package com.dicoding.tumoranger.api.response

data class RegisterResponse(
    val status: Int,
    val message: String,
    val data: RegisterData?
)

data class RegisterData(
    val account: Account
)

data class Account(
    val name: String,
    val email: String
)