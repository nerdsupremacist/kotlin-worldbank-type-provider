package org.jetbrains.kotlin.script.examples.api.records

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.api.documents
import org.jetbrains.kotlin.script.examples.sourceCode.SourceCodeTransformable
import org.jetbrains.kotlin.script.examples.sourceCode.asSourceCodeTransformable
import org.jetbrains.kotlin.script.examples.utils.toCamelCase
import org.jetbrains.kotlin.script.examples.utils.toUpperCamelCase

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class RegionRecord(val code: String, val name: String): SourceCodeTransformable {

    override suspend fun WorldBankClient.transform(): String = coroutineScope {
        val codeForCountries = async {
            countriesForRegion(this@RegionRecord)
                .map { CountryInRegion(it, this@RegionRecord) }
                .asSourceCodeTransformable()
                .run { transform() }
        }

        val regionTypeName = name.toUpperCamelCase()
        val regionPropertyName = name.toCamelCase()

        val enumType = "enum class $regionTypeName { ; }"
        val regionsProperty = """
            val Regions.${regionPropertyName}: Region<$regionTypeName>
                get() = region(
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

private data class CountryInRegion(val country: CountryRecord, val region: RegionRecord): SourceCodeTransformable {

    override suspend fun WorldBankClient.transform(): String {
        return with (country) { transformInside(region = this@CountryInRegion.region) }
    }

}
