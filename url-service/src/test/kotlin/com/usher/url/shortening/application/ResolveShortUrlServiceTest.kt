package com.usher.url.shortening.application

import com.usher.url.shortening.domain.ShortUrl
import com.usher.url.shortening.domain.ShortUrlStatus
import com.usher.url.test.FakeEventPublisher
import com.usher.url.test.FakeShortUrlRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

class ResolveShortUrlServiceTest {
    private val ownerId = UUID.fromString("18efadc7-72c2-4cf2-adfa-ce40d2d4e4bb")
    private val clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)

    @Test
    fun `resolve returns the original URL and publishes a click event for an active short URL`() {
        val shortUrlRepository = FakeShortUrlRepository()
        val shortUrl = shortUrlRepository.save(
            ShortUrl(
                ownerId = ownerId,
                originalUrl = "https://example.com/some/path",
                shortCode = "abc123",
            ),
        )
        val eventPublisher = FakeEventPublisher()
        val resolveShortUrlService = ResolveShortUrlService(shortUrlRepository, eventPublisher, clock)

        val originalUrl = resolveShortUrlService.resolve(
            ResolveShortUrlQuery(shortCode = "abc123"),
        )

        assertThat(originalUrl).isEqualTo("https://example.com/some/path")
        val publishedEvent = eventPublisher.publishedEvents().single()
        assertThat(publishedEvent.topic).isEqualTo("url.clicked")
        assertThat(publishedEvent.event).isInstanceOf(UrlClickedEvent::class.java)

        val urlClickedEvent = publishedEvent.event as UrlClickedEvent
        assertThat(urlClickedEvent.occurredAt).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"))
        assertThat(urlClickedEvent.shortUrlId).isEqualTo(shortUrl.id)
        assertThat(urlClickedEvent.ownerId).isEqualTo(ownerId)
        assertThat(urlClickedEvent.shortCode).isEqualTo("abc123")
        assertThat(urlClickedEvent.originalUrl).isEqualTo("https://example.com/some/path")
    }

    @Test
    fun `resolve rejects an unknown short code`() {
        val eventPublisher = FakeEventPublisher()
        val resolveShortUrlService = ResolveShortUrlService(FakeShortUrlRepository(), eventPublisher, clock)

        assertThatThrownBy {
            resolveShortUrlService.resolve(
                ResolveShortUrlQuery(shortCode = "missing"),
            )
        }.isInstanceOf(ShortUrlNotFoundException::class.java)
        assertThat(eventPublisher.publishedEvents()).isEmpty()
    }

    @Test
    fun `resolve rejects a disabled short URL`() {
        val shortUrlRepository = FakeShortUrlRepository()
        shortUrlRepository.save(
            ShortUrl(
                ownerId = ownerId,
                originalUrl = "https://example.com/some/path",
                shortCode = "disabled",
                status = ShortUrlStatus.DISABLED,
            ),
        )
        val eventPublisher = FakeEventPublisher()
        val resolveShortUrlService = ResolveShortUrlService(shortUrlRepository, eventPublisher, clock)

        assertThatThrownBy {
            resolveShortUrlService.resolve(
                ResolveShortUrlQuery(shortCode = "disabled"),
            )
        }.isInstanceOf(ShortUrlDisabledException::class.java)
        assertThat(eventPublisher.publishedEvents()).isEmpty()
    }
}
