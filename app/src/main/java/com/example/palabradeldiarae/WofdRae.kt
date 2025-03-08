package com.example.palabradeldiarae

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

const val CHANNEL_ID = "9dfb4080-2c15-4121-a78c-092608d441a0"

class WofdRae(context: Context, params: WorkerParameters) : Worker(context, params) {

    private var notificationId = 0

    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                R.string.app_name.toString(),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(R.string.app_name.toString())
            .setContentText("haiorjuaihapirhipaerjaerae ")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Del ingl. sorority, este del lat. mediev. sororitas, -atis 'congregación de monjas', y este der. del lat. soror, -ōris 'hermana carnal'.\n" +
                        "\n" +
                        "    f. Amistad o afecto entre mujeres.\n" +
                        "    f. Relación de solidaridad entre las mujeres, especialmente en la lucha por su empoderamiento.\n" +
                        "    f. En los Estados Unidos de América, asociación estudiantil femenina que habitualmente cuenta con una residencia especial."))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(notificationId++, notification)
    }
}
