package com.usher.auth.registration.application

data class RegisterUserCommand(
    val email: String,
    val password: String,
)
