package com.example.cloudy.alert.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.cloudy.HomeActivity

class DismissSoundAlert: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        SoundAlert.stopAlarmSound()
        // Cancel the notification
        val notificationManager = NotificationManagerCompat.from(context!!)
        notificationManager.cancel(SoundAlert.NOTIFICATION_ID)

        val homeIntent = Intent(context, HomeActivity::class.java)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(homeIntent)

    }
}
