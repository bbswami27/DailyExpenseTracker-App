package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseHistoryScreen(
    expenses: List<ExpenseWithItems>,
    onBack: () -> Unit,
    onOpenDetail: (ExpenseWithItems) -> Unit,
    onEdit: (ExpenseWithItems) -> Unit,
    onDelete: (Expense) -> Unit
) {
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }
    val dayKeyFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val dayTitleFormatter = remember {
        SimpleDateFormat("EEEE, dd MMM yyyy", Locale("hi", "IN"))
    }
    val groupedExpenses = remember(expenses) {
        expenses.groupBy {
            dayKeyFormatter.format(Date(it.expense.createdAt))
        }
    }

    Scaffold(
        topBar = {
            TopAppAppBar(
                onBack = onBack
            )
        }
    ) { padding ->
        if (expenses.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppText("अभी कोई खर्च दर्ज नहीं है", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                AppText("No expense recorded yet", fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    AppText(
                        "कुल ${expenses.size} Bills • ${expenses.sumOf { it.items.size }} Items",
                        modifier = Modifier.padding(vertical = 10.dp),
                        fontWeight = FontWeight.Medium
                    )
                }

                groupedExpenses.forEach { (_, billsForDay) ->
                    val date = Date(billsForDay.first().expense.createdAt)
                    val dayTotal = billsForDay.sumOf { it.expense.amount }

                    item(key = "day_${dayKeyFormatter.format(date)}") {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText(
                                dayTitleFormatter.format(date),
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold
                            )
                            AppText(
                                formatAmount(dayTotal),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    items(
                        items = billsForDay,
                        key = { "bill_${it.expense.id}" }
                    ) { bill ->
                        ExpenseHistoryCard(
                            bill = bill,
                            onOpen = { onOpenDetail(bill) },
                            onEdit = { onEdit(bill) },
                            onDelete = { expenseToDelete = bill.expense }
                        )
                    }
                }
            }
        }
    }

    expenseToDelete?.let { expense ->
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { AppText("खर्च हटाएँ?") },
            text = {
                AppText("${formatAmount(expense.amount)} का bill और उसके सभी items स्थायी रूप से हट जाएँगे।")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(expense)
                        expenseToDelete = null
                    }
                ) { AppText("हटाएँ") }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) { AppText("रद्द करें") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppAppBar(
    onBack: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
        },
        title = {
            AppText("Cash Out History", fontWeight = FontWeight.Bold)
        }
    )
}

@Composable
private fun ExpenseHistoryCard(
    bill: ExpenseWithItems,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val expense = bill.expense

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ReceiptLong, "Open bill")
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    AppText(expense.category, fontWeight = FontWeight.Bold)
                    AppText(formatExpenseDate(expense.createdAt), fontSize = 12.sp)
                    if (expense.shopName.isNotBlank()) {
                        AppText(expense.shopName, fontSize = 12.sp)
                    }
                    if (expense.billNumber.isNotBlank()) {
                        AppText("Bill No: ${expense.billNumber}", fontSize = 12.sp)
                    }
                }
                AppText(
                    formatAmount(expense.amount),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            AppText(
                if (bill.items.isEmpty()) {
                    "पुराना खर्च • Item details नहीं हैं"
                } else {
                    "${bill.items.size} Items: " +
                            bill.items.take(3).joinToString { it.itemNameHindi }
                },
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onOpen) {
                    AppText("Bill खोलें")
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit Expense")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete Expense")
                }
            }
        }
    }
}

fun formatExpenseDate(time: Long): String {
    return SimpleDateFormat(
        "dd MMM yyyy, hh:mm a",
        Locale("hi", "IN")
    ).format(Date(time))
}
