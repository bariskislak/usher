package com.usher.url.shortening.api

import java.time.Instant
import java.util.UUID

data class CreateShortUrlResponse(
    val id: UUID,
    val ownerId: UUID,
    val originalUrl: String,
    val shortCode: String,
    val shortUrl: String,
    val createdAt: Instant,
)
