package com.usher.common.cache

import java.time.Duration

interface CachePort {
    fun get(key: String): String?

    fun set(key: String, value: String, ttl: Duration)

    fun evict(key: String)
}
