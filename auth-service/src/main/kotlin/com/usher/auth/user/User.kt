package com.usher.auth.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @field:Id
    val id: UUID = UUID.randomUUID(),
    @field:Column(nullable = false, unique = true, length = 320)
    val email: String = "",
    @field:Column(name = "password_hash", nullable = false, length = 255)
    val passwordHash: String = "",
    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false, length = 32)
    val role: UserRole = UserRole.USER,
    @field:Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
