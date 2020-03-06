package org.jetbrains.kotlin.script.examples.sourceCode

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jetbrains.kotlin.script.examples.api.WorldBankClient

internal fun <T> List<SourceCodeTransformable<T>>.asSourceCodeTransformable(
): SourceCodeTransformable<T> = CompoundSourceCodeTransformable(this)

private class CompoundSourceCodeTransformable<Context>(
    private val children: List<SourceCodeTransformable<Context>>
) : SourceCodeTransformable<Context> {

    override suspend fun Context.transform(): String {
        val code = coroutineScope {
            children.map { child ->
                async {
                    with (child) { transform() }
                }
            }.awaitAll()
        }

        return code.joinToString("\n\n")
    }

}