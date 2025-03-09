package com.example.palabradeldiarae

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class HtmlParser {
    data class Meaning(
        val number: String,
        val definition: String,
        val synonyms: List<String>
    )

    fun parseMeaningsAndSynonyms(html: String): String {
        val doc: Document = Jsoup.parse(html)
        val meanings = mutableListOf<Meaning>()
        val meaningsHtml = doc.select("p.j")

        meaningsHtml.forEachIndexed { index, meaningElement ->
            val numberMeaning = meaningElement.select("span.n_acep").text().trim()
            val completeDefinition = meaningElement.text().replace(numberMeaning, "").trim()
            
            val divSynonyms = meaningElement.nextElementSibling()
            val listSynonyms = mutableListOf<String>()

            if (divSynonyms != null && divSynonyms.hasClass("sin-header")) {
                val synonymElements = divSynonyms.select("mark.sin")
                synonymElements.forEach { synonym ->
                    listSynonyms.add(synonym.text().trim())
                }
            }

            meanings.add(Meaning(numberMeaning, completeDefinition, listSynonyms))
        }

        val result = StringBuilder()

        meanings.forEach { meaning ->
            result.append("${meaning.number} ${meaning.definition}\n")
            if (meaning.synonyms.isNotEmpty()) {
                result.append("Sinónimos: ${meaning.synonyms.joinToString(", ")}\n")
            }
            result.append("\n")
        }

        return result.toString()
    }
}