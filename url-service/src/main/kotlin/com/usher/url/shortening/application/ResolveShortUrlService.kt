package com.usher.url.shortening.application

import com.usher.url.shortening.domain.ShortUrlRepository
import com.usher.url.shortening.domain.ShortUrlStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ResolveShortUrlService(
    private val shortUrlRepository: ShortUrlRepository,
) {
    @Transactional(readOnly = true)
    fun resolve(query: ResolveShortUrlQuery): String {
        val shortUrl = shortUrlRepository.findByShortCode(query.shortCode)
            ?: throw ShortUrlNotFoundException()

        if (shortUrl.status != ShortUrlStatus.ACTIVE) {
            throw ShortUrlDisabledException()
        }

        return shortUrl.originalUrl
    }
}
