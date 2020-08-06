package org.covidwatch.android

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class SnoozeBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager = context?.getSystemService(NotificationManager::class.java)
        notificationManager?.cancelAll()
        CovidWatchTcnManager.snoozeActivated = Date()
    }
}