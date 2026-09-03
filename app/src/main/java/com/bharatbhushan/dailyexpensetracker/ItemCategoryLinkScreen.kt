package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCategoryLinkScreen(
    itemMasterDao: ItemMasterDao,
    categoryMasterDao: CategoryMasterDao,
    onBack: () -> Unit
) {
    var innerScreen by remember { mutableStateOf<String?>(null) }
    when (innerScreen) {
        "categories" -> {
            ManageCategoriesScreen(categoryMasterDao, onBack = { innerScreen = null })
            return
        }
        "add_item" -> {
            AddCustomItemScreen(itemMasterDao, categoryMasterDao, onBack = { innerScreen = null })
            return
        }
    }

    val scope = rememberCoroutineScope()
    val allItems by itemMasterDao.getAllItems().collectAsState(initial = emptyList())
    val categories by categoryMasterDao.getActiveCategories().collectAsState(initial = emptyList())
    var search by remember { mutableStateOf("") }
    var editingItem by remember { mutableStateOf<ItemMaster?>(null) }
    val filtered = remember(allItems, search) {
        val query = search.trim()
        if (query.isBlank()) allItems else allItems.filter {
            it.nameHindi.contains(query, true) || it.nameEnglish.contains(query, true) ||
                it.searchAliases.contains(query, true) || it.category.contains(query, true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                title = { AppText("Items & Categories", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(onClick = { innerScreen = "categories" }, modifier = Modifier.weight(1f)) {
                        AppText("Manage Categories")
                    }
                    OutlinedButton(onClick = { innerScreen = "add_item" }, modifier = Modifier.weight(1f)) {
                        AppText("Add New Item")
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    label = { AppText("Item या Category खोजें") },
                    singleLine = true
                )
            }
            items(filtered, key = { it.id }) { item ->
                Card(onClick = { editingItem = item }, modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = {
                            AppText("${item.nameHindi} / ${item.nameEnglish}", fontWeight = FontWeight.Bold)
                        },
                        supportingContent = { AppText("Category: ${item.category}") },
                        leadingContent = { Icon(Icons.Default.Link, null) }
                    )
                }
            }
        }
    }

    editingItem?.let { item ->
        var selectedCategory by remember(item.id) { mutableStateOf(item.category) }
        AlertDialog(
            onDismissRequest = { editingItem = null },
            title = { AppText("Item की Category बदलें") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppText("${item.nameHindi} / ${item.nameEnglish}", fontWeight = FontWeight.Bold)
                    DynamicCategoryDropdown(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategoryTextChanged = { selectedCategory = it },
                        onCategorySelected = { selectedCategory = it }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = selectedCategory.isNotBlank(),
                    onClick = {
                        scope.launch {
                            itemMasterDao.updateItemCategory(item.id, selectedCategory)
                            editingItem = null
                        }
                    }
                ) { AppText("Link करें") }
            },
            dismissButton = {
                TextButton(onClick = { editingItem = null }) { AppText("Cancel") }
            }
        )
    }
}
