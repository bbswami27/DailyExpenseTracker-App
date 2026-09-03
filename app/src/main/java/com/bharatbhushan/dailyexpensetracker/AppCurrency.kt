package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import java.text.NumberFormat
import java.util.Locale

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val displayName: String,
    val locale: Locale,
    val fractionDigits: Int = 2
) {
    INR("INR", "₹", "Indian Rupee", Locale("en", "IN")),
    USD("USD", "$", "US Dollar", Locale.US),
    EUR("EUR", "€", "Euro", Locale.GERMANY),
    GBP("GBP", "£", "British Pound", Locale.UK),
    AED("AED", "د.إ", "UAE Dirham", Locale("en", "AE")),
    SAR("SAR", "ر.س", "Saudi Riyal", Locale("en", "SA")),
    CAD("CAD", "C$", "Canadian Dollar", Locale.CANADA),
    AUD("AUD", "A$", "Australian Dollar", Locale("en", "AU")),
    SGD("SGD", "S$", "Singapore Dollar", Locale("en", "SG")),
    JPY("JPY", "¥", "Japanese Yen", Locale.JAPAN, 0),
    CNY("CNY", "CN¥", "Chinese Yuan", Locale.CHINA),
    NPR("NPR", "रु", "Nepalese Rupee", Locale("en", "NP"))
}

object AppCurrencyManager {
    private const val PREFS = "ghar_kharch_currency_settings"
    private const val KEY = "selected_currency"

    @Volatile
    var current: AppCurrency = AppCurrency.INR
        private set

    fun load(context: Context): AppCurrency {
        val code = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, AppCurrency.INR.code)
        current = AppCurrency.entries.firstOrNull { it.code == code } ?: AppCurrency.INR
        return current
    }

    fun save(context: Context, currency: AppCurrency) {
        current = currency
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, currency.code).apply()
    }
}

fun currentCurrencySymbol(): String = AppCurrencyManager.current.symbol

fun formatCurrencyAmount(amount: Double): String {
    val currency = AppCurrencyManager.current
    val formatter = NumberFormat.getNumberInstance(currency.locale).apply {
        maximumFractionDigits = currency.fractionDigits
        minimumFractionDigits = 0
    }
    return "${currency.symbol}${formatter.format(amount)}"
}
