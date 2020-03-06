package org.jetbrains.kotlin.script.examples.sourceCode

import org.jetbrains.kotlin.script.examples.WithClient
import org.jetbrains.kotlin.script.examples.api.WorldBankClient

internal interface SourceCodeTransformable {
    suspend fun WorldBankClient.transform(): String
}

internal suspend fun <T : SourceCodeTransformable> WithClient<T>.transform() = buildString {
    val instance = """
        val WorldBankData = WorldBankDataImpl(url = "${client.url}")
    """.trimIndent()

    appendln(instance)
    appendln()
    append(with (value) { client.transform() })
}