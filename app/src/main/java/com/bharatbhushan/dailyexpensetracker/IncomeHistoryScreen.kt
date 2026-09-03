package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeHistoryScreen(
    incomeDao: IncomeDao,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val incomeEntries by incomeDao
        .getAllIncome()
        .collectAsState(initial = emptyList())

    var editingIncome by remember {
        mutableStateOf<Income?>(null)
    }

    var incomeToDelete by remember {
        mutableStateOf<Income?>(null)
    }

    val totalIncome = incomeEntries.sumOf {
        it.amount
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
                        text = "Cash In History",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),

            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            item {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        AppText(
                            text = "कुल आय / Total Income",
                            fontSize = 13.sp
                        )

                        AppText(
                            text = formatAmount(totalIncome),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color =
                                MaterialTheme.colorScheme.primary
                        )

                        AppText(
                            text =
                                "कुल Entries: ${incomeEntries.size}",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (incomeEntries.isEmpty()) {

                item {

                    AppText(
                        text = "अभी कोई Income दर्ज नहीं है।",
                        modifier = Modifier.padding(
                            vertical = 24.dp
                        )
                    )
                }

            } else {

                items(
                    items = incomeEntries,
                    key = { income ->
                        income.id
                    }
                ) { income ->

                    IncomeHistoryCard(
                        income = income,

                        onEdit = {
                            editingIncome = income
                        },

                        onDelete = {
                            incomeToDelete = income
                        }
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.padding(40.dp)
                )
            }
        }
    }

    editingIncome?.let { income ->

        EditIncomeDialog(
            income = income,

            onSave = { updatedIncome ->

                coroutineScope.launch {
                    incomeDao.updateIncome(updatedIncome)
                    editingIncome = null
                }
            },

            onDismiss = {
                editingIncome = null
            }
        )
    }

    incomeToDelete?.let { income ->

        AlertDialog(
            onDismissRequest = {
                incomeToDelete = null
            },

            title = {
                AppText("Income हटाएँ?")
            },

            text = {
                AppText(
                    text =
                        "${formatAmount(income.amount)} की " +
                                "यह Income स्थायी रूप से हट जाएगी।"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        coroutineScope.launch {
                            incomeDao.deleteIncome(income)
                            incomeToDelete = null
                        }
                    }
                ) {
                    AppText("हटाएँ")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        incomeToDelete = null
                    }
                ) {
                    AppText("रद्द करें")
                }
            }
        )
    }
}

@Composable
private fun IncomeHistoryCard(
    income: Income,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                AppText(
                    text = income.source,
                    fontWeight = FontWeight.Bold
                )

                AppText(
                    text = formatExpenseDate(
                        income.receivedAt
                    ),
                    fontSize = 12.sp
                )

                AppText(
                    text = income.paymentMode,
                    fontSize = 12.sp
                )

                if (income.description.isNotBlank()) {

                    AppText(
                        text = income.description,
                        fontSize = 13.sp
                    )
                }

                AppText(
                    text = formatAmount(income.amount),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            IconButton(
                onClick = onEdit
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Income"
                )
            }

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Income"
                )
            }
        }
    }
}

@Composable
private fun EditIncomeDialog(
    income: Income,
    onSave: (Income) -> Unit,
    onDismiss: () -> Unit
) {
    var amount by remember(income.id) {
        mutableStateOf(
            income.amount.toString()
        )
    }

    var source by remember(income.id) {
        mutableStateOf(income.source)
    }

    var paymentMode by remember(income.id) {
        mutableStateOf(income.paymentMode)
    }

    var description by remember(income.id) {
        mutableStateOf(income.description)
    }

    var receivedAt by remember(income.id) {
        mutableStateOf(income.receivedAt)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val amountValue =
        amount.toDoubleOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            AppText("Income Edit करें")
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        AppText("Amount")
                    },
                    prefix = {
                        AppText("${currentCurrencySymbol()} ")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = source,
                    onValueChange = {
                        source = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        AppText("Income Source")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = paymentMode,
                    onValueChange = {
                        paymentMode = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        AppText("Payment Mode")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        AppText("Description")
                    },
                    minLines = 2
                )

                OutlinedButton(
                    onClick = {
                        showDatePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppText(
                        text =
                            "Date: ${formatSelectedDate(receivedAt)}"
                    )
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {

                    if (
                        amountValue != null &&
                        amountValue > 0 &&
                        source.isNotBlank() &&
                        paymentMode.isNotBlank()
                    ) {

                        onSave(
                            income.copy(
                                amount = amountValue,
                                source = source.trim(),
                                paymentMode =
                                    paymentMode.trim(),
                                description =
                                    description.trim(),
                                receivedAt = receivedAt
                            )
                        )
                    }
                },

                enabled =
                    amountValue != null &&
                            amountValue > 0 &&
                            source.isNotBlank() &&
                            paymentMode.isNotBlank()
            ) {
                AppText("Save")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                AppText("रद्द करें")
            }
        }
    )

    if (showDatePicker) {

        ExpenseDatePickerDialog(
            initialDate = receivedAt,

            onDateSelected = { date ->
                receivedAt = date
                showDatePicker = false
            },

            onDismiss = {
                showDatePicker = false
            }
        )
    }
}
