package org.jetbrains.kotlin.script.examples

import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.FileBasedScriptSource
import kotlin.script.experimental.host.toScriptSource

object Configurator : RefineScriptCompilationConfigurationHandler {

    override fun invoke(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
        val baseDirectory = (context.script as? FileBasedScriptSource)?.file?.parentFile

        val banks = context
            .collectedData
            ?.get(ScriptCollectedData.foundAnnotations)
            ?.mapNotNull { annotation ->
                when (annotation) {
                    is WorldBank -> annotation
                    else -> null
                }
            }
            ?.map { bank ->
                WorldBankAnnotationInstance(
                    url = bank.url.takeIf { it.isNotBlank() } ?: "http://api.worldbank.org",
                    sources = bank.sources.toList().takeIf { it.isNotEmpty() }
                )
            }
            ?.distinct()
            ?.takeIf { it.isNotEmpty() } ?: return context.compilationConfiguration.asSuccess()

        if (banks.count() != 1) {
            val bankDescriptions = banks.joinToString("\n\n")
            val errorMessage = "Importing different versions of the World Bank into the same script of: \n $bankDescriptions"
            return makeFailureResult(errorMessage.asErrorDiagnostics())
        }

        val generatedCode = runBlocking { banks.first().all().transform() }

        val generatedScript = createTempFile(prefix = "CodeGen", suffix = ".$extension.kts", directory = baseDirectory)
                .apply { writeText(generatedCode) }
                .apply { deleteOnExit() }
                .toScriptSource()

        return ScriptCompilationConfiguration(context.compilationConfiguration) {
            importScripts.append(generatedScript)
        }.asSuccess()
    }

}