package org.jetbrains.kotlin.script.examples.utils

//region Changing the Case of a String

fun String.toCamelCase(): String {
    if (isEmpty()) return this
    if (toUpperCase() == this) return toLowerCase()

    val parts = parts
    return parts.first().toLowerCase() + parts.drop(1).joinToString("") { it.toLowerCase().capitalize() }
}

fun String.toUpperCamelCase(): String {
    if (isEmpty()) return this
    if (toUpperCase() == this) return capitalize()

    return parts.joinToString("") { it.capitalize() }
}

//endregion

//region Splitting a String into its individual parts

private val lowerToCapitalSplit = Regex("([a-z])([A-Z])")
private val uppercaseWordSplit = Regex("([A-Z]+)([A-Z][a-z]|\$)")
private val invalidCharacters = Regex("[^0-9a-zA-Z]")

private val String.parts: List<String>
    get() {
        val simpleSplits = replace(regex = lowerToCapitalSplit, replacement = "$1 $2")
        val splitAfterUppercaseWord = simpleSplits.replace(regex = uppercaseWordSplit, replacement = "$1 $2")
        return splitAfterUppercaseWord.split(regex = invalidCharacters)
    }

//endregion