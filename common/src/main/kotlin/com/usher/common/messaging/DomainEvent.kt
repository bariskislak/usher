package com.usher.common.messaging

import java.time.Instant
import java.util.UUID

interface DomainEvent {
    val id: UUID
    val occurredAt: Instant
}
