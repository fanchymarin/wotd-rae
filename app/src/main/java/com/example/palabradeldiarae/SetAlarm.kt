package com.example.palabradeldiarae

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import java.util.Calendar

private val TAG: String = SetAlarm::class.java.getName()

class SetAlarm {

    companion object {
        fun setAlarm(context: Context) {
            try {
                val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(context, NotificationService::class.java).let { intent ->
                    PendingIntent.getBroadcast(
                        context, 0, intent,
                        PendingIntent.FLAG_IMMUTABLE or
                                PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                Log.d(TAG, "Setting alarm")

                val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    alarmIntent
                )
                Log.d(TAG, "Alarm set for ${calendar.time}")

            } catch (e: Exception) {
                Log.e(TAG, "Error setting alarm: ${e.message}")
            }
        }
    }
}