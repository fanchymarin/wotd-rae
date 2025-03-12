package com.example.palabradeldiarae

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.palabradeldiarae.SetAlarm.Companion.setAlarm

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.intent.action.BOOT_COMPLETED")
            return
        setAlarm(context)
    }
}