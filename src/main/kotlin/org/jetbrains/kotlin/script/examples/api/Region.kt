package org.jetbrains.kotlin.script.examples.api

class Region<R : Any> internal constructor(
    val id: String,
    val name: String,
    private val client: WorldBankClient
) {
    fun countries() = RegionCountries<R>(client)
    fun indicators() = Indicators(id, client)

    suspend operator fun invoke(indicator: IndicatorDescription) = indicator(this)
}