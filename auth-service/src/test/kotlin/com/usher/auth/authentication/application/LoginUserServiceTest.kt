package com.usher.auth.authentication.application

import com.usher.auth.test.AuthServiceTestFixture
import com.usher.auth.user.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class LoginUserServiceTest {
    private val fixture = AuthServiceTestFixture()
    private val loginUserService = LoginUserService(
        userRepository = fixture.userRepository,
        passwordEncoder = fixture.passwordEncoder,
        accessTokenService = fixture.accessTokenService,
        refreshTokenIssuer = fixture.refreshTokenIssuer,
    )

    @Test
    fun `login returns an access token and stores only the refresh token hash`() {
        existingUser(email = "user@example.com", password = "secure-password")

        val tokens = loginUserService.login(
            LoginUserCommand(email = " User@Example.com ", password = "secure-password"),
        )

        assertThat(tokens.accessToken).isNotBlank()
        assertThat(tokens.refreshToken).isNotBlank()
        assertThat(tokens.expiresIn).isEqualTo(900)
        assertThat(fixture.refreshTokenRepository.count()).isEqualTo(1)

        val storedToken = fixture.refreshTokenRepository.findAll().single()
        assertThat(storedToken.tokenHash).isEqualTo(fixture.refreshTokenHasher.hash(tokens.refreshToken))
        assertThat(storedToken.tokenHash).isNotEqualTo(tokens.refreshToken)
        assertThat(storedToken.revokedAt).isNull()
    }

    @Test
    fun `login rejects an unknown email`() {
        assertThatThrownBy {
            loginUserService.login(
                LoginUserCommand(email = "unknown@example.com", password = "secure-password"),
            )
        }.isInstanceOf(InvalidCredentialsException::class.java)

        assertThat(fixture.refreshTokenRepository.count()).isZero()
    }

    @Test
    fun `login rejects a wrong password`() {
        existingUser(email = "user@example.com", password = "secure-password")

        assertThatThrownBy {
            loginUserService.login(
                LoginUserCommand(email = "user@example.com", password = "wrong-password"),
            )
        }.isInstanceOf(InvalidCredentialsException::class.java)

        assertThat(fixture.refreshTokenRepository.count()).isZero()
    }

    private fun existingUser(email: String, password: String): User =
        fixture.userRepository.save(
            User(
                email = email,
                passwordHash = fixture.passwordEncoder.encode(password),
            ),
        )
}
