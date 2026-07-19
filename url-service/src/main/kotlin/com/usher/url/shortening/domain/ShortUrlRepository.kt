package com.usher.url.shortening.domain

import java.util.UUID

interface ShortUrlRepository {
    fun existsByShortCode(shortCode: String): Boolean

    fun findByShortCode(shortCode: String): ShortUrl?

    fun findById(id: UUID): ShortUrl?

    fun save(shortUrl: ShortUrl): ShortUrl
}
