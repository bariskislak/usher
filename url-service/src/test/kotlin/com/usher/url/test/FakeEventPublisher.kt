package com.usher.url.test

import com.usher.common.messaging.DomainEvent
import com.usher.common.messaging.EventPublisher

class FakeEventPublisher : EventPublisher {
    private val publishedEvents = mutableListOf<PublishedEvent>()

    override fun publish(topic: String, event: DomainEvent) {
        publishedEvents.add(PublishedEvent(topic = topic, event = event))
    }

    fun publishedEvents(): List<PublishedEvent> = publishedEvents.toList()

    data class PublishedEvent(
        val topic: String,
        val event: DomainEvent,
    )
}
