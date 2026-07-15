package com.usher.auth.registration.api

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank
    @field:Email
    @field:Size(max = 320)
    val email: String,
    @field:Size(min = 8, max = 64)
    val password: String,
)
