package org.jetbrains.kotlin.script.examples.sourceCode

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jetbrains.kotlin.script.examples.api.WorldBankClient

internal fun List<SourceCodeTransformable>.asSourceCodeTransformable(
): SourceCodeTransformable = CompoundSourceCodeTransformable(this)

private class CompoundSourceCodeTransformable(
    private val children: List<SourceCodeTransformable>
) : SourceCodeTransformable {

    override suspend fun WorldBankClient.transform(): String {
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