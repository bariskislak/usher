package com.usher.url.shortening.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.usher.url.shortening.application.CreateShortUrlService
import com.usher.url.test.FakeShortUrlRepository
import com.usher.url.test.StubShortCodeGenerator
import com.usher.url.web.ApiExceptionHandler
import org.hamcrest.Matchers.blankOrNullString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class ShortUrlEndpointContractTest {
    private val objectMapper = jacksonObjectMapper()
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        val createShortUrlService = CreateShortUrlService(
            shortUrlRepository = FakeShortUrlRepository(),
            shortCodeGenerator = StubShortCodeGenerator(listOf("abc123")),
        )

        mockMvc = MockMvcBuilders
            .standaloneSetup(
                ShortUrlController(
                    createShortUrlService = createShortUrlService,
                    ownerIdResolver = OwnerIdResolver(),
                    publicBaseUrl = "http://localhost:8082",
                ),
            )
            .setControllerAdvice(ApiExceptionHandler())
            .build()
    }

    @Test
    fun `POST urls creates a short URL`() {
        mockMvc.post("/urls") {
            header("X-User-Id", "18efadc7-72c2-4cf2-adfa-ce40d2d4e4bb")
            contentType = MediaType.APPLICATION_JSON
            content = json("originalUrl" to "https://example.com/some/path")
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(not(blankOrNullString())) }
            jsonPath("$.ownerId") { value("18efadc7-72c2-4cf2-adfa-ce40d2d4e4bb") }
            jsonPath("$.originalUrl") { value("https://example.com/some/path") }
            jsonPath("$.shortCode") { value("abc123") }
            jsonPath("$.shortUrl") { value("http://localhost:8082/abc123") }
            jsonPath("$.createdAt") { value(not(blankOrNullString())) }
        }
    }

    @Test
    fun `POST urls rejects missing owner id`() {
        mockMvc.post("/urls") {
            contentType = MediaType.APPLICATION_JSON
            content = json("originalUrl" to "https://example.com")
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("missing_owner_id") }
        }
    }

    @Test
    fun `POST urls rejects invalid owner id`() {
        mockMvc.post("/urls") {
            header("X-User-Id", "not-a-uuid")
            contentType = MediaType.APPLICATION_JSON
            content = json("originalUrl" to "https://example.com")
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("invalid_owner_id") }
        }
    }

    @Test
    fun `POST urls rejects invalid original URL`() {
        mockMvc.post("/urls") {
            header("X-User-Id", "18efadc7-72c2-4cf2-adfa-ce40d2d4e4bb")
            contentType = MediaType.APPLICATION_JSON
            content = json("originalUrl" to "not-a-url")
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.code") { value("invalid_original_url") }
        }
    }

    private fun json(vararg fields: Pair<String, Any>): String =
        objectMapper.writeValueAsString(mapOf(*fields))
}
