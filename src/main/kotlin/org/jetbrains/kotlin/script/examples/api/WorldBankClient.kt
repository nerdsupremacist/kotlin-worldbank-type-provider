package org.jetbrains.kotlin.script.examples.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import java.lang.Integer.max
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.reflect.KClass

//region Basic World Bank HTTP Client

class WorldBankClient(private val url: String) {

    //region Sub Types

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PageInfo(val page: Int, val pages: Int)

    data class Page<T>(val info: PageInfo, val values: List<T>)

    //endregion


    //region Loading Data

    suspend fun <T : Any> page(
        vararg functions: String,
        arguments: Map<String, String> = emptyMap(),
        page: Int,
        type: KClass<T>
    ): Page<T> {
        val client = HttpClient.newHttpClient()

        val allArguments = arguments + ("format" to "json") + ("per_page" to "1000") + ("page" to page)
        val path = functions.joinToString("/")
        val query = allArguments.entries.joinToString("&") { "${it.key}=${it.value}" }
        val uri = URI.create("$url/$path/?$query")
        val request = HttpRequest.newBuilder(uri).build()

        val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        val content = response.await().body()

        val module = KotlinModule()
        module.addDeserializer(Page::class.java, PageDeserializer(type = type))
        val mapper = ObjectMapper().registerModule(module)

        return mapper.readValue(content = content)
    }

    suspend fun <T : Any> documents(vararg functions: String, arguments: Map<String, String> = emptyMap(), type: KClass<T>): List<T> {
        return documents(*functions, arguments = arguments, page = 1, parallelPageDownload = 1, type = type)
    }

    private suspend fun <T : Any> documents(
        vararg functions: String,
        arguments: Map<String, String>,
        page: Int,
        parallelPageDownload: Int,
        type: KClass<T>
    ): List<T> {
        val pageNumbers = IntArray(parallelPageDownload).map { page + it }
        val pages: List<Page<T>> = coroutineScope {
            pageNumbers
                .map { async { page(*functions, arguments = arguments, page = it, type = type) } }
                .awaitAll()
        }

        val remainingPages = pages
            .firstOrNull()
            ?.info
            ?.pages
            ?.let { max(it - page - parallelPageDownload, 0)  } ?: 1

        val items = pages.flatMap { it.values }

        return if (remainingPages > 0) {
            items + documents(
                *functions,
                arguments = arguments,
                page = page + parallelPageDownload,
                parallelPageDownload = remainingPages,
                type = type
            )
        } else {
            items
        }
    }

    //endregion

}

//endregion


//region Reified Fetch Functions

suspend inline fun <reified T : Any> WorldBankClient.page(
    vararg functions: String,
    arguments: Map<String, String> = emptyMap(),
    page: Int
): WorldBankClient.Page<T> {
    return page(*functions, arguments = arguments, page = page, type = T::class)
}

suspend inline fun <reified T : Any> WorldBankClient.documents(
    vararg functions: String,
    arguments: Map<String, String> = emptyMap()
): List<T> {
    return documents(*functions, arguments = arguments, type = T::class)
}

//endregion

//region Deserialize a Page

/*
    The World Bank API returns pages as an array of two elements.

    This Array contains:
        Index 0: Object with page info
        Index 1: Array of values in the page

    Example: [
        { "page": 1, "pages": 1, "per_page": "1000", "total": 304 },
        [
            {
                ...
            },
            ...
        ]
    ]

    This custom deserializer will transform that into a an actual Page
 */
private class PageDeserializer<T : Any>(val type: KClass<T>) : JsonDeserializer<WorldBankClient.Page<T>>() {

    override fun deserialize(parser: JsonParser?, context: DeserializationContext?): WorldBankClient.Page<T> {
        if (parser == null)
            throw JsonParseException(null, "No parser provided")

        val node = parser.codec.readTree(parser) as JsonNode
        val iterator = node.elements()

        val info = iterator
            .nextOrNull()
            ?.let { info ->
                parser.codec.treeToValue(info, WorldBankClient.PageInfo::class.java)
            } ?: throw JsonParseException(parser, "No Page Info Provided")

        val values = iterator
                .nextOrNull()
                ?.elements()
                ?.asSequence()
                ?.map { parser.codec.treeToValue(it, type.java) }
                ?.toList() ?: emptyList()

        require(!iterator.hasNext()) { "Array should only have exactly two elements" }

        return WorldBankClient.Page(info = info, values = values)
    }
}

private fun <T> Iterator<T>.nextOrNull(): T? = if (hasNext()) next() else null

//endregion
