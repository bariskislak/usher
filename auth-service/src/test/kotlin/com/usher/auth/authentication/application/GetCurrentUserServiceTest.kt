package com.usher.auth.authentication.application

import com.usher.auth.test.AuthServiceTestFixture
import com.usher.auth.user.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class GetCurrentUserServiceTest {
    private val fixture = AuthServiceTestFixture()
    private val getCurrentUserService = GetCurrentUserService(
        accessTokenService = fixture.accessTokenService,
        userRepository = fixture.userRepository,
    )

    @Test
    fun `get current user returns the user from a valid bearer token`() {
        val user = existingUser(email = "user@example.com", password = "secure-password")
        val accessToken = fixture.accessTokenService.issue(user)

        val currentUser = getCurrentUserService.getCurrentUser("Bearer $accessToken")

        assertThat(currentUser).isEqualTo(user)
    }

    @Test
    fun `get current user rejects a missing access token`() {
        assertThatThrownBy {
            getCurrentUserService.getCurrentUser(null)
        }.isInstanceOf(MissingAccessTokenException::class.java)
    }

    @Test
    fun `get current user rejects an invalid access token`() {
        assertThatThrownBy {
            getCurrentUserService.getCurrentUser("Bearer invalid-access-token")
        }.isInstanceOf(InvalidAccessTokenException::class.java)
    }

    private fun existingUser(email: String, password: String): User =
        fixture.userRepository.save(
            User(
                email = email,
                passwordHash = fixture.passwordEncoder.encode(password),
            ),
        )
}
