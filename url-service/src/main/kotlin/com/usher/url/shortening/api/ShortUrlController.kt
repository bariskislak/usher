package com.usher.url.shortening.api

import com.usher.url.shortening.application.CreateShortUrlCommand
import com.usher.url.shortening.application.CreateShortUrlService
import com.usher.url.shortening.application.ResolveShortUrlQuery
import com.usher.url.shortening.application.ResolveShortUrlService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class ShortUrlController(
    private val createShortUrlService: CreateShortUrlService,
    private val resolveShortUrlService: ResolveShortUrlService,
    private val ownerIdResolver: OwnerIdResolver,
    @Value("\${url-service.public-base-url:http://localhost:8082}")
    private val publicBaseUrl: String,
) {
    @PostMapping("/urls")
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

    @GetMapping("/{shortCode}")
    fun redirect(@PathVariable shortCode: String): ResponseEntity<Void> {
        val originalUrl = resolveShortUrlService.resolve(ResolveShortUrlQuery(shortCode = shortCode))

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(originalUrl))
            .build()
    }
}
