package com.example.palabradeldiarae

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.random.Random

const val CHANNEL_ID = "9dfb4080-2c15-4121-a78c-092608d441a0"
private val TAG: String = WofdRae::class.java.getName();

class WofdRae(context: Context, params: WorkerParameters) : Worker(context, params) {

    private var notificationId = Random.nextInt()
    private val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun doWork(): Result {
        Log.d(TAG, "Task executed")
        createNotificationChannel()
        showNotification()
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                R.string.app_name.toString(),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
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
