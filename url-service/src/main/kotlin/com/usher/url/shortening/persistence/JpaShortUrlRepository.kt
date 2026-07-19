package com.usher.url.shortening.persistence

import com.usher.url.shortening.domain.ShortUrl
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaShortUrlRepository : JpaRepository<ShortUrl, UUID> {
    fun existsByShortCode(shortCode: String): Boolean

    fun findByShortCode(shortCode: String): ShortUrl?
}
