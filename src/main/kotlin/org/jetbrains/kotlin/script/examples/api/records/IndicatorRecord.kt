package org.jetbrains.kotlin.script.examples.api.records

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.api.documents

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class IndicatorRecord(
    val id: String,
    val name: String
)

internal suspend fun WorldBankClient.indicators() = documents<IndicatorRecord>("indicator")