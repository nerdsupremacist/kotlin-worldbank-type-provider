package org.jetbrains.kotlin.script.examples.api.records

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.jetbrains.kotlin.script.examples.api.Countries
import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.api.documents
import org.jetbrains.kotlin.script.examples.sourceCode.SourceCodeTransformable
import org.jetbrains.kotlin.script.examples.utils.toCamelCase
import org.jetbrains.kotlin.script.examples.utils.toUpperCamelCase

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class CountryRecord(
    val id: String,
    val name: String,
    val capitalCity: String,
    val region: RegionReference
): SourceCodeTransformable<Any> {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class RegionReference(val id: String, val value: String)

    val isRegion = region.id == "NA"

    override suspend fun Any.transform(): String {
        val propertyName = name.toCamelCase()
        val regionTypeName = region.value.toUpperCamelCase()

        return """
            fun Countries.${propertyName}(): Country<$regionTypeName> = country(
                id = "$id", 
                name = "$name",
                capitalCity = "$capitalCity",
                regionId = "${region.id}",
                regionName = "${region.value}"
            )
        """.trimIndent()
    }

    fun RegionRecord.transformInRegion(): String {
        val propertyName = this@CountryRecord.name.toCamelCase()
        val regionTypeName = name.toUpperCamelCase()

        return """
            @JvmName("get$regionTypeName$propertyName")
            fun RegionCountries<$regionTypeName>.${propertyName}(): Country<$regionTypeName> = country(
                id = "$id", 
                name = "${this@CountryRecord.name}",
                capitalCity = "$capitalCity",
                regionId = "$code",
                regionName = "$name"
            )
        """.trimIndent()
    }
}

internal suspend fun WorldBankClient.countries(arguments: Map<String, String> = emptyMap())
        = documents<CountryRecord>("country", arguments = arguments).filter { !it.isRegion }

internal suspend fun WorldBankClient.countriesForRegion(region: RegionRecord)
        = countries(arguments = mapOf("region" to region.code))