package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class EditableExpenseItem(
    val itemMasterId: Int? = null,
    val hindiName: String = "",
    val englishName: String = "",
    val quantity: String = "1",
    val unit: String = "Piece",
    val rate: String = "0"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(
    bill: ExpenseWithItems,
    onBack: () -> Unit,
    onSave: (Expense, List<ExpenseLineItem>) -> Unit
) {
    val expense = bill.expense
    var note by remember(expense.id) { mutableStateOf(expense.description) }
    var shopName by remember(expense.id) { mutableStateOf(expense.shopName) }
    var billNumber by remember(expense.id) { mutableStateOf(expense.billNumber) }
    var selectedCategory by remember(expense.id) { mutableStateOf(expense.category) }
    var selectedPayment by remember(expense.id) { mutableStateOf(expense.paymentMode) }
    var expenseDate by remember(expense.id) { mutableStateOf(expense.createdAt) }
    var showDatePicker by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf("") }

    val editableItems = remember(expense.id) {
        mutableStateListOf<EditableExpenseItem>().apply {
            addAll(bill.items.map { item ->
                EditableExpenseItem(
                    itemMasterId = item.itemMasterId,
                    hindiName = item.itemNameHindi,
                    englishName = item.itemNameEnglish,
                    quantity = item.quantity.toString(),
                    unit = item.unit,
                    rate = item.rate.toString()
                )
            })
        }
    }
    val billTotal = editableItems.sumOf { item ->
        (item.quantity.toDoubleOrNull() ?: 0.0) * (item.rate.toDoubleOrNull() ?: 0.0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                title = { AppText("Expense और Items Edit करें", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppText(
                "Items (${editableItems.size}) • Total ${formatAmount(billTotal)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            editableItems.forEachIndexed { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            AppText(
                                item.hindiName.ifBlank { "New Item" },
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { editableItems.removeAt(index) }) {
                                Icon(Icons.Default.Delete, "Delete item")
                            }
                        }
                        OutlinedTextField(
                            value = item.hindiName,
                            onValueChange = { editableItems[index] = item.copy(hindiName = it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { AppText("Item Name") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = item.englishName,
                            onValueChange = { editableItems[index] = item.copy(englishName = it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { AppText("English Name") },
                            singleLine = true
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = item.quantity,
                                onValueChange = { editableItems[index] = item.copy(quantity = it) },
                                modifier = Modifier.weight(1f),
                                label = { AppText("Qty") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = item.unit,
                                onValueChange = { editableItems[index] = item.copy(unit = it) },
                                modifier = Modifier.weight(1f),
                                label = { AppText("Unit") },
                                singleLine = true
                            )
                        }
                        OutlinedTextField(
                            value = item.rate,
                            onValueChange = { editableItems[index] = item.copy(rate = it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { AppText("Rate") },
                            prefix = { AppText("${currentCurrencySymbol()} ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                        val amount = (item.quantity.toDoubleOrNull() ?: 0.0) *
                                (item.rate.toDoubleOrNull() ?: 0.0)
                        AppText("Amount: ${formatAmount(amount)}", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Button(
                onClick = { editableItems += EditableExpenseItem() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Icon(Icons.Default.Add, null)
                AppText(" Add New Item")
            }
            OutlinedTextField(
                value = shopName,
                onValueChange = { shopName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { AppText("Shop / Vendor") },
                singleLine = true
            )
            OutlinedTextField(
                value = billNumber,
                onValueChange = { billNumber = it },
                modifier = Modifier.fillMaxWidth(),
                label = { AppText("Bill / Invoice Number") },
                singleLine = true
            )
            CategoryDropdown(selectedCategory) { selectedCategory = it }
            PaymentDropdown(selectedPayment) { selectedPayment = it }
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) { AppText("Date: ${formatSelectedDate(expenseDate)}") }
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                label = { AppText("Description") },
                minLines = 2
            )
            if (validationMessage.isNotBlank()) {
                AppText(validationMessage, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = {
                    val validItems = editableItems.mapNotNull { item ->
                        val quantity = item.quantity.toDoubleOrNull()
                        val rate = item.rate.toDoubleOrNull()
                        if (item.hindiName.isBlank() || quantity == null || quantity <= 0 || rate == null || rate < 0) null
                        else ExpenseLineItem(
                            expenseId = expense.id,
                            itemMasterId = item.itemMasterId,
                            itemNameHindi = item.hindiName.trim(),
                            itemNameEnglish = item.englishName.trim().ifBlank { item.hindiName.trim() },
                            quantity = quantity,
                            unit = item.unit.trim().ifBlank { "Piece" },
                            rate = rate,
                            amount = quantity * rate
                        )
                    }
                    if (validItems.isEmpty() || validItems.size != editableItems.size) {
                        validationMessage = "सभी Items में सही Name, Qty और Rate भरें।"
                    } else {
                        onSave(
                            expense.copy(
                                amount = validItems.sumOf { it.amount },
                                category = selectedCategory,
                                paymentMode = selectedPayment,
                                description = note.trim(),
                                shopName = shopName.trim(),
                                billNumber = billNumber.trim(),
                                createdAt = expenseDate
                            ),
                            validItems
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) { AppText("बदलाव सेव करें", fontWeight = FontWeight.Bold) }
        }
    }
    if (showDatePicker) {
        ExpenseDatePickerDialog(
            initialDate = expenseDate,
            onDateSelected = { expenseDate = it; showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }
}
