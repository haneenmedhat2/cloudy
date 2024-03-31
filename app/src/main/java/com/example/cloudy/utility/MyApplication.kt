package com.example.cloudy.utility

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.example.cloudy.settings.SettingsFragment

class MyApplication: Application() {
 /*   lateinit var context:Context

    override fun attachBaseContext(newBase: Context?) {

        var language= SettingsFragment.languageSP
        if (language){
            context=LanguageConfig.changeLanguage(newBase!!,"en")
        }
        else {
            context=LanguageConfig.changeLanguage(newBase!!,"ar")
        }
        super.attachBaseContext(context)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LanguageConfig.setLocale(this)
    }*/
}