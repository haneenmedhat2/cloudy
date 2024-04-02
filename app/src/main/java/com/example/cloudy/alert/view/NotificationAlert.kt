package com.example.cloudy.alert.view

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.cloudy.HomeActivity
import com.example.cloudy.R

const val notificationID= 1
const val channelID="CHANNEL_ID"
private const val TAG = "NotificationAlert"
class NotificationAlert : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationIntent = Intent(context, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alertDescription = intent!!.getStringExtra("alertDescription")
        Log.i(TAG, "onViewCreated: $alertDescription")
        val builder = NotificationCompat.Builder(context!!, channelID)
            .setContentTitle("Weather Alert")
            .setContentText(alertDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.cloud)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, builder)
    }
}