package com.usher.auth.registration.application

import com.usher.auth.user.User
import com.usher.auth.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Locale

@Service
class RegisterUserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun register(command: RegisterUserCommand): User {
        val email = command.email.trim().lowercase(Locale.ROOT)

        if (userRepository.existsByEmail(email)) {
            throw EmailAlreadyRegisteredException()
        }

        return userRepository.save(
            User(
                email = email,
                passwordHash = passwordEncoder.encode(command.password),
            ),
        )
    }
}
