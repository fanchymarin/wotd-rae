package com.example.palabradeldiarae

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import java.util.Calendar

private val TAG: String = SetAlarm::class.java.getName()
private const val INTENT_REQUEST_CODE = 0

class SetAlarm {

    companion object {
        fun setAlarm(context: Context, dateAdded: Int = 1) {
            try {
                val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(context, NotificationService::class.java).let { intent ->
                    PendingIntent.getBroadcast(
                        context, INTENT_REQUEST_CODE, intent,
                        PendingIntent.FLAG_IMMUTABLE or
                                PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                Log.d(TAG, "Setting alarm")

                val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.DATE, get(Calendar.DATE) + dateAdded)
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    alarmIntent
                )
                Log.d(TAG, "Alarm set for ${calendar.time}")

            } catch (e: Exception) {
                Log.e(TAG, "Error setting alarm: ${e.message}")
            }
        }
    }
}