package com.example.palabradeldiarae

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import com.example.palabradeldiarae.SetAlarm.Companion.setAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.random.Random

const val CHANNEL_ID = "9dfb4080-2c15-4121-a78c-092608d441a0"
private val TAG: String = NotificationService::class.java.getName()

class NotificationService() : BroadcastReceiver() {

    private var notificationId = Random.nextInt()
    private lateinit var applicationContext: Context
    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "Task executed")
        applicationContext = context
        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        createNotificationChannel()
        CoroutineScope(Dispatchers.IO).launch {
            val httpClient = HttpClient()

            val wordResult = async { httpClient.retrieveWordOfTheDay(applicationContext) }

            wordResult.await()

            withContext(Dispatchers.Main) {
                showNotification(httpClient)
                setAlarm(context)
            }
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

    private fun showNotification(httpClient: HttpClient) {
        val wordOfTheDayName = httpClient.wordOfTheDayName.header.replaceFirstChar{
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val wordOfTheDayDefinition = httpClient.wordOfTheDayDefinition

        try {
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle("Palabra del día: $wordOfTheDayName")
                .setContentText(wordOfTheDayDefinition)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(wordOfTheDayDefinition))
                .setSmallIcon(R.drawable.dictionary)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
            Log.d(TAG, "Notification created")

            notificationManager.notify(notificationId, notification)
            Log.d(TAG, "Notification with id $notificationId sent to notification manager")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification: ${e.message}")
        }
    }
}