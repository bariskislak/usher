package com.usher.common.messaging

interface EventPublisher {
    fun publish(topic: String, event: DomainEvent)
}
