package org.jetbrains.kotlin.script.examples.api.records

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.jetbrains.kotlin.script.examples.api.WorldBankClient
import org.jetbrains.kotlin.script.examples.sourceCode.SourceCodeTransformable
import org.jetbrains.kotlin.script.examples.sourceCode.asSourceCodeTransformable
import org.jetbrains.kotlin.script.examples.withClient

internal data class Records(
    val topics: List<TopicRecord>,
    val indicators: List<IndicatorRecord>,
    val countries: List<CountryRecord>,
    val regions: List<RegionRecord>
) : SourceCodeTransformable<WorldBankClient> {

    override suspend fun WorldBankClient.transform(): String {
        val typeTransformable = listOf(regions, topics, indicators, countries).flatten().asSourceCodeTransformable()

        val instance = """
            val WorldBankData = WorldBankDataImpl(url = "$url")
        """.trimIndent()

        val types = with (typeTransformable) {
            this@Records.withClient(this@transform).transform()
        }

        return buildString {
            appendln(instance)
            append(types)
        }
    }

}

internal suspend fun WorldBankClient.all(): Records = coroutineScope {
    val topics = async { topics() }
    val indicators = async { indicators() }
    val countries = async { countries() }
    val regions = async { regions() }

    Records(
        topics = topics.await(),
        indicators = indicators.await(),
        countries = countries.await(),
        regions = regions.await()
    )
}