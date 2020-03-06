package org.jetbrains.kotlin.script.examples.api

class Country<R : Any> internal constructor(
    val id: String,
    val name: String,
    val capitalCity: String,
    private val regionId: String,
    private val regionName: String,
    private val client: WorldBankClient
) {
    fun region() = Region<R>(id = regionId, name = regionName, client = client)
    fun indicators() = Indicators(countryOrRegionCode = id, client = client)

    suspend operator fun invoke(indicator: IndicatorDescription) = indicator(this)
}