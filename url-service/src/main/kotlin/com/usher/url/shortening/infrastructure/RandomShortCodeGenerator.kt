package com.usher.url.shortening.infrastructure

import com.usher.url.shortening.domain.ShortCodeGenerator
import org.springframework.stereotype.Component
import java.security.SecureRandom

@Component
class RandomShortCodeGenerator : ShortCodeGenerator {
    private val random = SecureRandom()
    private val alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    override fun generate(): String =
        (1..8)
            .map { alphabet[random.nextInt(alphabet.length)] }
            .joinToString("")
}
