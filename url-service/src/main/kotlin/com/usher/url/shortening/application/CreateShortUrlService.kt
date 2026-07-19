package com.usher.url.shortening.application

import com.usher.url.shortening.domain.ShortCodeGenerator
import com.usher.url.shortening.domain.ShortUrl
import com.usher.url.shortening.domain.ShortUrlRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Service
class CreateShortUrlService(
    private val shortUrlRepository: ShortUrlRepository,
    private val shortCodeGenerator: ShortCodeGenerator,
) {
    @Transactional
    fun create(command: CreateShortUrlCommand): ShortUrl {
        val normalizedOriginalUrl = normalizeUrl(command.originalUrl)
        val shortCode = generateUniqueShortCode()

        return shortUrlRepository.save(
            ShortUrl(
                ownerId = command.ownerId,
                originalUrl = normalizedOriginalUrl,
                shortCode = shortCode,
            ),
        )
    }

    private fun normalizeUrl(originalUrl: String): String {
        val trimmedUrl = originalUrl.trim()
        val uri = try {
            URI(trimmedUrl)
        } catch (ex: Exception) {
            throw InvalidOriginalUrlException()
        }

        if ((uri.scheme != "http" && uri.scheme != "https") || uri.host.isNullOrBlank()) {
            throw InvalidOriginalUrlException()
        }

        return uri.toString()
    }

    private fun generateUniqueShortCode(): String {
        repeat(MAX_CODE_GENERATION_ATTEMPTS) {
            val shortCode = shortCodeGenerator.generate()
            if (!shortUrlRepository.existsByShortCode(shortCode)) {
                return shortCode
            }
        }

        throw ShortCodeGenerationException()
    }

    private companion object {
        const val MAX_CODE_GENERATION_ATTEMPTS = 5
    }
}
