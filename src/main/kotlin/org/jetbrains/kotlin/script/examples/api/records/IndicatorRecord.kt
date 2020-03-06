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
    val source: SourceReference,
    val topics: List<TopicReference>
): SourceCodeTransformable<Any> {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class SourceReference(val id: String, val value: String)

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TopicReference(val id: String?)

    val topicIds: Set<String>
        get() = topics.mapNotNull { it.id }.toSet()

    val cleanName: String = name.replace("%", "Percentage")

    override suspend fun Any.transform(): String {
        val propertyName = cleanName.toCamelCase()

        return """
            fun Indicators.$propertyName(): Indicator = indicator(
                id = "$id",
                name = "$name"
            )
        """.trimIndent()
    }

    fun TopicRecord.transformInTopic(): String {
        val topicTypeName = value.toUpperCamelCase()
        val propertyName = cleanName.toCamelCase()

        return """
            @JvmName("get$topicTypeName$propertyName")
            fun TopicIndicators<$topicTypeName>.$propertyName() = indicatorDescription(
                id = "$id",
                name = "$name"
            )
        """.trimIndent()
    }
}

internal suspend fun WorldBankClient.indicators() = documents<IndicatorRecord>("indicator").distinct()