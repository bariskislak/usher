package com.usher.common.messaging

interface EventConsumer {
    fun subscribe(topic: String, handler: (DomainEvent) -> Unit)
}
