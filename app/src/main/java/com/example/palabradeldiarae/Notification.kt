package com.example.palabradeldiarae

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.random.Random

const val CHANNEL_ID = "9dfb4080-2c15-4121-a78c-092608d441a0"
private val TAG: String = Notification::class.java.getName()

class Notification(context: Context, params: WorkerParameters) : Worker(context, params) {

    private var notificationId = Random.nextInt()
    private val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var wordOfTheDayName = ""
    private var wordOfTheDayDefinition = ""

    override fun doWork(): Result {
        Log.d(TAG, "Task executed")
        retrieveWordOfTheDay()
        createNotificationChannel()
        showNotification()
        return Result.success()
    }
    private fun retrieveWordOfTheDay() {
        Log.d(TAG, "Retrieving word of the day")
        val url = "https://dle.rae.es/data/wotd?callback=json"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Diccionario/2 CFNetwork/808.2.16 Darwin/16.3.0")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Authorization", "Basic cDY4MkpnaFMzOmFHZlVkQ2lFNDM0")
            .build()
        try {
            val response: Response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            // Si la respuesta contiene "callback=json(", necesitamos procesarla
            responseBody?.let { body ->
                if (body.startsWith("json(") && body.endsWith(")")) {
                    // Extraer solo el JSON quitando json( al inicio y ) al final
                    body.substring(5, body.length - 1)
                } else {
                    body
                }
            }
            Log.d(TAG, "Word of the day retrieved: $responseBody")
        } catch (e: IOException) {
            println("Error al realizar la petición: ${e.message}")
            null
        }

    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(applicationContext, R.string.app_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
        Log.d(TAG, "Notification channel created")
    }

    private fun showNotification() {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Palabra del día: Sororidad")
            .setContentText("Del ingl. sorority, este del lat. med... ")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Del ingl. sorority, este del lat. mediev. sororitas, -atis 'congregación de monjas', y este der. del lat. soror, -ōris 'hermana carnal'.\n" +
                        "\n" +
                        "1.    f. Amistad o afecto entre mujeres.\n" +
                        "2.    f. Relación de solidaridad entre las mujeres, especialmente en la lucha por su empoderamiento.\n" +
                        "3.    f. En los Estados Unidos de América, asociación estudiantil femenina que habitualmente cuenta con una residencia especial."))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        Log.d(TAG, "Notification created")

        notificationManager.notify(notificationId, notification)
        Log.d(TAG, "Notification with id $notificationId sent to notification manager")
        notificationId++
    }
}