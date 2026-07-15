package com.usher.auth.authentication.api

import com.usher.auth.user.UserRole
import java.time.Instant
import java.util.UUID

data class MeResponse(
    val id: UUID,
    val email: String,
    val role: UserRole,
    val createdAt: Instant,
)
