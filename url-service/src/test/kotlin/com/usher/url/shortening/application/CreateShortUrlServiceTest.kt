package com.usher.url.shortening.application

import com.usher.url.shortening.domain.ShortUrl
import com.usher.url.test.FakeShortUrlRepository
import com.usher.url.test.StubShortCodeGenerator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateShortUrlServiceTest {
    private val ownerId = UUID.fromString("18efadc7-72c2-4cf2-adfa-ce40d2d4e4bb")

    @Test
    fun `create stores a short URL for the owner`() {
        val shortUrlRepository = FakeShortUrlRepository()
        val createShortUrlService = CreateShortUrlService(
            shortUrlRepository = shortUrlRepository,
            shortCodeGenerator = StubShortCodeGenerator(listOf("abc123")),
        )

        val shortUrl = createShortUrlService.create(
            CreateShortUrlCommand(
                ownerId = ownerId,
                originalUrl = " https://example.com/some/path ",
            ),
        )

        assertThat(shortUrl.ownerId).isEqualTo(ownerId)
        assertThat(shortUrl.originalUrl).isEqualTo("https://example.com/some/path")
        assertThat(shortUrl.shortCode).isEqualTo("abc123")
        assertThat(shortUrlRepository.findByShortCode("abc123")).isEqualTo(shortUrl)
    }

    @Test
    fun `create retries when the generated short code already exists`() {
        val shortUrlRepository = FakeShortUrlRepository()
        shortUrlRepository.save(
            ShortUrl(
                ownerId = ownerId,
                originalUrl = "https://existing.example.com",
                shortCode = "taken",
            ),
        )
        val createShortUrlService = CreateShortUrlService(
            shortUrlRepository = shortUrlRepository,
            shortCodeGenerator = StubShortCodeGenerator(listOf("taken", "fresh")),
        )

        val shortUrl = createShortUrlService.create(
            CreateShortUrlCommand(ownerId = ownerId, originalUrl = "https://example.com"),
        )

        assertThat(shortUrl.shortCode).isEqualTo("fresh")
        assertThat(shortUrlRepository.count()).isEqualTo(2)
    }

    @Test
    fun `create rejects a URL without http or https scheme`() {
        val createShortUrlService = CreateShortUrlService(
            shortUrlRepository = FakeShortUrlRepository(),
            shortCodeGenerator = StubShortCodeGenerator(listOf("abc123")),
        )

        assertThatThrownBy {
            createShortUrlService.create(
                CreateShortUrlCommand(ownerId = ownerId, originalUrl = "ftp://example.com"),
            )
        }.isInstanceOf(InvalidOriginalUrlException::class.java)
    }

    @Test
    fun `create fails when a unique short code cannot be generated`() {
        val shortUrlRepository = FakeShortUrlRepository()
        listOf("a", "b", "c", "d", "e").forEach { shortCode ->
            shortUrlRepository.save(
                ShortUrl(
                    ownerId = ownerId,
                    originalUrl = "https://$shortCode.example.com",
                    shortCode = shortCode,
                ),
            )
        }
        val createShortUrlService = CreateShortUrlService(
            shortUrlRepository = shortUrlRepository,
            shortCodeGenerator = StubShortCodeGenerator(listOf("a", "b", "c", "d", "e")),
        )

        assertThatThrownBy {
            createShortUrlService.create(
                CreateShortUrlCommand(ownerId = ownerId, originalUrl = "https://example.com"),
            )
        }.isInstanceOf(ShortCodeGenerationException::class.java)
    }
}
