package com.usher.auth.user

import java.util.UUID

interface UserRepository {
    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): User?

    fun findById(id: UUID): User?

    fun save(user: User): User
}
