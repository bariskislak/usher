package com.usher.auth.token

interface RefreshTokenRepository {
    fun findByTokenHash(tokenHash: String): RefreshToken?

    fun save(refreshToken: RefreshToken): RefreshToken
}
