package com.usher.auth.token

import com.usher.auth.authentication.application.InvalidAccessTokenException
import com.usher.auth.user.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.time.Instant
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtAccessTokenService(
    @Value("\${auth.jwt.secret:usher-local-development-secret-must-be-at-least-32-bytes}")
    private val secret: String,
    @Value("\${auth.jwt.expires-in-seconds:900}")
    override val expiresInSeconds: Long,
    private val clock: Clock,
) : AccessTokenService {
    private val signingKey: SecretKey
        get() = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    override fun issue(user: User): String {
        val now = Instant.now(clock)

        return Jwts.builder()
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("role", user.role.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expiresInSeconds)))
            .signWith(signingKey)
            .compact()
    }

    override fun extractUserId(token: String): UUID =
        try {
            val claims = Jwts.parser()
                .clock { Date.from(Instant.now(clock)) }
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload

            UUID.fromString(claims.subject)
        } catch (ex: Exception) {
            throw InvalidAccessTokenException()
        }
}
