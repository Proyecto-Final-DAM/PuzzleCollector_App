package com.janicolas.puzzlecollector.util

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.os.LocaleListCompat

class SettingsChangeListener: SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if(key == "mode"){
            val prefs = sharedPreferences.getString(key, "0")

            when(prefs?.toInt()){
                1 -> setDefaultNightMode(MODE_NIGHT_NO)
                2 -> setDefaultNightMode(MODE_NIGHT_YES)
                else -> setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            }
        } else if(key == "language"){
            when(sharedPreferences.getString(key, "es")){
                "en" -> {
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("en")
                    setApplicationLocales(appLocale)
                }
                else -> {
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("es")
                    setApplicationLocales(appLocale)
                }
            }
        }
    }
}