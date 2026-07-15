package com.usher.auth.user.persistence

import com.usher.auth.user.User
import com.usher.auth.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
class JpaUserRepositoryAdapter(
    private val jpaUserRepository: JpaUserRepository,
) : UserRepository {
    override fun existsByEmail(email: String): Boolean =
        jpaUserRepository.existsByEmail(email)

    override fun findByEmail(email: String): User? =
        jpaUserRepository.findByEmail(email)

    override fun save(user: User): User =
        jpaUserRepository.save(user)
}
