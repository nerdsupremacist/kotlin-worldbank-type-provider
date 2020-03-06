package org.jetbrains.kotlin.script.examples

import org.jetbrains.kotlin.script.examples.api.*
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm

object ScriptDefinition : ScriptCompilationConfiguration({
    defaultImports(
        File::class,
        WorldBank::class,
        WorldBankDataImpl::class,
        Countries::class,
        Country::class,
        Indicator::class,
        IndicatorDescription::class,
        Indicators::class,
        Region::class,
        RegionCountries::class,
        Regions::class,
        Topic::class,
        TopicIndicators::class,
        Topics::class
    )

    jvm {
        dependenciesFromClassContext(ScriptDefinition::class, wholeClasspath = true)
    }

    refineConfiguration {
        onAnnotations(WorldBank::class, handler = Configurator)
    }

    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
})