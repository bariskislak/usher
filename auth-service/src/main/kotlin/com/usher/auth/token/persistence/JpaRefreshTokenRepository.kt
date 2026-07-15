package com.usher.auth.token.persistence

import com.usher.auth.token.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaRefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
    fun findByTokenHash(tokenHash: String): RefreshToken?
}
