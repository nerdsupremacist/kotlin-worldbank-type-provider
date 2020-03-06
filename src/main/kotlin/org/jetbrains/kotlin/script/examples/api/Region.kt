package org.jetbrains.kotlin.script.examples.api

class Region<R : Any> internal constructor(
    val id: String,
    val name: String,
    client: WorldBankClient
) {
    val countries = RegionCountries<R>(client)
    val indicators = Indicators(id, client)

    suspend operator fun invoke(indicator: IndicatorDescription) = indicator(this)
}