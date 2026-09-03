package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListsScreen(
    shoppingListDao: ShoppingListDao,
    onBack: () -> Unit,
    onOpenList: (ShoppingList) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val shoppingLists by shoppingListDao
        .getAllShoppingLists()
        .collectAsState(initial = emptyList())

    var showCreateDialog by remember {
        mutableStateOf(false)
    }

    var listName by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                title = {
                    AppText(
                        text = "Shopping Lists",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    listName = ""
                    showCreateDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Shopping List"
                )
            }
        }
    ) { padding ->

        if (shoppingLists.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AppText(
                    text = "अभी कोई Shopping List नहीं है",
                    fontWeight = FontWeight.Bold
                )

                AppText(
                    text = "नई list बनाने के लिए + दबाएँ"
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),

                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = shoppingLists,
                    key = { list -> list.id }
                ) { shoppingList ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOpenList(shoppingList)
                            },

                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),

                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                AppText(
                                    text = shoppingList.name,
                                    fontWeight = FontWeight.Bold
                                )

                                AppText(
                                    text = formatSelectedDate(
                                        shoppingList.createdAt
                                    )
                                )
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        shoppingListDao
                                            .deleteShoppingList(
                                                shoppingList
                                            )
                                    }
                                }
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete List"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {

        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
            },

            title = {
                AppText("नई Shopping List")
            },

            text = {
                OutlinedTextField(
                    value = listName,

                    onValueChange = {
                        listName = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        AppText("List का नाम")
                    },

                    placeholder = {
                        AppText("जैसे: महीने का राशन")
                    },

                    singleLine = true
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        if (listName.isNotBlank()) {

                            coroutineScope.launch {

                                val newListId =
                                    shoppingListDao
                                        .insertShoppingList(
                                            ShoppingList(
                                                name = listName.trim()
                                            )
                                        )

                                showCreateDialog = false

                                onOpenList(
                                    ShoppingList(
                                        id = newListId.toInt(),
                                        name = listName.trim()
                                    )
                                )
                            }
                        }
                    }
                ) {
                    AppText("List बनाएँ")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateDialog = false
                    }
                ) {
                    AppText("रद्द करें")
                }
            }
        )
    }
}
