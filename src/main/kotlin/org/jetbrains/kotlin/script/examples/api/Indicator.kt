package org.jetbrains.kotlin.script.examples.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

class Indicator internal constructor(
    val id: String,
    val name: String,
    private val countryOrRegionCode: String,
    private val client: WorldBankClient
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    private class Data(val date: String, val value: String?)

    suspend operator fun invoke(): List<Pair<Int, Double>> = client
        .documents<Data>(
            "countries",
            countryOrRegionCode,
            "indicators",
            id
        )
        .map { it.date.toInt() to (it.value?.toDouble() ?: 0.0) }
}