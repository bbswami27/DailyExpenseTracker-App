package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun AddCustomItemScreen(
    itemMasterDao: ItemMasterDao,
    categoryMasterDao: CategoryMasterDao,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val categories by categoryMasterDao
        .getActiveCategories()
        .collectAsState(initial = emptyList())

    var nameHindi by remember {
        mutableStateOf("")
    }

    var nameEnglish by remember {
        mutableStateOf("")
    }

    var searchAliases by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("")
    }

    var defaultUnit by remember {
        mutableStateOf("Piece")
    }

    LaunchedEffect(categories) {
        if (
            selectedCategory.isBlank() &&
            categories.isNotEmpty()
        ) {
            selectedCategory = categories.first().nameHindi
        }
    }

    val formIsValid =
        nameHindi.isNotBlank() &&
                nameEnglish.isNotBlank() &&
                selectedCategory.isNotBlank() &&
                defaultUnit.isNotBlank()

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
                        text = "नया Item जोड़ें",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            OutlinedTextField(
                value = nameHindi,
                onValueChange = {
                    nameHindi = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    AppText("Item का हिंदी नाम")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = nameEnglish,
                onValueChange = {
                    nameEnglish = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    AppText("Item का English Name")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = searchAliases,
                onValueChange = {
                    searchAliases = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    AppText("Hinglish/Search Names")
                },
                supportingText = {
                    AppText("उदाहरण: atta, aata, flour")
                }
            )

            DynamicCategoryDropdown(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategoryTextChanged = {
                    selectedCategory = it
                },
                onCategorySelected = {
                    selectedCategory = it
                }
            )

            UnitDropdown(
                selectedUnit = defaultUnit,
                onUnitSelected = {
                    defaultUnit = it
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = {
                    coroutineScope.launch {

                        itemMasterDao.insertCustomItem(
                            ItemMaster(
                                nameHindi = nameHindi.trim(),
                                nameEnglish = nameEnglish.trim(),
                                searchAliases = searchAliases.trim(),
                                category = selectedCategory,
                                defaultUnit = defaultUnit.trim(),
                                isCustom = true
                            )
                        )

                        onBack()
                    }
                },

                enabled = formIsValid,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                shape = RoundedCornerShape(12.dp)
            ) {
                AppText(
                    text = "Item सेव करें",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
