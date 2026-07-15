package com.usher.auth.authentication.api

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank
    @field:Email
    @field:Size(max = 320)
    val email: String,
    @field:NotBlank
    val password: String,
)
