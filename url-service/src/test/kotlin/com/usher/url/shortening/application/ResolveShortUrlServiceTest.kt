package com.usher.url.shortening.application

import com.usher.url.shortening.domain.ShortUrl
import com.usher.url.shortening.domain.ShortUrlStatus
import com.usher.url.test.FakeShortUrlRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.UUID

class ResolveShortUrlServiceTest {
    private val ownerId = UUID.fromString("18efadc7-72c2-4cf2-adfa-ce40d2d4e4bb")

    @Test
    fun `resolve returns the original URL for an active short URL`() {
        val shortUrlRepository = FakeShortUrlRepository()
        shortUrlRepository.save(
            ShortUrl(
                ownerId = ownerId,
                originalUrl = "https://example.com/some/path",
                shortCode = "abc123",
            ),
        )
        val resolveShortUrlService = ResolveShortUrlService(shortUrlRepository)

        val originalUrl = resolveShortUrlService.resolve(
            ResolveShortUrlQuery(shortCode = "abc123"),
        )

        assertThat(originalUrl).isEqualTo("https://example.com/some/path")
    }

    @Test
    fun `resolve rejects an unknown short code`() {
        val resolveShortUrlService = ResolveShortUrlService(FakeShortUrlRepository())

        assertThatThrownBy {
            resolveShortUrlService.resolve(
                ResolveShortUrlQuery(shortCode = "missing"),
            )
        }.isInstanceOf(ShortUrlNotFoundException::class.java)
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
        val resolveShortUrlService = ResolveShortUrlService(shortUrlRepository)

        assertThatThrownBy {
            resolveShortUrlService.resolve(
                ResolveShortUrlQuery(shortCode = "disabled"),
            )
        }.isInstanceOf(ShortUrlDisabledException::class.java)
    }
}
