package org.jetbrains.kotlin.script.examples.api

class Topic<T : Any> internal constructor(
    val id: String,
    val name: String,
    private val client: WorldBankClient
) {
    fun indicators() = TopicIndicators<T>(client)
}