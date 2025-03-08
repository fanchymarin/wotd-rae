package com.example.palabradeldiarae

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

private val TAG: String = MainActivity::class.java.getName();

class MainActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Activity created")

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Notification permission not granted")
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
                return@with
            }
        }

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            WofdRae::class.java,
            15, TimeUnit.MINUTES
        ).build()
        Log.d(TAG, "PeriodicWorkRequest created")

        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(periodicWorkRequest)
        Log.d(TAG, "PeriodicWorkRequest enqueued")
        finish()
    }
}

