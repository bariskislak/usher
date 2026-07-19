package com.usher.url.messaging

import com.usher.common.messaging.DomainEvent
import com.usher.common.messaging.EventPublisher
import org.springframework.stereotype.Component

@Component
class NoOpEventPublisher : EventPublisher {
    override fun publish(topic: String, event: DomainEvent) = Unit
}
