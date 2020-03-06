package org.jetbrains.kotlin.script.examples

@Target(AnnotationTarget.FILE)
@Repeatable
@Retention(AnnotationRetention.SOURCE)
annotation class WorldBank(
    val url: String = "",
    vararg val sources: String = ["World Development Indicators", "Global Financial Development"]
)