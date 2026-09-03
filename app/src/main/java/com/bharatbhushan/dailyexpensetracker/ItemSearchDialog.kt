package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ItemSearchDialog(
    category: String,
    itemMasterDao: ItemMasterDao,
    onItemSelected: (ItemMaster) -> Unit,
    onDismiss: () -> Unit
) {

    var searchText by remember {
        mutableStateOf("")
    }

    var searchAllCategories by remember {
        mutableStateOf(false)
    }

    val categoryFilter =
        if (searchAllCategories) {
            ""
        } else {
            category
        }

    val itemFlow = remember(
        categoryFilter,
        searchText
    ) {

        itemMasterDao.searchItems(
            category = categoryFilter,
            search = searchText.trim()
        )
    }

    val items by itemFlow.collectAsState(
        initial = emptyList()
    )

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            AppText("Item चुनें")
        },

        text = {

            Column {

                OutlinedTextField(
                    value = searchText,

                    onValueChange = {
                        searchText = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        AppText("Hindi, English या Hinglish में खोजें")
                    },

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },

                    singleLine = true
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            searchAllCategories =
                                !searchAllCategories
                        },

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Checkbox(
                        checked = searchAllCategories,

                        onCheckedChange = {
                            searchAllCategories = it
                        }
                    )

                    AppText(
                        text = "सभी श्रेणियों में खोजें / All Categories"
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = 150.dp,
                            max = 420.dp
                        )
                        .padding(top = 8.dp)
                ) {

                    if (items.isEmpty()) {

                        item {
                            AppText(
                                text = "कोई item नहीं मिला",
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                    } else {

                        items(
                            items = items,
                            key = { item -> item.id }
                        ) { item ->

                            ListItem(
                                headlineContent = {
                                    AppText(
                                        "${item.nameHindi} / " +
                                                item.nameEnglish
                                    )
                                },

                                supportingContent = {
                                    Column {

                                        AppText(
                                            text = item.category
                                        )

                                        AppText(
                                            text =
                                                "Default Unit: " +
                                                        item.defaultUnit
                                        )
                                    }
                                },

                                modifier = Modifier.clickable {
                                    onItemSelected(item)
                                }
                            )

                            HorizontalDivider()
                        }
                    }
                }
            }
        },

        confirmButton = {},

        dismissButton = {
            TextButton(onClick = onDismiss) {
                AppText("बंद करें")
            }
        }
    )
}
