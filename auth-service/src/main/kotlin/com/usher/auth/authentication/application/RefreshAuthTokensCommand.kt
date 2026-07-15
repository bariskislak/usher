package com.usher.auth.authentication.application

data class RefreshAuthTokensCommand(
    val refreshToken: String,
)
