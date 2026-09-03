package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    monthKey: String,
    existingBudgets: List<Budget>,
    categoryMasterDao: CategoryMasterDao,
    onBack: () -> Unit,
    onSave: (List<Budget>) -> Unit
) {
    val activeCategories by categoryMasterDao
        .getActiveCategories()
        .collectAsState(initial = emptyList())

    val categories = activeCategories.map {
        it.nameHindi
    }

    val budgetValues = remember(
        existingBudgets,
        categories
    ) {
        mutableStateMapOf<String, String>().apply {

            categories.forEach { category ->

                val savedAmount = existingBudgets
                    .find {
                        it.category == category
                    }
                    ?.amount

                this[category] =
                    if (
                        savedAmount == null ||
                        savedAmount == 0.0
                    ) {
                        ""
                    } else {
                        savedAmount.toString()
                    }
            }
        }
    }

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
                        text = "मासिक बजट",
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AppText(
                text = "Budget Allocation",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            AppText(
                text = "माह: $monthKey",
                fontSize = 14.sp
            )

            if (categories.isEmpty()) {

                AppText(
                    text = "कोई active category उपलब्ध नहीं है।"
                )
            }

            categories.forEach { category ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {

                        AppText(
                            text = category,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = budgetValues[category] ?: "",

                            onValueChange = { value ->

                                if (
                                    value.isEmpty() ||
                                    value.toDoubleOrNull() != null
                                ) {
                                    budgetValues[category] = value
                                }
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),

                            label = {
                                AppText(
                                    "मासिक बजट / Monthly Budget"
                                )
                            },

                            prefix = {
                                AppText("${currentCurrencySymbol()} ")
                            },

                            singleLine = true,

                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )
                    }
                }
            }

            Button(
                onClick = {

                    val updatedBudgets =
                        categories.mapNotNull { category ->

                            val amount =
                                budgetValues[category]
                                    ?.toDoubleOrNull()

                            if (
                                amount != null &&
                                amount >= 0
                            ) {
                                Budget(
                                    monthKey = monthKey,
                                    category = category,
                                    amount = amount
                                )
                            } else {
                                null
                            }
                        }

                    onSave(updatedBudgets)
                },

                enabled = categories.isNotEmpty(),

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(14.dp)
            ) {

                AppText(
                    text = "बजट सेव करें",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
