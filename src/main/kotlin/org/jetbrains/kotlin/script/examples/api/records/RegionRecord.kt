package org.jetbrains.kotlin.script.examples.api.records

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.jetbrains.kotlin.script.examples.WithClient
import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.api.documents
import org.jetbrains.kotlin.script.examples.sourceCode.SourceCodeTransformable
import org.jetbrains.kotlin.script.examples.sourceCode.asSourceCodeTransformable
import org.jetbrains.kotlin.script.examples.utils.toCamelCase
import org.jetbrains.kotlin.script.examples.utils.toUpperCamelCase

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class RegionRecord(val code: String, val name: String): SourceCodeTransformable<WithClient<*>> {

    override suspend fun WithClient<*>.transform(): String = coroutineScope {
        val codeForCountries = async {
            client.countriesForRegion(this@RegionRecord)
                .map { CountryInRegion(it) }
                .asSourceCodeTransformable()
                .run { this@RegionRecord.transform() }
        }

        val regionTypeName = name.toUpperCamelCase()
        val regionPropertyName = name.toCamelCase()

        val enumType = "enum class $regionTypeName { ; }"
        val regionsProperty = """
            fun Regions.${regionPropertyName}(): Region<$regionTypeName> = region(
                id = "$code",
                name = "$name"
            )
        """.trimIndent()

        buildString {
            appendln(enumType)
            appendln(regionsProperty)
            appendln(codeForCountries.await())
        }
    }

}

internal suspend fun WorldBankClient.regions(): List<RegionRecord> = documents("region")

private data class CountryInRegion(val country: CountryRecord): SourceCodeTransformable<RegionRecord> {
    override suspend fun RegionRecord.transform() = with (country) { transformInRegion() }
}
