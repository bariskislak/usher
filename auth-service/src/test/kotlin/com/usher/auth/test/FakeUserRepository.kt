package com.usher.auth.test

import com.usher.auth.user.User
import com.usher.auth.user.UserRepository
import java.util.UUID

class FakeUserRepository : UserRepository {
    private val usersById = linkedMapOf<UUID, User>()

    override fun existsByEmail(email: String): Boolean =
        usersById.values.any { it.email == email }

    override fun findByEmail(email: String): User? =
        usersById.values.firstOrNull { it.email == email }

    override fun findById(id: UUID): User? =
        usersById[id]

    override fun save(user: User): User {
        usersById[user.id] = user
        return user
    }

    fun count(): Int = usersById.size
}
