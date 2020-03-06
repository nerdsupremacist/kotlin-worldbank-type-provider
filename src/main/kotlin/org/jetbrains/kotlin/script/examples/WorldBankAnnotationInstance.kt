package org.jetbrains.kotlin.script.examples

import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.api.records.Records
import org.jetbrains.kotlin.script.examples.api.records.all

internal data class WorldBankAnnotationInstance(
    val url: String,
    val sources: List<String>?
) {

    override fun toString() = buildString {
        appendln("WorldBank:")
        append(url.prependIndent())
        if (sources != null) {
            appendln()
            append("Sources:")
            sources.forEach {
                appendln()
                append(it.prependIndent())
            }
        }
    }

}

internal fun WorldBankAnnotationInstance.client() = WorldBankClient(url = url)

internal suspend fun WorldBankAnnotationInstance.all(): WithClient<Records> {
    val client = client()
    return client
        .all()
        .let { records ->
            val sources = sources?.toSet() ?: return@let records
            val indicators = records.indicators.filter { sources.contains(it.source.value) }
            records.copy(indicators = indicators)
        }
        .withClient(client)
}