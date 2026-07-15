package com.usher.auth.token

import com.usher.auth.user.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.Clock
import java.time.Instant
import java.util.Base64

@Component
class RefreshTokenIssuer(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val refreshTokenHasher: RefreshTokenHasher,
    private val clock: Clock,
    @Value("\${auth.refresh-token.expires-in-seconds:604800}")
    private val expiresInSeconds: Long,
) {
    private val secureRandom = SecureRandom()

    fun issue(user: User): String {
        val token = randomToken()
        refreshTokenRepository.save(
            RefreshToken(
                userId = user.id,
                tokenHash = refreshTokenHasher.hash(token),
                expiresAt = Instant.now(clock).plusSeconds(expiresInSeconds),
            ),
        )
        return token
    }

    private fun randomToken(): String {
        val bytes = ByteArray(64)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
