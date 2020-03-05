package org.jetbrains.kotlin.script.examples

import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.FileBasedScriptSource
import kotlin.script.experimental.host.toScriptSource

object Configurator : RefineScriptCompilationConfigurationHandler {

    override fun invoke(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
        val baseDirectory = (context.script as? FileBasedScriptSource)?.file?.parentFile

        context
            .collectedData
            ?.get(ScriptCollectedData.foundAnnotations)
            ?.mapNotNull { annotation ->
                when (annotation) {
                    is WorldBank -> annotation
                    else -> null
                }
            }
            ?.takeIf { it.isNotEmpty() } ?: return context.compilationConfiguration.asSuccess()

        val generatedCode = emptyList<String>()

        val generatedScripts = generatedCode
            .map { resolvedCode ->
                createTempFile(prefix = "CodeGen", suffix = ".$extension.kts", directory = baseDirectory)
                    .apply { writeText(resolvedCode) }
                    .apply { deleteOnExit() }
                    .toScriptSource()
            }

        return ScriptCompilationConfiguration(context.compilationConfiguration) {
            importScripts.append(generatedScripts)
        }.asSuccess()
    }

}