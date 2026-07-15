package com.usher.auth.test

import com.usher.auth.token.JwtAccessTokenService
import com.usher.auth.token.RefreshTokenHasher
import com.usher.auth.token.RefreshTokenIssuer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class AuthServiceTestFixture {
    val clock: Clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    val passwordEncoder = BCryptPasswordEncoder()
    val userRepository = FakeUserRepository()
    val refreshTokenRepository = FakeRefreshTokenRepository()
    val refreshTokenHasher = RefreshTokenHasher()
    val accessTokenService = JwtAccessTokenService(
        secret = "test-secret-must-be-at-least-32-bytes-long",
        expiresInSeconds = 900,
        clock = clock,
    )
    val refreshTokenIssuer = RefreshTokenIssuer(
        refreshTokenRepository = refreshTokenRepository,
        refreshTokenHasher = refreshTokenHasher,
        clock = clock,
        expiresInSeconds = 604800,
    )
}
