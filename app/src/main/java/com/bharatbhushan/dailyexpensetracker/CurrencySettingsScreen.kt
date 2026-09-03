package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySettingsScreen(
    selectedCurrency: AppCurrency,
    onCurrencySelected: (AppCurrency) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                title = { AppText("Select Currency", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { AppText("चुनी हुई currency सभी amounts, reports और exports पर लागू होगी।") }
            items(AppCurrency.entries, key = { it.code }) { currency ->
                Card(
                    onClick = { onCurrencySelected(currency) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = {
                            AppText("${currency.symbol}  ${currency.displayName}", fontWeight = FontWeight.Bold)
                        },
                        supportingContent = { AppText(currency.code) },
                        leadingContent = { Icon(Icons.Default.CurrencyExchange, null) },
                        trailingContent = {
                            if (currency == selectedCurrency) Icon(Icons.Default.Check, "Selected")
                        }
                    )
                }
            }
        }
    }
}
