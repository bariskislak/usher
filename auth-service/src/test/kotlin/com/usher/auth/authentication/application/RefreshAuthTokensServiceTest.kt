package com.usher.auth.authentication.application

import com.usher.auth.test.AuthServiceTestFixture
import com.usher.auth.token.RefreshToken
import com.usher.auth.user.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.Instant

class RefreshAuthTokensServiceTest {
    private val fixture = AuthServiceTestFixture()
    private val refreshAuthTokensService = RefreshAuthTokensService(
        refreshTokenRepository = fixture.refreshTokenRepository,
        refreshTokenHasher = fixture.refreshTokenHasher,
        refreshTokenIssuer = fixture.refreshTokenIssuer,
        accessTokenService = fixture.accessTokenService,
        userRepository = fixture.userRepository,
        clock = fixture.clock,
    )

    @Test
    fun `refresh rotates the refresh token and revokes the previous token`() {
        val user = existingUser(email = "user@example.com", password = "secure-password")
        val oldRefreshToken = fixture.refreshTokenIssuer.issue(user)

        val tokens = refreshAuthTokensService.refresh(
            RefreshAuthTokensCommand(refreshToken = oldRefreshToken),
        )

        assertThat(tokens.accessToken).isNotBlank()
        assertThat(tokens.refreshToken).isNotBlank()
        assertThat(tokens.refreshToken).isNotEqualTo(oldRefreshToken)
        assertThat(tokens.expiresIn).isEqualTo(900)
        assertThat(fixture.refreshTokenRepository.count()).isEqualTo(2)

        val oldStoredToken = fixture.refreshTokenRepository.findByTokenHash(
            fixture.refreshTokenHasher.hash(oldRefreshToken),
        )
        val newStoredToken = fixture.refreshTokenRepository.findByTokenHash(
            fixture.refreshTokenHasher.hash(tokens.refreshToken),
        )

        assertThat(oldStoredToken?.revokedAt).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"))
        assertThat(newStoredToken?.userId).isEqualTo(user.id)
        assertThat(newStoredToken?.revokedAt).isNull()
    }

    @Test
    fun `refresh rejects an unknown refresh token`() {
        assertThatThrownBy {
            refreshAuthTokensService.refresh(
                RefreshAuthTokensCommand(refreshToken = "unknown-refresh-token"),
            )
        }.isInstanceOf(InvalidRefreshTokenException::class.java)
    }

    @Test
    fun `refresh rejects a revoked refresh token`() {
        val user = existingUser(email = "user@example.com", password = "secure-password")
        val refreshToken = fixture.refreshTokenIssuer.issue(user)
        fixture.refreshTokenRepository.findByTokenHash(
            fixture.refreshTokenHasher.hash(refreshToken),
        )?.revokedAt = Instant.parse("2026-01-01T00:00:00Z")

        assertThatThrownBy {
            refreshAuthTokensService.refresh(
                RefreshAuthTokensCommand(refreshToken = refreshToken),
            )
        }.isInstanceOf(InvalidRefreshTokenException::class.java)
    }

    @Test
    fun `refresh rejects an expired refresh token`() {
        val user = existingUser(email = "user@example.com", password = "secure-password")
        val refreshToken = "expired-refresh-token"
        fixture.refreshTokenRepository.save(
            RefreshToken(
                userId = user.id,
                tokenHash = fixture.refreshTokenHasher.hash(refreshToken),
                expiresAt = Instant.parse("2025-12-31T23:59:59Z"),
            ),
        )

        assertThatThrownBy {
            refreshAuthTokensService.refresh(
                RefreshAuthTokensCommand(refreshToken = refreshToken),
            )
        }.isInstanceOf(InvalidRefreshTokenException::class.java)
    }

    private fun existingUser(email: String, password: String): User =
        fixture.userRepository.save(
            User(
                email = email,
                passwordHash = fixture.passwordEncoder.encode(password),
            ),
        )
}
