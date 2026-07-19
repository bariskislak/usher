package com.usher.url.shortening.persistence

import com.usher.url.shortening.domain.ShortUrl
import com.usher.url.shortening.domain.ShortUrlRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JpaShortUrlRepositoryAdapter(
    private val jpaShortUrlRepository: JpaShortUrlRepository,
) : ShortUrlRepository {
    override fun existsByShortCode(shortCode: String): Boolean =
        jpaShortUrlRepository.existsByShortCode(shortCode)

    override fun findByShortCode(shortCode: String): ShortUrl? =
        jpaShortUrlRepository.findByShortCode(shortCode)

    override fun findById(id: UUID): ShortUrl? =
        jpaShortUrlRepository.findById(id).orElse(null)

    override fun save(shortUrl: ShortUrl): ShortUrl =
        jpaShortUrlRepository.save(shortUrl)
}
