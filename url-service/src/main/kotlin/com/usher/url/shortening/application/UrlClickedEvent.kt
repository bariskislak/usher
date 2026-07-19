package com.usher.url.shortening.application

import com.usher.common.messaging.DomainEvent
import java.time.Instant
import java.util.UUID

data class UrlClickedEvent(
    override val id: UUID = UUID.randomUUID(),
    override val occurredAt: Instant,
    val shortUrlId: UUID,
    val ownerId: UUID,
    val shortCode: String,
    val originalUrl: String,
) : DomainEvent
