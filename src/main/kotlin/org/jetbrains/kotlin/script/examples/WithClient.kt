package org.jetbrains.kotlin.script.examples

import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.sourceCode.SourceCodeTransformable

internal data class WithClient<T>(val client: WorldBankClient, val value: T)

internal fun <T> T.withClient(client: WorldBankClient): WithClient<T> {
    return WithClient(client, this)
}

internal suspend fun <T : SourceCodeTransformable<WorldBankClient>> WithClient<T>.transform() = with (value) {
    client.transform()
}