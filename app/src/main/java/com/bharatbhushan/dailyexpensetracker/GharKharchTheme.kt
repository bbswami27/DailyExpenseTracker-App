package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
    CASHFLOW_TEAL,
    ROYAL_GOLD,
    MIDNIGHT_BLUE,
    ROSE_GOLD,
    SMART_LIGHT,
    PASTEL_PLAYFUL
}

private val CashflowTealColors = lightColorScheme(
    primary = Color(0xFF006B5E), onPrimary = Color.White,
    primaryContainer = Color(0xFFA7F2DF), onPrimaryContainer = Color(0xFF00201B),
    secondary = Color(0xFFD9480F), onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBCD), onSecondaryContainer = Color(0xFF351000),
    background = Color(0xFFF3FBF8), onBackground = Color(0xFF16201D),
    surface = Color.White, onSurface = Color(0xFF16201D),
    surfaceVariant = Color(0xFFDCEAE5), onSurfaceVariant = Color(0xFF3F4945),
    outline = Color(0xFF6F7975), error = Color(0xFFBA1A1A)
)

private val RoyalGoldColors = darkColorScheme(
    primary = Color(0xFFFFC857),
    onPrimary = Color(0xFF211800),
    primaryContainer = Color(0xFF5A4300),
    onPrimaryContainer = Color(0xFFFFE8A3),
    secondary = Color(0xFFD6B875),
    background = Color(0xFF080A0D),
    onBackground = Color(0xFFF7F2E8),
    surface = Color(0xFF111418),
    onSurface = Color(0xFFF7F2E8),
    surfaceVariant = Color(0xFF1C2025),
    onSurfaceVariant = Color(0xFFD7D0C3),
    outline = Color(0xFF8D7B55),
    error = Color(0xFFFF6B6B)
)

private val MidnightBlueColors = darkColorScheme(
    primary = Color(0xFF42C8FF),
    onPrimary = Color(0xFF001F2A),
    primaryContainer = Color(0xFF004D66),
    onPrimaryContainer = Color(0xFFBCEBFF),
    secondary = Color(0xFF65E6D4),
    background = Color(0xFF020B18),
    onBackground = Color(0xFFE8F3FF),
    surface = Color(0xFF081525),
    onSurface = Color(0xFFE8F3FF),
    surfaceVariant = Color(0xFF10243A),
    onSurfaceVariant = Color(0xFFC4D8EA),
    outline = Color(0xFF4B7895),
    error = Color(0xFFFF7070)
)

private val RoseGoldColors = darkColorScheme(
    primary = Color(0xFFFFB69E),
    onPrimary = Color(0xFF351006),
    primaryContainer = Color(0xFF703522),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = Color(0xFFEAB9A7),
    background = Color(0xFF0E0908),
    onBackground = Color(0xFFFFF1EC),
    surface = Color(0xFF1A1210),
    onSurface = Color(0xFFFFF1EC),
    surfaceVariant = Color(0xFF2A1C19),
    onSurfaceVariant = Color(0xFFE4C7BE),
    outline = Color(0xFF9A6C5E),
    error = Color(0xFFFF6F6F)
)
private val SmartLightColors = lightColorScheme(

    primary = Color(0xFF1368CE),
    onPrimary = Color.White,

    primaryContainer = Color(0xFFD9E8FF),
    onPrimaryContainer = Color(0xFF001B3E),

    secondary = Color(0xFF008C72),
    onSecondary = Color.White,

    secondaryContainer = Color(0xFFC5F3E6),
    onSecondaryContainer = Color(0xFF002019),

    background = Color(0xFFF6F9FF),
    onBackground = Color(0xFF171C24),

    surface = Color.White,
    onSurface = Color(0xFF171C24),

    surfaceVariant = Color(0xFFE7EDF5),
    onSurfaceVariant = Color(0xFF424A55),

    outline = Color(0xFF727A86),

    error = Color(0xFFBA1A1A),
    onError = Color.White
)
private val PastelPlayfulColors = lightColorScheme(

    primary = Color(0xFF38B875),
    onPrimary = Color.White,

    primaryContainer = Color(0xFFCFF5DD),
    onPrimaryContainer = Color(0xFF073D24),

    secondary = Color(0xFFFF835C),
    onSecondary = Color.White,

    secondaryContainer = Color(0xFFFFD8C9),
    onSecondaryContainer = Color(0xFF4A1607),

    tertiary = Color(0xFF4D9BE8),
    onTertiary = Color.White,

    background = Color(0xFFFFFAEE),
    onBackground = Color(0xFF20231F),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF20231F),

    surfaceVariant = Color(0xFFEAF7F0),
    onSurfaceVariant = Color(0xFF47534B),

    outline = Color(0xFF83A393),

    error = Color(0xFFBA1A1A)
)

@Composable
fun GharKharchTheme(
    themeMode: AppThemeMode = AppThemeMode.CASHFLOW_TEAL,
    content: @Composable () -> Unit
) {
    val selectedColors = when (themeMode) {
        AppThemeMode.CASHFLOW_TEAL -> CashflowTealColors
        AppThemeMode.ROYAL_GOLD -> RoyalGoldColors
        AppThemeMode.MIDNIGHT_BLUE -> MidnightBlueColors
        AppThemeMode.ROSE_GOLD -> RoseGoldColors
        AppThemeMode.SMART_LIGHT -> SmartLightColors
        AppThemeMode.PASTEL_PLAYFUL -> PastelPlayfulColors
    }

    MaterialTheme(
        colorScheme = selectedColors,
        content = content
    )
}

fun loadSavedTheme(
    context: Context
): AppThemeMode {

    val preferences = context.getSharedPreferences(
        "ghar_kharch_settings",
        Context.MODE_PRIVATE
    )

    val savedName = preferences.getString(
        "selected_theme",
        AppThemeMode.CASHFLOW_TEAL.name
    )

    return try {
        AppThemeMode.valueOf(
            savedName ?: AppThemeMode.CASHFLOW_TEAL.name
        )
    } catch (_: Exception) {
        AppThemeMode.CASHFLOW_TEAL
    }
}

fun saveSelectedTheme(
    context: Context,
    themeMode: AppThemeMode
) {
    context.getSharedPreferences(
        "ghar_kharch_settings",
        Context.MODE_PRIVATE
    )
        .edit()
        .putString(
            "selected_theme",
            themeMode.name
        )
        .apply()
}
