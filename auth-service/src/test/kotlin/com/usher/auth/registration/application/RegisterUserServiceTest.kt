package com.usher.auth.registration.application

import com.usher.auth.user.User
import com.usher.auth.test.FakeUserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class RegisterUserServiceTest {
    private val userRepository = FakeUserRepository()
    private val passwordEncoder = BCryptPasswordEncoder()
    private val registerUserService = RegisterUserService(userRepository, passwordEncoder)

    @Test
    fun `register normalizes the email and stores a BCrypt password hash`() {
        val registeredUser = registerUserService.register(
            RegisterUserCommand(email = " User@Example.com ", password = "secure-password"),
        )

        assertThat(registeredUser.email).isEqualTo("user@example.com")
        assertThat(passwordEncoder.matches("secure-password", registeredUser.passwordHash)).isTrue()
        assertThat(userRepository.findByEmail("user@example.com")).isEqualTo(registeredUser)
    }

    @Test
    fun `register rejects an existing email`() {
        userRepository.save(
            User(
                email = "user@example.com",
                passwordHash = passwordEncoder.encode("secure-password"),
            ),
        )

        assertThatThrownBy {
            registerUserService.register(
                RegisterUserCommand(email = "user@example.com", password = "secure-password"),
            )
        }.isInstanceOf(EmailAlreadyRegisteredException::class.java)

        assertThat(userRepository.count()).isEqualTo(1)
    }
}
