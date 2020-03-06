package org.jetbrains.kotlin.script.examples.api

class Topics internal constructor(private val client: WorldBankClient) {
    fun <T : Any> topics(id: String, name: String) = Topic<T>(id, name, client)
}