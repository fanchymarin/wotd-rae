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
    data class WordOfTheDayName(
        val header: String,
        val id: String
    )

    lateinit var wordOfTheDayName: WordOfTheDayName
    lateinit var wordOfTheDayDefinition: String

    fun retrieveWordOfTheDay(context: Context) {
        Log.d(TAG, "Retrieving word of the day")
        parseWordOfTheDayName(context)
        parseWordOfTheDayDefinition(context)
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

    private fun parseWordOfTheDayDefinition(context: Context) {
        val url = "https://dle.rae.es/${wordOfTheDayName.header}"
        val responseBodyString = getResponse(url, context)
        val htmlParser = HtmlParser()
        try {
            wordOfTheDayDefinition = htmlParser.parseDefinition(responseBodyString)
            Log.d(TAG, "Word of the day definition:\n$wordOfTheDayDefinition")

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing word of the day definition: ${e.message}")
        }
    }

    private fun parseWordOfTheDayName(context: Context) {
        val url = "https://dle.rae.es/data/wotd?callback=json"
        var responseBodyString = getResponse(url, context)
        responseBodyString = responseBodyString.substring(5, responseBodyString.length - 1)
        wordOfTheDayName = Json.decodeFromString<WordOfTheDayName>(
            responseBodyString.toString()
        )
        Log.d(
            TAG,
            "Word of the day name: ${wordOfTheDayName.header}"
        )
    }
}