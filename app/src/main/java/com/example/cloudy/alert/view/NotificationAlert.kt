package com.example.cloudy.alert.view

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.cloudy.HomeActivity
import com.example.cloudy.R

const val notificationID= 1
const val channelID="CHANNEL_ID"
class NotificationAlert : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationIntent = Intent(context, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE // You might use another flag if needed
        )

        val builder = NotificationCompat.Builder(context!!, channelID)
            .setContentTitle("Foreground Service")
            .setContentText("Downloading and saving image...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, builder)
    }
}