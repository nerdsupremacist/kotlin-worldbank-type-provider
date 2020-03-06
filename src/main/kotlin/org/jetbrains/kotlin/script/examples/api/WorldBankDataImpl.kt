package org.jetbrains.kotlin.script.examples.api

class WorldBankDataImpl(url: String) {
    private val client = WorldBankClient(url = url)

    fun countries() = Countries(client = client)
    fun regions() = Regions(client = client)
    fun topics() = Topics(client = client)
}