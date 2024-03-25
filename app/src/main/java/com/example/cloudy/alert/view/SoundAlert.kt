package com.example.cloudy.alert.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.cloudy.HomeActivity
import com.example.cloudy.R
import com.example.cloudy.alert.viewmodel.AlertViewModel
import com.example.cloudy.alert.viewmodel.AlertViewModelFactory
import com.example.cloudy.db.LocalDataSourceImp
import com.example.cloudy.model.WeatherRepositoryImp
import com.example.cloudy.network.WeatherRemoteDataSourceImp

class SoundAlert : BroadcastReceiver() {
    private lateinit var viewFactory: AlertViewModelFactory
    private lateinit var viewModel: AlertViewModel

    override fun onReceive(context: Context?, intent: Intent?) {

        viewFactory = AlertViewModelFactory(
            WeatherRepositoryImp.getInstance
                (WeatherRemoteDataSourceImp.getInstance(), LocalDataSourceImp(context!!))
        )

        viewModel = ViewModelProvider(AlertFragment::class.java, viewFactory).get(AlertViewModel::class.java)
        val notificationManager = NotificationManagerCompat.from(context!!)

        // Create notification channel for devices running Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "alarm_channel"
            val channelName = "Alarm Channel"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create notification with looping sound and dismiss action
        val alertDescription = intent!!.getStringExtra("alertDescription")
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val notificationBuilder = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.cloud)
            .setContentTitle("Weather Alert")
            .setContentText(alertDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(alarmUri)
            .setAutoCancel(true)
            .setOngoing(true)  // Make sure notification stays until dismissed
            .addAction(R.drawable.img_4, "Dismiss", getDismissIntent(context))

        // Show notification
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(1, notificationBuilder.build())

        startAlarmSound(context, alarmUri)
    }

    private fun startAlarmSound(context: Context, alarmUri: Uri) {
        val mediaPlayer = MediaPlayer.create(context, alarmUri)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    private fun getDismissIntent(context: Context): PendingIntent {
        val intent = Intent(context, HomeActivity::class.java)
        viewModel.deleteAlertData()
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}