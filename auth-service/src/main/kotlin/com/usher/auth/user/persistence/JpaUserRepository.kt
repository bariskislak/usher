package com.usher.auth.user.persistence

import com.usher.auth.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaUserRepository : JpaRepository<User, UUID> {
    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): User?
}
