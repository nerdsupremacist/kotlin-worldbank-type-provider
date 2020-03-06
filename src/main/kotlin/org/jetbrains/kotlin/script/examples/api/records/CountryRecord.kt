package org.jetbrains.kotlin.script.examples.api.records

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.api.documents

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class CountryRecord(
    val id: String,
    val name: String,
    val capitalCity: String,
    val region: RegionReference
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class RegionReference(val id: String)

    val isRegion = region.id == "NA"
}

internal suspend fun WorldBankClient.countries(arguments: Map<String, String> = emptyMap())
        = documents<CountryRecord>("country", arguments = arguments)

internal suspend fun WorldBankClient.countriesForRegion(region: RegionRecord)
        = countries(arguments = mapOf("region" to region.id))