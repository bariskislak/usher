package com.usher.auth.authentication.application

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)
