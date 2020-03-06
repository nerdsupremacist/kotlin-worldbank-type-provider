package org.jetbrains.kotlin.script.examples.api

class TopicIndicators<T : Any> internal constructor(private val client: WorldBankClient) {
    fun indicatorDescription(
        id: String,
        name: String
    ) = IndicatorDescription(id, name, client)
}