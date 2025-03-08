package com.example.palabradeldiarae

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            WofdRae::class.java,
            15, TimeUnit.MINUTES
        ).build()

        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(periodicWorkRequest)
    }
}
