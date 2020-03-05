package org.jetbrains.kotlin.script.examples

import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.reflect.full.memberProperties
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.impl.getResourcePathForClass
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

class ScriptTest {

    @Test
    fun `Sample Test`() {
        val out = captureOut {
            val res = evalFile("hello-world")
            assertSucceeded(res)
        }.lines()

        Assert.assertEquals("hello world", out[0])
    }

}

private fun assertSucceeded(res: ResultWithDiagnostics<EvaluationResult>) {
    Assert.assertTrue(
        "test failed:\n  ${res.reports.joinToString("\n  ") { it.message + if (it.exception == null) "" else ": ${it.exception}" }}",
        res is ResultWithDiagnostics.Success
    )
}

private fun evalFile(scriptName: String): ResultWithDiagnostics<EvaluationResult> {
    val scriptFile = File("testData/$scriptName.$extension.kts")
    val scriptDefinition = createJvmCompilationConfigurationFromTemplate<Script>()

    val evaluationEnv = ScriptEvaluationConfiguration {
        jvm {
            baseClassLoader(null)
        }
        constructorArgs(emptyArray<String>())
        enableScriptsInstancesSharing()
    }

    return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), scriptDefinition, evaluationEnv)
}

private fun captureOut(body: () -> Unit): String {
    val outStream = ByteArrayOutputStream()
    val prevOut = System.out
    System.setOut(PrintStream(outStream))
    try {
        body()
    } finally {
        System.out.flush()
        System.setOut(prevOut)
    }
    return outStream.toString().trim()
}