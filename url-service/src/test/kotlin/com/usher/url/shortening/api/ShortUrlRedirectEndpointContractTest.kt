package com.usher.url.shortening.api

import com.usher.url.shortening.application.CreateShortUrlService
import com.usher.url.shortening.application.ResolveShortUrlService
import com.usher.url.shortening.domain.ShortUrl
import com.usher.url.shortening.domain.ShortUrlStatus
import com.usher.url.test.FakeEventPublisher
import com.usher.url.test.FakeShortUrlRepository
import com.usher.url.test.StubShortCodeGenerator
import com.usher.url.web.ApiExceptionHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.Clock
import java.util.UUID

class ShortUrlRedirectEndpointContractTest {
    private lateinit var shortUrlRepository: FakeShortUrlRepository
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        shortUrlRepository = FakeShortUrlRepository()
        val createShortUrlService = CreateShortUrlService(
            shortUrlRepository = shortUrlRepository,
            shortCodeGenerator = StubShortCodeGenerator(listOf("unused")),
        )
        val resolveShortUrlService = ResolveShortUrlService(
            shortUrlRepository = shortUrlRepository,
            eventPublisher = FakeEventPublisher(),
            clock = Clock.systemUTC(),
        )

        mockMvc = MockMvcBuilders
            .standaloneSetup(
                ShortUrlController(
                    createShortUrlService = createShortUrlService,
                    resolveShortUrlService = resolveShortUrlService,
                    ownerIdResolver = OwnerIdResolver(),
                    publicBaseUrl = "http://localhost:8082",
                ),
            )
            .setControllerAdvice(ApiExceptionHandler())
            .build()
    }

    @Test
    fun `GET short code redirects to the original URL`() {
        shortUrlRepository.save(
            ShortUrl(
                ownerId = ownerId,
                originalUrl = "https://example.com/some/path",
                shortCode = "abc123",
            ),
        )

        mockMvc.get("/abc123")
            .andExpect {
                status { isFound() }
                header { string("Location", "https://example.com/some/path") }
            }
    }

    @Test
    fun `GET short code returns not found for unknown short code`() {
        mockMvc.get("/missing")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.code") { value("short_url_not_found") }
            }
    }

    @Test
    fun `GET short code rejects a disabled short URL`() {
        shortUrlRepository.save(
            ShortUrl(
                ownerId = ownerId,
                originalUrl = "https://example.com/some/path",
                shortCode = "disabled",
                status = ShortUrlStatus.DISABLED,
            ),
        )

        mockMvc.get("/disabled")
            .andExpect {
                status { isGone() }
                jsonPath("$.code") { value("short_url_disabled") }
            }
    }

    private companion object {
        val ownerId: UUID = UUID.fromString("18efadc7-72c2-4cf2-adfa-ce40d2d4e4bb")
    }
}
