package com.usher.auth.token

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.Base64

@Component
class RefreshTokenHasher {
    fun hash(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(token.toByteArray(Charsets.UTF_8))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }
}
