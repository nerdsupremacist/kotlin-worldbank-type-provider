package org.jetbrains.kotlin.script.examples.api.records

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.jetbrains.kotlin.script.examples.WithClient
import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.api.documents
import org.jetbrains.kotlin.script.examples.sourceCode.SourceCodeTransformable
import org.jetbrains.kotlin.script.examples.sourceCode.asSourceCodeTransformable
import org.jetbrains.kotlin.script.examples.utils.toCamelCase
import org.jetbrains.kotlin.script.examples.utils.toUpperCamelCase

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class TopicRecord(
    val id: String,
    val value: String,
    val sourceNote: String
): SourceCodeTransformable<WithClient<Records>> {

    override suspend fun WithClient<Records>.transform(): String {
        val topicPropertyName = this@TopicRecord.value.toCamelCase()
        val topicTypeName = this@TopicRecord.value.toUpperCamelCase()

        val topicType = "enum class $topicTypeName { ; }"

        val topicProperty = """
            fun Topics.$topicPropertyName(): Topic<$topicTypeName> = topic(
                id = "$id",
                name = "${this@TopicRecord.value}"
            )
        """.trimIndent()

        val indicators = value
            .indicators
            .filter { it.topicIds.contains(id) }
            .map(::IndicatorInTopic)
            .asSourceCodeTransformable()
            .run { this@TopicRecord.transform() }

        return buildString {
            appendln(topicType)
            appendln(topicProperty)
            append(indicators)
        }
    }

}

internal suspend fun WorldBankClient.topics(): List<TopicRecord> = documents("topic")

private class IndicatorInTopic(private val indicator: IndicatorRecord): SourceCodeTransformable<TopicRecord> {
    override suspend fun TopicRecord.transform() = with (indicator) { transformInTopic() }
}