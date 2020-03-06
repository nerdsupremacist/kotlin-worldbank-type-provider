package org.jetbrains.kotlin.script.examples

import org.jetbrains.kotlin.script.examples.api.WorldBankClient

internal data class WithClient<T>(val client: WorldBankClient, val value: T)

internal fun <T> T.withClient(client: WorldBankClient): WithClient<T> {
    return WithClient(client, this)
}