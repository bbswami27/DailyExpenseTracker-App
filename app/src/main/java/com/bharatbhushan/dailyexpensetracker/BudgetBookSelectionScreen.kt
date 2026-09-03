package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetBookSelectionScreen(
    userId: String,
    onBookSelected: (BudgetBook) -> Unit
) {
    val context = LocalContext.current
    var books by remember(userId) {
        mutableStateOf(BudgetBookManager.getBooks(context, userId))
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { AppText("घर बजट चुनें", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "Add Budget Book")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                AppText("एक user के अंदर अलग-अलग घरों का हिसाब अलग रखें।")
            }
            items(books, key = { it.id }) { book ->
                Card(
                    onClick = {
                        BudgetBookManager.selectBook(context, userId, book)
                        onBookSelected(book)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { AppText(book.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { AppText("इस घर बजट को खोलें") },
                        leadingContent = { Icon(Icons.Default.Home, null) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { AppText("नया घर बजट") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { AppText("नाम, जैसे माता-पिता का घर") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    enabled = newName.isNotBlank(),
                    onClick = {
                        val book = BudgetBookManager.addBook(context, userId, newName)
                        books = BudgetBookManager.getBooks(context, userId)
                        showAddDialog = false
                        onBookSelected(book)
                    }
                ) { AppText("जोड़ें और खोलें") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { AppText("रद्द करें") }
            }
        )
    }
}
