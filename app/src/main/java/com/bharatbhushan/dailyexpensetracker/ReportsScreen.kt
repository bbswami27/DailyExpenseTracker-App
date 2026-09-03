package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    expenseDao: ExpenseDao,
    expenseLineItemDao: ExpenseLineItemDao,
    categoryMasterDao: CategoryMasterDao,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activeCategories by categoryMasterDao
        .getActiveCategories()
        .collectAsState(initial = emptyList())

    val allExpensesForOptions by expenseDao
        .getAllExpenses()
        .collectAsState(initial = emptyList())
    val allExpenseBills by expenseDao.getAllExpensesWithItems()
        .collectAsState(initial = emptyList())

    val categoryOptions = listOf("सभी श्रेणियाँ") +
            activeCategories.map { it.nameHindi }

    var selectedPeriod by remember { mutableStateOf("पिछला 1 महीना") }
    var selectedCategory by remember { mutableStateOf("सभी श्रेणियाँ") }
    var selectedPayment by remember { mutableStateOf("सभी माध्यम") }
    var customStart by remember { mutableStateOf(getMonthStart()) }
    var customEnd by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectingStartDate by remember { mutableStateOf(false) }
    var selectingEndDate by remember { mutableStateOf(false) }
    var itemSearch by remember { mutableStateOf("") }
    var partySearch by remember { mutableStateOf("") }

    val timeRange = remember(selectedPeriod, customStart, customEnd) {
        when (selectedPeriod) {
            "आज" -> getTodayStart() to getTomorrowStart()
            "पिछला 1 महीना" -> monthsAgoStart(1) to tomorrowStartForReport()
            "पिछले 2 महीने" -> monthsAgoStart(2) to tomorrowStartForReport()
            "पिछले 3 महीने" -> monthsAgoStart(3) to tomorrowStartForReport()
            "पिछले 6 महीने" -> monthsAgoStart(6) to tomorrowStartForReport()
            "पिछले 12 महीने" -> monthsAgoStart(12) to tomorrowStartForReport()
            "मनचाही तारीख" -> startOfReportDay(customStart) to
                    dayAfterReportDate(customEnd)
            else -> 0L to Long.MAX_VALUE
        }
    }

    val categoryFilter =
        if (selectedCategory == "सभी श्रेणियाँ") "" else selectedCategory
    val paymentFilter =
        if (selectedPayment == "सभी माध्यम") "" else selectedPayment
    val paymentOptions = remember(allExpensesForOptions) {
        listOf("सभी माध्यम") + allExpensesForOptions
            .map { it.paymentMode.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    val databaseFilteredExpenses by remember(
        timeRange.first,
        timeRange.second,
        categoryFilter,
        paymentFilter
    ) {
        expenseDao.getFilteredExpenses(
            startTime = timeRange.first,
            endTime = timeRange.second,
            category = categoryFilter,
            paymentMode = paymentFilter
        )
    }.collectAsState(initial = emptyList())

    val filteredExpenses = remember(databaseFilteredExpenses, partySearch) {
        val query = partySearch.trim()
        if (query.isBlank()) databaseFilteredExpenses else {
            databaseFilteredExpenses.filter { expense ->
                expense.shopName.contains(query, ignoreCase = true) ||
                        expense.description.contains(query, ignoreCase = true) ||
                        expense.category.contains(query, ignoreCase = true) ||
                        expense.paymentMode.contains(query, ignoreCase = true)
            }
        }
    }
    val filteredBills = remember(allExpenseBills, filteredExpenses) {
        val ids = filteredExpenses.map { it.id }.toSet()
        allExpenseBills.filter { it.expense.id in ids }
    }

    val sixMonthRange = remember { sixCalendarMonthRange() }
    val monthlyItemRates by remember(sixMonthRange) {
        expenseLineItemDao.getMonthlyItemRates(
            startTime = sixMonthRange.first,
            endTime = sixMonthRange.second
        )
    }.collectAsState(initial = emptyList())

    val monthlyCategoryExpenses by remember(sixMonthRange) {
        expenseDao.getMonthlyCategoryExpenses(
            startTime = sixMonthRange.first,
            endTime = sixMonthRange.second
        )
    }.collectAsState(initial = emptyList())

    val monthKeys = remember { lastSixMonthKeys() }
    val visibleItemGroups = remember(monthlyItemRates, itemSearch) {
        monthlyItemRates
            .groupBy {
                it.itemMasterId?.let { id -> "id:$id" }
                    ?: "name:${it.itemNameHindi.lowercase()}"
            }
            .values
            .filter { records ->
                val first = records.first()
                itemSearch.isBlank() ||
                        first.itemNameHindi.contains(itemSearch, true) ||
                        first.itemNameEnglish.contains(itemSearch, true)
            }
            .sortedBy { it.first().itemNameHindi }
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
                    AppText("खर्च रिपोर्ट / Reports", fontWeight = FontWeight.Bold)
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
                AppText(
                    "Date और Filter",
                    modifier = Modifier.padding(top = 10.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                ReportFilterDropdown(
                    label = "अवधि / Period",
                    selectedValue = selectedPeriod,
                    options = listOf(
                        "आज", "पिछला 1 महीना", "पिछले 2 महीने",
                        "पिछले 3 महीने", "पिछले 6 महीने",
                        "पिछले 12 महीने", "मनचाही तारीख", "सभी समय"
                    ),
                    onValueSelected = { selectedPeriod = it }
                )
            }

            item {
                OutlinedTextField(
                    value = partySearch,
                    onValueChange = { partySearch = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { AppText("Party / Shop / Vendor खोजें") },
                    singleLine = true
                )
            }

            if (selectedPeriod == "मनचाही तारीख") {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { selectingStartDate = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CalendarMonth, null)
                            AppText("  From: ${formatSelectedDate(customStart)}")
                        }
                        OutlinedButton(
                            onClick = { selectingEndDate = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CalendarMonth, null)
                            AppText("  To: ${formatSelectedDate(customEnd)}")
                        }
                    }
                }
            }

            item {
                ReportFilterDropdown(
                    label = "श्रेणी / Category",
                    selectedValue = selectedCategory,
                    options = categoryOptions,
                    onValueSelected = { selectedCategory = it }
                )
            }

            item {
                ReportFilterDropdown(
                    label = "भुगतान माध्यम / Payment Mode",
                    selectedValue = selectedPayment,
                    options = paymentOptions,
                    onValueSelected = { selectedPayment = it }
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AppText("कुल खर्च", fontSize = 14.sp)
                        AppText(
                            formatAmount(filteredExpenses.sumOf { it.amount }),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        AppText("कुल Entries: ${filteredExpenses.size}", fontSize = 13.sp)
                    }
                }
            }

            item {
                AppText("Complete Bills Export", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button({ exportBillsAsPdf(context, filteredBills, "expense_report", "Complete Expense Report") }, enabled = filteredBills.isNotEmpty(), modifier = Modifier.weight(1f)) { AppText("PDF") }
                    Button({ exportBillsAsExcel(context, filteredBills, "expense_report") }, enabled = filteredBills.isNotEmpty(), modifier = Modifier.weight(1f)) { AppText("Excel") }
                    Button({ exportBillsAsJpg(context, filteredBills, "expense_report", "Complete Expense Report") }, enabled = filteredBills.isNotEmpty(), modifier = Modifier.weight(1f)) { AppText("JPG") }
                }
                AppText("Export में हर bill की complete detail और items शामिल हैं।", fontSize = 12.sp)
            }

            item {
                AppText("खर्च विवरण", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            if (filteredExpenses.isEmpty()) {
                item { AppText("चुने गए Filter में कोई खर्च नहीं मिला।") }
            } else {
                items(filteredExpenses, key = { "report_${it.id}" }) {
                    ReportExpenseCard(it)
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                AppText(
                    "Category-wise Monthly Expense — पिछले 6 महीने",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (monthlyCategoryExpenses.isEmpty()) {
                item { AppText("Category-wise monthly records नहीं मिले।") }
            } else {
                items(
                    items = monthKeys,
                    key = { "category_month_$it" }
                ) { monthKey ->
                    val records = monthlyCategoryExpenses
                        .filter { it.monthKey == monthKey }
                    if (records.isNotEmpty()) {
                        MonthlyCategoryExpenseCard(
                            monthKey = monthKey,
                            records = records
                        )
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                AppText(
                    "Items Price Comparison Report — पिछले 6 महीने",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                AppText(
                    "हर महीने का item खर्च और average खरीद दर",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                val visibleRecords = visibleItemGroups.flatten()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button({ exportPriceComparisonAsPdf(context, visibleRecords) }, enabled = visibleRecords.isNotEmpty(), modifier = Modifier.weight(1f)) { AppText("PDF") }
                    Button({ exportPriceComparisonAsExcel(context, visibleRecords) }, enabled = visibleRecords.isNotEmpty(), modifier = Modifier.weight(1f)) { AppText("Excel") }
                    Button({ exportPriceComparisonAsJpg(context, visibleRecords) }, enabled = visibleRecords.isNotEmpty(), modifier = Modifier.weight(1f)) { AppText("JPG") }
                }
            }

            item {
                OutlinedTextField(
                    value = itemSearch,
                    onValueChange = { itemSearch = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { AppText("Item खोजें / Search Item") },
                    singleLine = true
                )
            }

            if (visibleItemGroups.isEmpty()) {
                item { AppText("पिछले 6 महीनों में item-wise records नहीं मिले।") }
            } else {
                items(
                    items = visibleItemGroups,
                    key = { records ->
                        records.first().itemMasterId?.let { "rate_$it" }
                            ?: "rate_${records.first().itemNameHindi}"
                    }
                ) { records ->
                    ItemSixMonthComparisonCard(records, monthKeys)
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (selectingStartDate) {
        ExpenseDatePickerDialog(
            initialDate = customStart,
            onDateSelected = {
                customStart = it
                if (customEnd < it) customEnd = it
                selectingStartDate = false
            },
            onDismiss = { selectingStartDate = false }
        )
    }

    if (selectingEndDate) {
        ExpenseDatePickerDialog(
            initialDate = customEnd,
            onDateSelected = {
                customEnd = it
                if (customStart > it) customStart = it
                selectingEndDate = false
            },
            onDismiss = { selectingEndDate = false }
        )
    }
}

@Composable
private fun MonthlyCategoryExpenseCard(
    monthKey: String,
    records: List<MonthlyCategoryExpense>
) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AppText(
                monthLabelForReport(monthKey),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            records.sortedByDescending { it.totalAmount }.forEach { record ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    AppText(
                        record.category,
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp
                    )
                    AppText(
                        "${formatAmount(record.totalAmount)} (${record.entryCount})",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth()) {
                AppText("महीने का कुल", modifier = Modifier.weight(1f))
                AppText(
                    formatAmount(records.sumOf { it.totalAmount }),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ItemSixMonthComparisonCard(
    records: List<MonthlyItemRate>,
    monthKeys: List<String>
) {
    val first = records.first()
    val byMonth = records.associateBy { it.monthKey }
    val available = monthKeys.mapNotNull { byMonth[it] }
    val oldest = available.firstOrNull()?.averageRate
    val latest = available.lastOrNull()?.averageRate
    val change = if (oldest != null && latest != null && oldest > 0.0) {
        ((latest - oldest) / oldest) * 100.0
    } else null

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            AppText(first.itemNameHindi, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            if (first.itemNameEnglish.isNotBlank()) {
                AppText(first.itemNameEnglish, fontSize = 12.sp)
            }
            monthKeys.forEach { key ->
                val record = byMonth[key]
                Row(modifier = Modifier.fillMaxWidth()) {
                    AppText(monthLabelForReport(key), modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                        AppText(
                            record?.let {
                                "खर्च ${formatAmount(it.totalAmount)}"
                            } ?: "—",
                            fontWeight = FontWeight.Medium
                        )
                        if (record != null) {
                            AppText(
                                "Avg ${formatAmount(record.averageRate)} • Low ${formatAmount(record.lowestRate)} • High ${formatAmount(record.highestRate)} • ${record.purchaseCount} बार",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            if (change != null) {
                AppText(
                    String.format(
                        Locale.getDefault(),
                        "6 महीने में बदलाव: %+.1f%%",
                        change
                    ),
                    color = if (change > 0) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFilterDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            label = { AppText(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { AppText(option) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ReportExpenseCard(expense: Expense) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AppText(expense.category, fontWeight = FontWeight.Bold)
                AppText(formatExpenseDate(expense.createdAt), fontSize = 12.sp)
                AppText(expense.paymentMode, fontSize = 12.sp)
                if (expense.shopName.isNotBlank()) {
                    AppText(expense.shopName, fontSize = 12.sp)
                }
                if (expense.description.isNotBlank()) {
                    AppText(expense.description, fontSize = 13.sp)
                }
            }
            AppText(formatAmount(expense.amount), fontWeight = FontWeight.Bold)
        }
    }
}

private fun monthsAgoStart(months: Int): Long = Calendar.getInstance().apply {
    add(Calendar.MONTH, -months)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun startOfReportDay(time: Long): Long = Calendar.getInstance().apply {
    timeInMillis = time
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

private fun dayAfterReportDate(time: Long): Long = Calendar.getInstance().apply {
    timeInMillis = startOfReportDay(time)
    add(Calendar.DAY_OF_MONTH, 1)
}.timeInMillis

private fun tomorrowStartForReport(): Long =
    dayAfterReportDate(System.currentTimeMillis())

private fun sixCalendarMonthRange(): Pair<Long, Long> {
    val start = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.MONTH, -5)
    }.timeInMillis
    return start to tomorrowStartForReport()
}

private fun lastSixMonthKeys(): List<String> {
    val formatter = SimpleDateFormat("yyyy-MM", Locale.US)
    val calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        add(Calendar.MONTH, -5)
    }
    return List(6) {
        formatter.format(calendar.time).also {
            calendar.add(Calendar.MONTH, 1)
        }
    }
}

private fun monthLabelForReport(monthKey: String): String {
    return try {
        val date = SimpleDateFormat("yyyy-MM", Locale.US).parse(monthKey)
            ?: return monthKey
        SimpleDateFormat("MMM yyyy", Locale("hi", "IN")).format(Date(date.time))
    } catch (_: Exception) {
        monthKey
    }
}
