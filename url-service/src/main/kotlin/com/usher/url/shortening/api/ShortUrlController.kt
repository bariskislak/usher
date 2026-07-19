package com.usher.url.shortening.api

import com.usher.url.shortening.application.CreateShortUrlCommand
import com.usher.url.shortening.application.CreateShortUrlService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/urls")
class ShortUrlController(
    private val createShortUrlService: CreateShortUrlService,
    private val ownerIdResolver: OwnerIdResolver,
    @Value("\${url-service.public-base-url:http://localhost:8082}")
    private val publicBaseUrl: String,
) {
    @PostMapping
    fun create(
        @RequestHeader("X-User-Id", required = false) ownerIdHeader: String?,
        @Valid @RequestBody request: CreateShortUrlRequest,
    ): ResponseEntity<CreateShortUrlResponse> {
        val ownerId = ownerIdResolver.resolve(ownerIdHeader)
        val shortUrl = createShortUrlService.create(
            CreateShortUrlCommand(ownerId = ownerId, originalUrl = request.originalUrl),
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(
            CreateShortUrlResponse(
                id = shortUrl.id,
                ownerId = shortUrl.ownerId,
                originalUrl = shortUrl.originalUrl,
                shortCode = shortUrl.shortCode,
                shortUrl = "${publicBaseUrl.trimEnd('/')}/${shortUrl.shortCode}",
                createdAt = shortUrl.createdAt,
            ),
        )
    }
}
