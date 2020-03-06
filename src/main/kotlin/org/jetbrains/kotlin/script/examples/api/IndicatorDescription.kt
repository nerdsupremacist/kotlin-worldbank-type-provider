package org.jetbrains.kotlin.script.examples.api

class IndicatorDescription internal constructor(
    val id: String,
    val name: String,
    private val client: WorldBankClient
) {
    suspend operator fun <R : Any> invoke(country: Country<R>): List<Pair<Int, Double>> =
        Indicator(id, name, country.id, client).invoke()

    suspend operator fun <R : Any> invoke(region: Region<R>): List<Pair<Int, Double>> =
        Indicator(id, name, region.id, client).invoke()
}