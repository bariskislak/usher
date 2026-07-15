package com.usher.auth.authentication.application

import com.usher.auth.token.AccessTokenService
import com.usher.auth.user.User
import com.usher.auth.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCurrentUserService(
    private val accessTokenService: AccessTokenService,
    private val userRepository: UserRepository,
) {
    @Transactional(readOnly = true)
    fun getCurrentUser(authorizationHeader: String?): User {
        val accessToken = authorizationHeader
            ?.takeIf { it.startsWith("Bearer ") }
            ?.removePrefix("Bearer ")
            ?.takeIf { it.isNotBlank() }
            ?: throw MissingAccessTokenException()

        val userId = accessTokenService.extractUserId(accessToken)
        return userRepository.findById(userId) ?: throw InvalidAccessTokenException()
    }
}
