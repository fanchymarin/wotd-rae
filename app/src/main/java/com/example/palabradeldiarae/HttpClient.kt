package com.example.palabradeldiarae

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup

private val TAG: String = Notification::class.java.getName()

class HttpClient: OkHttpClient() {

    @Serializable
    data class WordOfTheDayName(
        val header: String,
        val id: String
    )

    var wordOfTheDayName: WordOfTheDayName = WordOfTheDayName("", "")
    val wordOfTheDayDefinition: String = ""

    fun retrieveWordOfTheDay() {
        Log.d(TAG, "Retrieving word of the day")
        parseWordOfTheDayName()
        parseWordOfTheDayDefinition()
    }

    private fun getResponse(url: String): String {
        var request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Diccionario/2 CFNetwork/808.2.16 Darwin/16.3.0")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Authorization", "Basic cDY4MkpnaFMzOmFHZlVkQ2lFNDM0")
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

    private fun parseWordOfTheDayDefinition() {
        val url = "https://dle.rae.es/data/fetch?id=${wordOfTheDayName.id}"
        val responseBodyString = getResponse(url)
        try {
            val wordOfTheDayDefinitionHtml = Jsoup.parse(responseBodyString)
            // TODO: Implement Html parser

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing word of the day definition: ${e.message}")
        }
    }

    private fun parseWordOfTheDayName() {
        val url = "https://dle.rae.es/data/wotd?callback=json"
        var responseBodyString = getResponse(url)
        responseBodyString = responseBodyString.substring(5, responseBodyString.length - 1)
        wordOfTheDayName = Json.decodeFromString<WordOfTheDayName>(
            responseBodyString.toString()
        )
        Log.d(
            TAG,
            "Word of the day name: ${wordOfTheDayName.header}, ${wordOfTheDayName.id}"
        )
    }
}