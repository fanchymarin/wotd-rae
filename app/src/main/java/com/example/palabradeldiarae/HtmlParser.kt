package com.example.palabradeldiarae

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

private val TAG: String = HtmlParser::class.java.getName()

class HtmlParser {

    fun parseDefinition(html: String): String {
        val doc: Document = Jsoup.parse(html)

        Log.d(TAG, "Parsing definition from HTML page")
        val descriptionElement: Elements = doc.select("meta[name=description]")
        return if (descriptionElement.isNotEmpty()) {
            descriptionElement.attr("content")
                .replace(Regex("Definición RAE de «.*?» según el Diccionario de la lengua española:"), "")
                .replace(Regex("[0-9]+."), "\n$0")
                .trim()
        } else {
            Log.w(TAG, "No description element found in HTML")
            "Definición no encontrada"
        }
    }
}