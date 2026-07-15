package com.usher.auth.test

import com.usher.auth.token.RefreshToken
import com.usher.auth.token.RefreshTokenRepository
import java.util.UUID

class FakeRefreshTokenRepository : RefreshTokenRepository {
    private val refreshTokensById = linkedMapOf<UUID, RefreshToken>()

    override fun findByTokenHash(tokenHash: String): RefreshToken? =
        refreshTokensById.values.firstOrNull { it.tokenHash == tokenHash }

    override fun save(refreshToken: RefreshToken): RefreshToken {
        refreshTokensById[refreshToken.id] = refreshToken
        return refreshToken
    }

    fun findAll(): List<RefreshToken> = refreshTokensById.values.toList()

    fun count(): Int = refreshTokensById.size
}
