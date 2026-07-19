package com.usher.url.shortening.api

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateShortUrlRequest(
    @field:NotBlank
    @field:Size(max = 2048)
    val originalUrl: String,
)
