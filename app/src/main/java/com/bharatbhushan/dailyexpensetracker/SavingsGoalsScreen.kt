package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGoalsScreen(
    savingsGoalDao: SavingsGoalDao,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val goals by savingsGoalDao
        .getAllGoals()
        .collectAsState(initial = emptyList())

    var showAddGoalDialog by remember {
        mutableStateOf(false)
    }

    var addMoneyGoal by remember {
        mutableStateOf<SavingsGoal?>(null)
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
                        text = "Savings Goals",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddGoalDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Goal"
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            item {

                AppText(
                    text = "अपने सपनों के लिए बचत करें",
                    modifier = Modifier.padding(top = 12.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                AppText(
                    text = "Set a target and track your progress",
                    fontSize = 13.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }

            if (goals.isEmpty()) {

                item {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {

                        AppText(
                            text =
                                "अभी कोई Savings Goal नहीं है।\n" +
                                        "+ दबाकर पहला Goal बनाएँ।",
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

            } else {

                items(
                    items = goals,
                    key = { goal ->
                        goal.id
                    }
                ) { goal ->

                    SavingsGoalCard(
                        goal = goal,

                        onAddMoney = {
                            addMoneyGoal = goal
                        },

                        onDelete = {
                            coroutineScope.launch {
                                savingsGoalDao.deleteGoal(goal)
                            }
                        }
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(80.dp)
                )
            }
        }
    }

    if (showAddGoalDialog) {

        AddSavingsGoalDialog(

            onSave = {
                    name,
                    targetAmount,
                    savedAmount,
                    targetDate ->

                coroutineScope.launch {

                    savingsGoalDao.insertGoal(
                        SavingsGoal(
                            name = name,
                            targetAmount = targetAmount,
                            savedAmount = savedAmount,
                            targetDate = targetDate,
                            isCompleted =
                                savedAmount >= targetAmount
                        )
                    )

                    showAddGoalDialog = false
                }
            },

            onDismiss = {
                showAddGoalDialog = false
            }
        )
    }

    addMoneyGoal?.let { goal ->

        AddGoalMoneyDialog(
            goal = goal,

            onAdd = { amount ->

                coroutineScope.launch {

                    savingsGoalDao.addMoney(
                        goalId = goal.id,
                        amount = amount
                    )

                    addMoneyGoal = null
                }
            },

            onDismiss = {
                addMoneyGoal = null
            }
        )
    }
}

@Composable
private fun SavingsGoalCard(
    goal: SavingsGoal,
    onAddMoney: () -> Unit,
    onDelete: () -> Unit
) {
    val progress =
        if (goal.targetAmount > 0) {
            (
                    goal.savedAmount /
                            goal.targetAmount
                    ).toFloat().coerceIn(0f, 1f)
        } else {
            0f
        }

    val remaining =
        (goal.targetAmount - goal.savedAmount)
            .coerceAtLeast(0.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    AppText(
                        text = goal.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    AppText(
                        text =
                            if (goal.isCompleted) {
                                "✅ Goal Completed"
                            } else {
                                "लक्ष्य: ${formatAmount(goal.targetAmount)}"
                            },
                        fontSize = 13.sp,
                        color =
                            MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Goal"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color =
                    MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                AppText(
                    text =
                        "Saved: ${formatAmount(goal.savedAmount)}",
                    fontWeight = FontWeight.Medium
                )

                AppText(
                    text = "${(progress * 100).toInt()}%",
                    color =
                        MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            AppText(
                text =
                    "Remaining: ${formatAmount(remaining)}",
                fontSize = 13.sp
            )

            goal.targetDate?.let { date ->

                AppText(
                    text =
                        "Target Date: ${formatSelectedDate(date)}",
                    fontSize = 12.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = onAddMoney,
                enabled = !goal.isCompleted,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppText("Money जोड़ें / Add Money")
            }
        }
    }
}

@Composable
private fun AddSavingsGoalDialog(
    onSave: (
        name: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDate: Long?
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }

    var targetAmount by remember {
        mutableStateOf("")
    }

    var savedAmount by remember {
        mutableStateOf("")
    }

    var targetDate by remember {
        mutableStateOf<Long?>(null)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val targetValue =
        targetAmount.toDoubleOrNull()

    val savedValue =
        savedAmount.toDoubleOrNull() ?: 0.0

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            AppText("नया Savings Goal")
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        AppText("Goal Name")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = {
                        targetAmount = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        AppText("Target Amount")
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
                    value = savedAmount,
                    onValueChange = {
                        savedAmount = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        AppText("Already Saved")
                    },
                    prefix = {
                        AppText("${currentCurrencySymbol()} ")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true
                )

                OutlinedButton(
                    onClick = {
                        showDatePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppText(
                        text =
                            targetDate?.let {
                                "Target: ${formatSelectedDate(it)}"
                            } ?: "Target Date चुनें"
                    )
                }
            }
        },

        confirmButton = {

            TextButton(
                onClick = {
                    if (
                        name.isNotBlank() &&
                        targetValue != null &&
                        targetValue > 0 &&
                        savedValue >= 0
                    ) {
                        onSave(
                            name.trim(),
                            targetValue,
                            savedValue,
                            targetDate
                        )
                    }
                },

                enabled =
                    name.isNotBlank() &&
                            targetValue != null &&
                            targetValue > 0 &&
                            savedValue >= 0
            ) {
                AppText("Goal सेव करें")
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
            initialDate =
                targetDate ?: System.currentTimeMillis(),

            onDateSelected = { date ->
                targetDate = date
                showDatePicker = false
            },

            onDismiss = {
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun AddGoalMoneyDialog(
    goal: SavingsGoal,
    onAdd: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var amount by remember {
        mutableStateOf("")
    }

    val enteredAmount =
        amount.toDoubleOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            AppText("Money जोड़ें")
        },

        text = {

            Column {

                AppText(
                    text = goal.name,
                    fontWeight = FontWeight.Bold
                )

                AppText(
                    text =
                        "Remaining: " +
                                formatAmount(
                                    (
                                            goal.targetAmount -
                                                    goal.savedAmount
                                            ).coerceAtLeast(0.0)
                                ),
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
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
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    if (
                        enteredAmount != null &&
                        enteredAmount > 0
                    ) {
                        onAdd(enteredAmount)
                    }
                },
                enabled =
                    enteredAmount != null &&
                            enteredAmount > 0
            ) {
                AppText("Add")
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
}
