package org.jetbrains.kotlin.script.examples.api

class Countries internal constructor(private val client: WorldBankClient) {
    fun <R : Any> country(
        id: String,
        name: String,
        capitalCity: String,
        regionId: String,
        regionName: String
    ) = Country<R>(id, name, capitalCity, regionId, regionName, client)
}