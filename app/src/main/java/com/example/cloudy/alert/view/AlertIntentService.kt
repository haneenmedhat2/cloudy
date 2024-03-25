package com.example.cloudy.alert.view

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.os.IBinder


class AlertIntentService : IntentService("AlertIntentService") {


    override fun onBind(intent: Intent?): IBinder? {
        return null

    }
    override fun onHandleIntent(intent: Intent?) {
        val inputUrl = intent?.getLongExtra("timeDifference",0L)
        startForeground(10,createNotification(this).build())
        val endTime=System.currentTimeMillis() +20*1000
        while (System.currentTimeMillis()< endTime){
            synchronized(this){
                Thread.sleep(endTime-System.currentTimeMillis())
            }
        }

    }


}