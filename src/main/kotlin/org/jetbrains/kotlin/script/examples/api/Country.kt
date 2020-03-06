package org.jetbrains.kotlin.script.examples.api

class Country<R : Any> internal constructor(
    val id: String,
    val name: String,
    val capitalCity: String,
    regionId: String,
    regionName: String,
    client: WorldBankClient
) {
    val region = Region<R>(id = regionId, name = regionName, client = client)
    val indicators = Indicators(countryOrRegionCode = id, client = client)

    suspend operator fun invoke(indicator: IndicatorDescription) = indicator(this)
}