package com.example.palabradeldiarae

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Locale
import kotlin.random.Random

const val CHANNEL_ID = "9dfb4080-2c15-4121-a78c-092608d441a0"
private val TAG: String = Notification::class.java.getName()

class Notification(context: Context, params: WorkerParameters) : Worker(context, params) {

    private var notificationId = Random.nextInt()
    private val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun doWork(): Result {
        Log.d(TAG, "Task executed")
        val httpClient = HttpClient()

        httpClient.retrieveWordOfTheDay()
        createNotificationChannel()
        showNotification(httpClient)
        return Result.success()
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

    private fun showNotification(httpClient: HttpClient) {
        val wordOfTheDay = httpClient.wordOfTheDayName.header.replaceFirstChar{
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Palabra del día: $wordOfTheDay")
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