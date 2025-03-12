package com.example.palabradeldiarae

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.palabradeldiarae.SetAlarm.Companion.setAlarm

private val TAG: String = BootReceiver::class.java.getName()

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.intent.action.BOOT_COMPLETED")
            return
        Log.d(TAG, "Setting alarm on boot")
        setAlarm(context)
    }
}