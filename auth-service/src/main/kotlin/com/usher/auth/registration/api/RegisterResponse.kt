package com.usher.auth.registration.api

import com.usher.auth.user.UserRole
import java.time.Instant
import java.util.UUID

data class RegisterResponse(
    val id: UUID,
    val email: String,
    val role: UserRole,
    val createdAt: Instant,
)
