package org.jetbrains.kotlin.script.examples.api.records

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.api.documents

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class TopicRecord(val id: String, val value: String)

internal suspend fun WorldBankClient.topics(): List<TopicRecord> = documents("topic")
