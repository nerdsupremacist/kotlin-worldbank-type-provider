package org.jetbrains.kotlin.script.examples.api

class RegionCountries<R : Any> internal constructor(private val client: WorldBankClient) {
    fun country(
        id: String,
        name: String,
        capitalCity: String,
        regionId: String,
        regionName: String
    ) = Country<R>(id, name, capitalCity, regionId, regionName, client)
}