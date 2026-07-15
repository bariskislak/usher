package com.usher.auth.token

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
    @field:Id
    val id: UUID = UUID.randomUUID(),
    @field:Column(name = "user_id", nullable = false)
    val userId: UUID = UUID.randomUUID(),
    @field:Column(name = "token_hash", nullable = false, unique = true, length = 128)
    val tokenHash: String = "",
    @field:Column(name = "expires_at", nullable = false)
    val expiresAt: Instant = Instant.now(),
    @field:Column(name = "revoked_at")
    var revokedAt: Instant? = null,
    @field:Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
