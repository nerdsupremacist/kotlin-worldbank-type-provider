package org.jetbrains.kotlin.script.examples.api

class Regions internal constructor(private val client: WorldBankClient) {
    fun <R : Any> region(id: String, name: String) = Region<R>(id, name, client)
}