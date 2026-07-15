package com.usher.auth.authentication.application

import com.usher.auth.token.AccessTokenService
import com.usher.auth.token.RefreshTokenHasher
import com.usher.auth.token.RefreshTokenIssuer
import com.usher.auth.token.RefreshTokenRepository
import com.usher.auth.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class RefreshAuthTokensService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val refreshTokenHasher: RefreshTokenHasher,
    private val refreshTokenIssuer: RefreshTokenIssuer,
    private val accessTokenService: AccessTokenService,
    private val userRepository: UserRepository,
    private val clock: Clock,
) {
    @Transactional
    fun refresh(command: RefreshAuthTokensCommand): AuthTokens {
        val tokenHash = refreshTokenHasher.hash(command.refreshToken)
        val storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
            ?: throw InvalidRefreshTokenException()

        val now = Instant.now(clock)
        if (storedToken.revokedAt != null || !storedToken.expiresAt.isAfter(now)) {
            throw InvalidRefreshTokenException()
        }

        val user = userRepository.findById(storedToken.userId) ?: throw InvalidRefreshTokenException()
        storedToken.revokedAt = now
        refreshTokenRepository.save(storedToken)

        return AuthTokens(
            accessToken = accessTokenService.issue(user),
            refreshToken = refreshTokenIssuer.issue(user),
            expiresIn = accessTokenService.expiresInSeconds,
        )
    }
}
