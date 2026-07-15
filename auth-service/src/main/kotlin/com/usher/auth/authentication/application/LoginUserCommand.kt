package com.usher.auth.authentication.application

data class LoginUserCommand(
    val email: String,
    val password: String,
)
