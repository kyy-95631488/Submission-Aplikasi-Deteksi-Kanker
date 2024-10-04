package com.dicoding.asclepius.helper

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object PreferenceManager {
    private const val PREF_NAME = "user_settings"
    private const val DARK_MODE_KEY = "dark_mode"

    fun initialize(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false)
        setDarkMode(isDarkMode, context) // Pass context here
    }

    fun setDarkMode(isDarkMode: Boolean, context: Context) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(DARK_MODE_KEY, isDarkMode)
        editor.apply()

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun isDarkModeEnabled(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(DARK_MODE_KEY, false)
    }

}
