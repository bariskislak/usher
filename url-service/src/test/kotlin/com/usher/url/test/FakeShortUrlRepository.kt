package com.usher.url.test

import com.usher.url.shortening.domain.ShortUrl
import com.usher.url.shortening.domain.ShortUrlRepository
import java.util.UUID

class FakeShortUrlRepository : ShortUrlRepository {
    private val shortUrlsById = linkedMapOf<UUID, ShortUrl>()

    override fun existsByShortCode(shortCode: String): Boolean =
        shortUrlsById.values.any { it.shortCode == shortCode }

    override fun findByShortCode(shortCode: String): ShortUrl? =
        shortUrlsById.values.firstOrNull { it.shortCode == shortCode }

    override fun findById(id: UUID): ShortUrl? =
        shortUrlsById[id]

    override fun save(shortUrl: ShortUrl): ShortUrl {
        shortUrlsById[shortUrl.id] = shortUrl
        return shortUrl
    }

    fun count(): Int = shortUrlsById.size
}
