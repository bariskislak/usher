package com.usher.auth.token

import com.usher.auth.user.User
import java.util.UUID

interface AccessTokenService {
    val expiresInSeconds: Long

    fun issue(user: User): String

    fun extractUserId(token: String): UUID
}
