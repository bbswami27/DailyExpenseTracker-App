package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    selectedTheme: AppThemeMode,
    onThemeSelected: (AppThemeMode) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                title = {
                    AppText(
                        text = "Theme Settings",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AppText(
                text = "App Theme चुनें",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            AppText(
                text = "चुनी हुई theme App बंद करने के बाद भी save रहेगी।",
                fontSize = 13.sp
            )

            ThemeOptionCard(
                title = "Royal Gold",
                subtitle = "Black और Gold premium theme",
                themeMode = AppThemeMode.ROYAL_GOLD,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected
            )

            ThemeOptionCard(
                title = "Midnight Blue",
                subtitle = "Navy Blue और Cyan modern theme",
                themeMode = AppThemeMode.MIDNIGHT_BLUE,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected
            )

            ThemeOptionCard(
                title = "Rose Gold",
                subtitle = "Black और Copper luxury theme",
                themeMode = AppThemeMode.ROSE_GOLD,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected
            )

            ThemeOptionCard(
                title = "Smart Light",
                subtitle = "White, Blue और Green clean theme",
                themeMode = AppThemeMode.SMART_LIGHT,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected
            )

            ThemeOptionCard(
                title = "Pastel Playful",
                subtitle = "Mint Green, Orange और Blue colorful theme",
                themeMode = AppThemeMode.PASTEL_PLAYFUL,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    subtitle: String,
    themeMode: AppThemeMode,
    selectedTheme: AppThemeMode,
    onThemeSelected: (AppThemeMode) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onThemeSelected(themeMode)
            },
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = selectedTheme == themeMode,
                onClick = {
                    onThemeSelected(themeMode)
                }
            )

            Column(
                modifier = Modifier.padding(start = 10.dp)
            ) {

                AppText(
                    text = title,
                    fontWeight = FontWeight.Bold
                )

                AppText(
                    text = subtitle,
                    fontSize = 12.sp
                )
            }
        }
    }
}
