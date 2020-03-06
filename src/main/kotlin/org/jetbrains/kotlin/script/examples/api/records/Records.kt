package org.jetbrains.kotlin.script.examples.api.records

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.jetbrains.kotlin.script.examples.api.WorldBankClient

internal data class Records(
    val client: WorldBankClient,
    val topics: List<TopicRecord>,
    val indicators: List<IndicatorRecord>,
    val countries: List<CountryRecord>,
    val regions: List<RegionRecord>
)

internal suspend fun WorldBankClient.all(): Records = coroutineScope {
    val topics = async { topics() }
    val indicators = async { indicators() }
    val countries = async { countries() }
    val regions = async { regions() }

    Records(
        client = this@all,
        topics = topics.await(),
        indicators = indicators.await(),
        countries = countries.await(),
        regions = regions.await()
    )
}