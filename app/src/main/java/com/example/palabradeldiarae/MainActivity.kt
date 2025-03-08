package com.example.palabradeldiarae

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

private val TAG: String = MainActivity::class.java.getName();

class MainActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Activity created")
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            WofdRae::class.java,
            15, TimeUnit.MINUTES
        ).build()
        Log.d(TAG, "PeriodicWorkRequest created")

        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(periodicWorkRequest)
        Log.d(TAG, "PeriodicWorkRequest enqueued")
    }
}

