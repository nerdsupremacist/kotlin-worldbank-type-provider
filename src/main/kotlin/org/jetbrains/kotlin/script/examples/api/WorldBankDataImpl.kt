package org.jetbrains.kotlin.script.examples.api

class WorldBankDataImpl(url: String) {
    private val client = WorldBankClient(url = url)

    val countries = Countries(client = client)
    val regions = Regions(client = client)
    val topics = Topics(client = client)
}