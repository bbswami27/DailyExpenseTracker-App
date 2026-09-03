package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeScreen(
    onBack: () -> Unit,
    onSave: (
        amount: Double,
        source: String,
        paymentMode: String,
        description: String,
        receivedAt: Long
    ) -> Unit
) {

    val context = LocalContext.current

    var customIncomeSources by remember {
        mutableStateOf(
            loadCustomIncomeSources(context)
        )
    }

    var showNewSourceDialog by remember {
        mutableStateOf(false)
    }

    var newSourceName by remember {
        mutableStateOf("")
    }

    var amount by remember {
        mutableStateOf("")
    }

    var selectedSource by remember {
        mutableStateOf("वेतन / Salary")
    }

    var selectedPayment by remember {
        mutableStateOf("Bank Transfer")
    }

    var description by remember {
        mutableStateOf("")
    }

    var selectedDate by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var amountError by remember {
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
                        text = "Cash In",
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
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            AppText(
                text = "New Cash In",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = amount,

                onValueChange = {
                    amount = it
                    amountError = false
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    AppText("राशि / Amount")
                },

                prefix = {
                    AppText("${currentCurrencySymbol()} ")
                },

                singleLine = true,
                isError = amountError,

                supportingText = {
                    if (amountError) {
                        AppText("सही राशि दर्ज करें")
                    }
                },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            IncomeSourceDropdown(
                selectedSource = selectedSource,
                customSources = customIncomeSources,

                onSourceSelected = {
                    selectedSource = it
                }
            )
            OutlinedButton(
                onClick = {
                    newSourceName = ""
                    showNewSourceDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                AppText("+ Add New Income Source")
            }

            OutlinedButton(
                onClick = {
                    showDatePicker = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Select income date"
                )

                AppText(
                    text =
                        "  तारीख / Date: " +
                                formatSelectedDate(selectedDate)
                )
            }

            PaymentDropdown(
                selectedPayment = selectedPayment,

                onPaymentSelected = {
                    selectedPayment = it
                }
            )

            OutlinedTextField(
                value = description,

                onValueChange = {
                    description = it
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    AppText("नोट / Description")
                },

                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {

                    val enteredAmount = amount.toDoubleOrNull()

                    if (enteredAmount != null && enteredAmount > 0) {

                        onSave(
                            enteredAmount,
                            selectedSource,
                            selectedPayment,
                            description.trim(),
                            selectedDate
                        )

                    } else {
                        amountError = true
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                shape = RoundedCornerShape(14.dp)
            ) {

                AppText(
                    text = "आय सेव करें",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showDatePicker) {
        ExpenseDatePickerDialog(
            initialDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }
    if (showNewSourceDialog) {

        AlertDialog(
            onDismissRequest = {
                showNewSourceDialog = false
            },

            title = {
                AppText("नया Income Source जोड़ें")
            },

            text = {

                OutlinedTextField(
                    value = newSourceName,

                    onValueChange = {
                        newSourceName = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        AppText("Income Source Name")
                    },

                    singleLine = true
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val source =
                            newSourceName.trim()

                        if (source.isNotBlank()) {

                            val updatedSources =
                                (
                                        customIncomeSources +
                                                source
                                        )
                                    .distinctBy {
                                        it.lowercase()
                                    }
                                    .sorted()

                            customIncomeSources =
                                updatedSources

                            saveCustomIncomeSources(
                                context,
                                updatedSources
                            )

                            selectedSource = source
                            newSourceName = ""
                            showNewSourceDialog = false
                        }
                    },

                    enabled =
                        newSourceName.isNotBlank()
                ) {

                    AppText("Save")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showNewSourceDialog = false
                    }
                ) {

                    AppText("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeSourceDropdown(
    selectedSource: String,
    customSources: List<String>,
    onSourceSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val sources = (
            listOf(
                "वेतन / Salary",
                "व्यवसाय / Business",
                "कृषि आय / Agriculture",
                "किराया / Rent",
                "ब्याज / Interest",
                "पेंशन / Pension",
                "फ्रीलांस / Freelance",
                "निवेश लाभ / Investment Return",
                "रिफंड या कैशबैक / Refund",
                "उपहार / Gift",
                "अन्य आय / Other Income"
            ) + customSources
            ).distinct()

    ExposedDropdownMenuBox(
        expanded = expanded,

        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        OutlinedTextField(
            value = selectedSource,
            onValueChange = {},
            readOnly = true,

            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),

            label = {
                AppText("आय का स्रोत / Income Source")
            },

            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )

        DropdownMenu(
            expanded = expanded,

            onDismissRequest = {
                expanded = false
            }
        ) {

            sources.forEach { source ->

                DropdownMenuItem(
                    text = {
                        AppText(source)
                    },

                    onClick = {
                        onSourceSelected(source)
                        expanded = false
                    }
                )
            }
        }
    }
}
private const val INCOME_SOURCE_PREFS =
    "income_source_preferences"

private const val CUSTOM_SOURCE_KEY =
    "custom_income_sources"

private fun loadCustomIncomeSources(
    context: Context
): List<String> {

    return context
        .getSharedPreferences(
            INCOME_SOURCE_PREFS,
            Context.MODE_PRIVATE
        )
        .getStringSet(
            CUSTOM_SOURCE_KEY,
            emptySet()
        )
        .orEmpty()
        .sorted()
}

private fun saveCustomIncomeSources(
    context: Context,
    sources: List<String>
) {

    context
        .getSharedPreferences(
            INCOME_SOURCE_PREFS,
            Context.MODE_PRIVATE
        )
        .edit()
        .putStringSet(
            CUSTOM_SOURCE_KEY,
            sources.toSet()
        )
        .apply()
}
