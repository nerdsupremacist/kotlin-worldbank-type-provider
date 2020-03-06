package org.jetbrains.kotlin.script.examples.api.records

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.api.documents

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class RegionRecord(val id: String, val name: String)

internal suspend fun WorldBankClient.regions(): List<RegionRecord> = documents("region")