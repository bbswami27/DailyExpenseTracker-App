package com.bharatbhushan.dailyexpensetracker

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    bill: ExpenseWithItems,
    expenseLineItemDao: ExpenseLineItemDao,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val expense = bill.expense
    var comparisonItem by remember {
        mutableStateOf<ExpenseLineItem?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                title = {
                    AppText("Bill Details / खर्च विवरण", fontWeight = FontWeight.Bold)
                }
            )
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
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        AppText(
                            formatAmount(expense.amount),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        AppText(formatExpenseDate(expense.createdAt))
                        AppText("Category: ${expense.category}")
                        AppText("Payment: ${expense.paymentMode}")
                        if (expense.billNumber.isNotBlank()) {
                            AppText("Bill/Invoice No: ${expense.billNumber}")
                        }
                        if (expense.shopName.isNotBlank()) {
                            AppText("Shop/Vendor: ${expense.shopName}")
                        }
                        if (expense.description.isNotBlank()) {
                            AppText("Note: ${expense.description}")
                        }
                        if (expense.billAttachmentUri.isNotBlank()) {
                            Button(
                                onClick = {
                                    try {
                                        val savedUri = Uri.parse(expense.billAttachmentUri)
                                        val uri = if (savedUri.scheme == "file") {
                                            FileProvider.getUriForFile(
                                                context,
                                                "${context.packageName}.fileprovider",
                                                File(requireNotNull(savedUri.path))
                                            )
                                        } else savedUri
                                        val type = context.contentResolver.getType(uri) ?: "*/*"
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(uri, type)
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                        )
                                    } catch (_: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Bill file नहीं खुली। File उपलब्ध नहीं है।",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.AttachFile, null)
                                AppText("  Bill Photo/PDF खोलें")
                            }
                        }
                    }
                }
            }

            item {
                AppText(
                    "खरीदे गए Items (${bill.items.size})",
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                AppText(
                    "Bill Export",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            exportBillsAsPdf(context, listOf(bill), "bill_${expense.id}", "Expense Bill")
                        },
                        modifier = Modifier.weight(1f)
                    ) { AppText("PDF") }
                    Button(
                        onClick = {
                            exportBillsAsExcel(context, listOf(bill), "bill_${expense.id}")
                        },
                        modifier = Modifier.weight(1f)
                    ) { AppText("Excel") }
                    Button(
                        onClick = {
                            exportBillsAsJpg(context, listOf(bill), "bill_${expense.id}", "Expense Bill")
                        },
                        modifier = Modifier.weight(1f)
                    ) { AppText("JPG") }
                }
            }

            if (bill.items.isEmpty()) {
                item {
                    AppText("इस पुराने खर्च के item details उपलब्ध नहीं हैं।")
                }
            } else {
                items(bill.items, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                comparisonItem = item
                            },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                AppText(
                                    item.itemNameHindi,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (item.itemNameEnglish.isNotBlank()) {
                                    AppText(item.itemNameEnglish, fontSize = 12.sp)
                                }
                                AppText(
                                    "${item.quantity} ${item.unit} × ${formatAmount(item.rate)}",
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                AppText(formatAmount(item.amount), fontWeight = FontWeight.Bold)
                                Icon(
                                    Icons.Default.CompareArrows,
                                    "Compare price",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    comparisonItem?.let { selectedItem ->
        val history by expenseLineItemDao.getItemPriceHistory(
            itemMasterId = selectedItem.itemMasterId,
            itemNameHindi = selectedItem.itemNameHindi,
            itemNameEnglish = selectedItem.itemNameEnglish
        ).collectAsState(initial = emptyList())

        ItemPriceComparisonDialog(
            item = selectedItem,
            history = history,
            onDismiss = { comparisonItem = null }
        )
    }
}

@Composable
private fun ItemPriceComparisonDialog(
    item: ExpenseLineItem,
    history: List<ItemPriceHistory>,
    onDismiss: () -> Unit
) {
    val rates = history.map { it.rate }
    val latestRate = history.firstOrNull()?.rate ?: item.rate
    val oldestRate = history.lastOrNull()?.rate ?: item.rate
    val changePercent = if (oldestRate > 0.0) {
        ((latestRate - oldestRate) / oldestRate) * 100.0
    } else 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { AppText("${item.itemNameHindi} Rate Comparison") },
        text = {
            Column {
                if (history.isEmpty()) {
                    AppText("अभी comparison के लिए पुराना record नहीं है।")
                } else {
                    AppText("Latest: ${formatAmount(latestRate)} • Lowest: ${formatAmount(rates.minOrNull() ?: 0.0)}")
                    AppText("Highest: ${formatAmount(rates.maxOrNull() ?: 0.0)} • Average: ${formatAmount(rates.average())}")
                    AppText(
                        String.format(Locale.getDefault(), "Overall change: %+.1f%%", changePercent),
                        color = if (changePercent > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(history) { record ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    AppText(formatAmount(record.rate), fontWeight = FontWeight.Bold)
                                    AppText(
                                        "${record.quantity} ${record.unit} • ${formatExpenseDate(record.purchasedAt)}",
                                        fontSize = 12.sp
                                    )
                                    if (record.shopName.isNotBlank()) {
                                        AppText("Shop: ${record.shopName}", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { AppText("बंद करें") }
        }
    )
}
