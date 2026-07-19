package com.usher.url.shortening.api

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OwnerIdResolver {
    fun resolve(ownerIdHeader: String?): UUID =
        ownerIdHeader
            ?.takeIf { it.isNotBlank() }
            ?.let {
                try {
                    UUID.fromString(it)
                } catch (ex: IllegalArgumentException) {
                    throw InvalidOwnerIdException()
                }
            }
            ?: throw MissingOwnerIdException()
}
