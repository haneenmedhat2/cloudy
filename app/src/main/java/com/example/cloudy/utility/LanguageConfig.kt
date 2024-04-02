package com.example.cloudy.utility

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager
import java.util.Locale

class LanguageConfig {
    companion object {
        fun changeLanguage(context: Context, languageCode: String): ContextWrapper {
            var contextToUse = context // Creating a mutable copy of context

            val resources: Resources = contextToUse.resources
            val configuration: Configuration = resources.configuration
            val systemLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.locales.get(0)
            } else {
                configuration.locale
            }
            if (languageCode.isNotEmpty() && systemLocale.language != languageCode) {
                val locale = Locale(languageCode)
                Locale.setDefault(locale)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    configuration.setLocale(locale)
                } else {
                    configuration.locale = locale
                }
                contextToUse = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    contextToUse.createConfigurationContext(configuration)
                } else {
                    contextToUse.resources.updateConfiguration(
                        configuration,
                        contextToUse.resources.displayMetrics
                    )
                    contextToUse
                }
            }
            return ContextWrapper(contextToUse)
        }
    }
}