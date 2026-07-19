package com.usher.url.shortening.application

import com.usher.common.messaging.EventPublisher
import com.usher.url.shortening.domain.ShortUrlRepository
import com.usher.url.shortening.domain.ShortUrlStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class ResolveShortUrlService(
    private val shortUrlRepository: ShortUrlRepository,
    private val eventPublisher: EventPublisher,
    private val clock: Clock,
) {
    @Transactional(readOnly = true)
    fun resolve(query: ResolveShortUrlQuery): String {
        val shortUrl = shortUrlRepository.findByShortCode(query.shortCode)
            ?: throw ShortUrlNotFoundException()

        if (shortUrl.status != ShortUrlStatus.ACTIVE) {
            throw ShortUrlDisabledException()
        }

        eventPublisher.publish(
            TOPIC,
            UrlClickedEvent(
                occurredAt = Instant.now(clock),
                shortUrlId = shortUrl.id,
                ownerId = shortUrl.ownerId,
                shortCode = shortUrl.shortCode,
                originalUrl = shortUrl.originalUrl,
            ),
        )

        return shortUrl.originalUrl
    }

    private companion object {
        const val TOPIC = "url.clicked"
    }
}
