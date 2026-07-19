package com.usher.url.test

import com.usher.url.shortening.domain.ShortCodeGenerator

class StubShortCodeGenerator(
    shortCodes: List<String>,
) : ShortCodeGenerator {
    private val shortCodes = ArrayDeque(shortCodes)

    override fun generate(): String =
        shortCodes.removeFirstOrNull() ?: error("No short codes left")
}
