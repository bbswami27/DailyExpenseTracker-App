package com.bharatbhushan.dailyexpensetracker

import android.content.Context

object AppLanguageManager {

    private const val PREFS =
        "ghar_budget_language"

    fun load(
        context: Context
    ): AppLanguage {

        val code = context
            .getSharedPreferences(PREFS, 0)
            .getString("language", "en")

        return when (code) {
            "hi" -> AppLanguage.HINDI
            else -> AppLanguage.ENGLISH
        }
    }

    fun save(
        context: Context,
        language: AppLanguage
    ) {
        context
            .getSharedPreferences(PREFS, 0)
            .edit()
            .putString(
                "language",
                language.code
            )
            .apply()
    }
}