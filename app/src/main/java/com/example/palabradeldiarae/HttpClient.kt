package com.example.palabradeldiarae

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

private val TAG: String = HttpClient::class.java.getName()

class HttpClient: OkHttpClient() {

    @Serializable
    data class WordOfTheDayNameRequest(
        val header: String,
        val id: String
    )

    lateinit var baseUrl: String
    lateinit var wordOfTheDayName: String
    lateinit var wordOfTheDayNameHttp: String
    lateinit var wordOfTheDayDefinition: String
    var          homonyms: Boolean = false

    fun retrieveWordOfTheDay(context: Context) {
        Log.d(TAG, "Retrieving word of the day")
        baseUrl = getString(context, R.string.http_url)
        wordOfTheDayName = parseWordOfTheDayName(context)
        wordOfTheDayNameHttp = wordOfTheDayName.substringBefore(',')
        wordOfTheDayDefinition = parseWordOfTheDayDefinition(context)
    }

    private fun getResponse(url: String, context: Context): String {
        var request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Diccionario/2 CFNetwork/808.2.16 Darwin/16.3.0")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Authorization", getString(context, R.string.http_auth))
            .build()

        return try {
            var response: Response = newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e(TAG, "Error on response: ${response.code}")
                return ""
            }
            response.body?.string()
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving information from server: ${e.message}")
            ""
        } as String
    }

    private fun parseWordOfTheDayDefinition(context: Context): String {
        val url = "${baseUrl}/${wordOfTheDayNameHttp}"
        var responseBodyString = getResponse(url, context)
        val htmlParser = HtmlParser()

        if (responseBodyString == "") {
            Log.e(TAG, "Error retrieving word of the day definition")
            return "Abre la notificación para leer la definición completa"
        }

        try {
            wordOfTheDayDefinition = htmlParser.parseDefinition(responseBodyString)
            Log.d(TAG, "Word of the day definition:\n$wordOfTheDayDefinition")
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing word of the day definition: ${e.message}")
        }
        return wordOfTheDayDefinition
    }

    private fun parseWordOfTheDayName(context: Context): String {
        val url = "${baseUrl}/data/wotd?callback=json"
        var responseBodyString = getResponse(url, context)
        var wordOfTheDayNameRequest = WordOfTheDayNameRequest("", "")
        var wordOfTheDayName = ""

        if (responseBodyString == "") {
            Log.e(TAG, "Error retrieving word of the day name")
            return "Palabra no encontrada"
        }
        // Remove callback function name and parentheses
        responseBodyString = responseBodyString.substring(5, responseBodyString.length - 1)

        wordOfTheDayNameRequest = Json.decodeFromString<WordOfTheDayNameRequest>(
            responseBodyString.toString()
        )
        Log.d(
            TAG,
            "Word of the day name: ${wordOfTheDayNameRequest.header.replace(Regex("<sup>.*?</sup>"), "")}"
        )
        // Remove superscript tags
        wordOfTheDayName = wordOfTheDayNameRequest.header.replace(Regex("<sup>.*?</sup>"), "")
        if (wordOfTheDayName != wordOfTheDayNameRequest.header)
        {
            Log.d(TAG, "Word of the day has homonyms")
            homonyms = true
        }
        return wordOfTheDayName
    }
}