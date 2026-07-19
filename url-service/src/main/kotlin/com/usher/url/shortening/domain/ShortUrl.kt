package com.usher.url.shortening.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "urls")
class ShortUrl(
    @field:Id
    val id: UUID = UUID.randomUUID(),
    @field:Column(name = "owner_id", nullable = false)
    val ownerId: UUID = UUID.randomUUID(),
    @field:Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    val originalUrl: String = "",
    @field:Column(name = "short_code", nullable = false, unique = true, length = 16)
    val shortCode: String = "",
    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false, length = 32)
    val status: ShortUrlStatus = ShortUrlStatus.ACTIVE,
    @field:Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
