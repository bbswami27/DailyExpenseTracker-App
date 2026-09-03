package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsScreen(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, appText("back", selectedLanguage))
                    }
                },
                title = {
                    AppText(appText("select_language", selectedLanguage), fontWeight = FontWeight.Bold)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { AppText(appText("language_hint", selectedLanguage)) }
            items(
                listOf(
                    AppLanguage.ENGLISH,
                    AppLanguage.HINDI
                ),
                key = { it.code }
            ) { language ->
                Card(
                    onClick = { onLanguageSelected(language) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { AppText(language.displayName, fontWeight = FontWeight.Bold) },
                        supportingContent = { AppText(language.code.uppercase()) },
                        leadingContent = { Icon(Icons.Default.Language, null) },
                        trailingContent = {
                            if (language == selectedLanguage) Icon(Icons.Default.Check, "Selected")
                        }
                    )
                }
            }
        }
    }
}
