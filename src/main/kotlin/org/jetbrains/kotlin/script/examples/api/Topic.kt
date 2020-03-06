package org.jetbrains.kotlin.script.examples.api

class Topic<T : Any> internal constructor(
    val id: String,
    val name: String,
    client: WorldBankClient
) {
    val indicators = TopicIndicators<T>(client)
}