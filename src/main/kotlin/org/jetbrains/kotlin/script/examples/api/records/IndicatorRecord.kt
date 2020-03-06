package org.jetbrains.kotlin.script.examples.api.records

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.jetbrains.kotlin.script.examples.api.IndicatorDescription
import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.api.documents
import org.jetbrains.kotlin.script.examples.sourceCode.SourceCodeTransformable
import org.jetbrains.kotlin.script.examples.utils.toCamelCase
import org.jetbrains.kotlin.script.examples.utils.toUpperCamelCase

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class IndicatorRecord(
    val id: String,
    val name: String,
    val sourceNote: String,
    val source: SourceReference
): SourceCodeTransformable {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class SourceReference(val id: String, val value: String)

    override suspend fun WorldBankClient.transform(): String {
        val propertyName = name.toCamelCase()

        return """
            fun Indicators.$propertyName(): Indicator = indicator(
                id = "$id",
                name = "$name",
                description = "$sourceNote"
            )
        """.trimIndent()
    }

    suspend fun WorldBankClient.transformInside(topic: TopicRecord): String {
        val topicTypeName = topic.value.toUpperCamelCase()
        val propertyName = name.toCamelCase()

        return """
            func TopicIndicators<$topicTypeName>.$propertyName() = indicator(
                id = "$id",
                name = "$name",
                description = "$sourceNote"
            )
        """.trimIndent()
    }
}

internal suspend fun WorldBankClient.indicators() = documents<IndicatorRecord>("indicator").distinct()