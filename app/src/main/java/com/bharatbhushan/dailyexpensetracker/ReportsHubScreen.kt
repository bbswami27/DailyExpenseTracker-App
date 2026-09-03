package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsHubScreen(
    onOpenOverview: () -> Unit,
    onOpenCashOutHistory: () -> Unit,
    onOpenCashInHistory: () -> Unit,
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
                title = { AppText("Reports", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportOption("Reports & Analysis", "Category, item, party और date-wise reports", Icons.Default.Assessment, onOpenOverview)
            ReportOption("Cash Out History", "सभी Cash Out entries देखें, edit या delete करें", Icons.Default.CallMade, onOpenCashOutHistory)
            ReportOption("Cash In History", "सभी Cash In entries एक जगह देखें", Icons.Default.CallReceived, onOpenCashInHistory)
        }
    }
}

@Composable
private fun ReportOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { AppText(title, fontWeight = FontWeight.Bold) },
            supportingContent = { AppText(subtitle) },
            leadingContent = { Icon(icon, null) }
        )
    }
}
