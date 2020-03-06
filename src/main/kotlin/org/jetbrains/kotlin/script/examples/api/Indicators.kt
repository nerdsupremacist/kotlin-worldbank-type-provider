package org.jetbrains.kotlin.script.examples.api

data class Indicators internal constructor(
    private val countryOrRegionCode: String,
    private val client: WorldBankClient
) {
    fun indicator(
        id: String,
        name: String,
        description: String
    ) = Indicator(id, name, description, countryOrRegionCode, client)
}