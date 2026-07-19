package com.usher.url.shortening.application

import java.util.UUID

data class CreateShortUrlCommand(
    val ownerId: UUID,
    val originalUrl: String,
)
