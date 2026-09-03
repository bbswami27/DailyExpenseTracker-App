package com.bharatbhushan.dailyexpensetracker

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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesScreen(
    categoryMasterDao: CategoryMasterDao,
    onBack: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val categories by categoryMasterDao
        .getActiveCategories()
        .collectAsState(initial = emptyList())

    var editingCategory by remember {
        mutableStateOf<CategoryMaster?>(null)
    }

    var showCategoryDialog by remember {
        mutableStateOf(false)
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
                        text = "खर्च की Categories",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingCategory = null
                    showCategoryDialog = true
                }
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Category"
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),

            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {

                AppText(
                    text = "नई Category के लिए + दबाएँ",
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }

            items(
                items = categories,
                key = { category -> category.id }
            ) { category ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            AppText(
                                text = category.nameHindi,
                                fontWeight = FontWeight.Bold
                            )

                            AppText(
                                text = category.nameEnglish
                            )

                            if (category.isCustom) {
                                AppText("Custom Category")
                            } else {
                                AppText("Default Category")
                            }
                        }

                        IconButton(
                            onClick = {
                                editingCategory = category
                                showCategoryDialog = true
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Category"
                            )
                        }

                        if (category.isCustom) {

                            IconButton(
                                onClick = {

                                    coroutineScope.launch {

                                        categoryMasterDao
                                            .deactivateCategory(
                                                category.id
                                            )
                                    }
                                }
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription =
                                        "Deactivate Category"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCategoryDialog) {

        CategoryEditorDialog(
            category = editingCategory,

            onSave = { hindi, english, aliases ->

                coroutineScope.launch {

                    if (editingCategory == null) {

                        categoryMasterDao.insertCategory(
                            CategoryMaster(
                                nameHindi = hindi,
                                nameEnglish = english,
                                searchAliases = aliases
                            )
                        )

                    } else {

                        categoryMasterDao.updateCategory(
                            editingCategory!!.copy(
                                nameHindi = hindi,
                                nameEnglish = english,
                                searchAliases = aliases
                            )
                        )
                    }

                    showCategoryDialog = false
                    editingCategory = null
                }
            },

            onDismiss = {
                showCategoryDialog = false
                editingCategory = null
            }
        )
    }
}

@Composable
fun CategoryEditorDialog(
    category: CategoryMaster?,
    onSave: (
        hindi: String,
        english: String,
        aliases: String
    ) -> Unit,
    onDismiss: () -> Unit
) {

    var hindiName by remember(category?.id) {
        mutableStateOf(
            category?.nameHindi ?: ""
        )
    }

    var englishName by remember(category?.id) {
        mutableStateOf(
            category?.nameEnglish ?: ""
        )
    }

    var aliases by remember(category?.id) {
        mutableStateOf(
            category?.searchAliases ?: ""
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            AppText(
                if (category == null) {
                    "नई Category"
                } else {
                    "Category Edit करें"
                }
            )
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                OutlinedTextField(
                    value = hindiName,

                    onValueChange = {
                        hindiName = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        AppText("हिंदी नाम")
                    },

                    singleLine = true
                )

                OutlinedTextField(
                    value = englishName,

                    onValueChange = {
                        englishName = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        AppText("English Name")
                    },

                    singleLine = true
                )

                OutlinedTextField(
                    value = aliases,

                    onValueChange = {
                        aliases = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        AppText("Hinglish Search Names")
                    },

                    placeholder = {
                        AppText("जैसे: education padhai")
                    }
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    if (
                        hindiName.isNotBlank() &&
                        englishName.isNotBlank()
                    ) {

                        onSave(
                            hindiName.trim(),
                            englishName.trim(),
                            aliases.trim()
                        )
                    }
                }
            ) {

                AppText("Save")
            }
        },

        dismissButton = {

            TextButton(onClick = onDismiss) {
                AppText("रद्द करें")
            }
        }
    )
}
