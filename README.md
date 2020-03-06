# kotlin-worldbank-type-provider
Type Provider for the WorldBank API inside of Kotlin Scripting. It let's you query specific data directly with added type safety

## Example

Use `@WorldBank` in your file and it will create all the types needed to use the WorldBank API. 
For instance if you want to compare the Percentage of GDP that is Trade In Services between Germany and the rest of the EU:

```kotlin
@file:WorldBank

val germany = WorldBankData.countries().germany()
val europeanUnion = WorldBankDAta.regions().europeanUnion()

// Data is laid out by year
val tradeInServicesForGermany = germany.indicators().tradeInServicesPercentageOfGdp() // List<Pair<Int, Double>>
val tradeInServicesForEuropeanUnion = europeanUnion.indicators().tradeInServicesPercentageOfGdp() // List<Pair<Int, Double>>

// use some plotting library
plot(tradeInServicesForGermany)
plot(tradeInServicesForEuropeanUnion)
```

## Usage

coming soon
