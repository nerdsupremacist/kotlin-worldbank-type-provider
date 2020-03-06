package org.jetbrains.kotlin.script.examples.sourceCode

internal interface SourceCodeTransformable<in C> {
    suspend fun C.transform(): String
}