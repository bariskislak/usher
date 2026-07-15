package com.usher.auth.authentication.application

import com.usher.auth.token.AccessTokenService
import com.usher.auth.token.RefreshTokenIssuer
import com.usher.auth.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Locale

@Service
class LoginUserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val accessTokenService: AccessTokenService,
    private val refreshTokenIssuer: RefreshTokenIssuer,
) {
    @Transactional
    fun login(command: LoginUserCommand): AuthTokens {
        val email = command.email.trim().lowercase(Locale.ROOT)
        val user = userRepository.findByEmail(email) ?: throw InvalidCredentialsException()

        if (!passwordEncoder.matches(command.password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }

        return AuthTokens(
            accessToken = accessTokenService.issue(user),
            refreshToken = refreshTokenIssuer.issue(user),
            expiresIn = accessTokenService.expiresInSeconds,
        )
    }
}
