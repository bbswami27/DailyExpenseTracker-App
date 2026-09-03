package com.bharatbhushan.dailyexpensetracker

import android.content.Context

object GuestModeManager {
    private const val PREFS = "ghar_kharch_guest_mode"
    private const val KEY = "enabled"

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY, false)

    fun enable(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY, true).apply()
    }

    fun disable(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY, false).apply()
    }
}
