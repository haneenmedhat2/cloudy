package com.example.cloudy.alert.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import com.example.cloudy.HomeActivity
import com.example.cloudy.R

private const val CHANNEL_ID="CHANNEL_ID"
const val NOTIFICATION_PERM=1023

fun createNotification(context: Context):NotificationCompat.Builder{
     val intent= Intent(context,HomeActivity::class.java)
    val pendingIntent=PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // notification channels were introduced in Oreo.
        val serviceChannel = NotificationChannel(//create channel with specified name,id,importance level
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager: NotificationManager =
            context.getSystemService(NotificationManager::class.java) as NotificationManager
        manager.createNotificationChannel(serviceChannel) //register the channel to the system
    }
    val builder=NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("Foreground Service")
        .setContentText("Downloading and saving image...")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
    return builder
}

