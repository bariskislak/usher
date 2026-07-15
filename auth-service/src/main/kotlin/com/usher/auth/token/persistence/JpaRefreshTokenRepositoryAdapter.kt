package com.usher.auth.token.persistence

import com.usher.auth.token.RefreshToken
import com.usher.auth.token.RefreshTokenRepository
import org.springframework.stereotype.Repository

@Repository
class JpaRefreshTokenRepositoryAdapter(
    private val jpaRefreshTokenRepository: JpaRefreshTokenRepository,
) : RefreshTokenRepository {
    override fun findByTokenHash(tokenHash: String): RefreshToken? =
        jpaRefreshTokenRepository.findByTokenHash(tokenHash)

    override fun save(refreshToken: RefreshToken): RefreshToken =
        jpaRefreshTokenRepository.save(refreshToken)
}
