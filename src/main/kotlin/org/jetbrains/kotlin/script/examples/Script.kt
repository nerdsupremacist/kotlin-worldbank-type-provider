package org.jetbrains.kotlin.script.examples

import kotlin.script.experimental.annotations.KotlinScript

const val extension = "sample"

@Suppress("unused")
@KotlinScript(
    fileExtension = "$extension.kts",
    compilationConfiguration = ScriptDefinition::class
)
abstract class Script(val args: Array<String>)