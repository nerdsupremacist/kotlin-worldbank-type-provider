
@file:WorldBank

val germany = WorldBankData.countries.germany
val europe = germany.region
val spain = europe.countries.spain

println(germany.name)
println(spain.name)