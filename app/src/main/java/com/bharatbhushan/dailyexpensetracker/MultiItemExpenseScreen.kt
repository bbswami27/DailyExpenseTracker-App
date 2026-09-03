package com.bharatbhushan.dailyexpensetracker

import kotlinx.coroutines.Dispatchers
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import androidx.core.content.FileProvider
import androidx.compose.material.icons.filled.CameraAlt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiItemExpenseScreen(
    itemMasterDao: ItemMasterDao,
    categoryMasterDao: CategoryMasterDao,
    onBack: () -> Unit,
    onSave: (
        category: String,
        paymentMode: String,
        description: String,
        shopName: String,
        billAttachmentUri: String,
        billNumber: String,
        expenseDate: Long,
        items: List<DraftExpenseItem>
    ) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val compactCategoryLayout =
        LocalConfiguration.current.screenWidthDp < 400 ||
                LocalDensity.current.fontScale > 1.15f

    val activeCategories by categoryMasterDao
        .getActiveCategories()
        .collectAsState(initial = emptyList())

    var showAddNewItem by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf("घरेलू राशन व दैनिक सामान")
    }

    var selectedPayment by remember {
        mutableStateOf("Cash")
    }
    var selectedDate by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var description by remember {
        mutableStateOf("")
    }

    var shopName by remember {
        mutableStateOf("")
    }

    var billAttachmentUri by remember {
        mutableStateOf("")
    }

    var billNumber by remember {
        mutableStateOf("")
    }

    var scannedBill by remember {
        mutableStateOf<ImportedBillData?>(null)
    }

    var isScanningBill by remember {
        mutableStateOf(false)
    }
    val billPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {

            coroutineScope.launch {

                try {

                    val savedUri = withContext(Dispatchers.IO) {

                        val mimeType =
                            context.contentResolver
                                .getType(uri)
                                .orEmpty()

                        require(
                            mimeType.startsWith("image/") ||
                                    mimeType == "application/pdf"
                        ) {
                            "केवल Photo या PDF चुनें"
                        }

                        val extension = when {

                            mimeType == "application/pdf" -> "pdf"

                            mimeType.contains("png") -> "png"

                            mimeType.contains("webp") -> "webp"

                            else -> "jpg"
                        }

                        val billsDirectory = File(
                            context.filesDir,
                            "bills"
                        ).apply {
                            mkdirs()
                        }

                        val billFile = File(
                            billsDirectory,
                            "bill_${System.currentTimeMillis()}.$extension"
                        )

                        context.contentResolver
                            .openInputStream(uri)
                            ?.use { input ->

                                billFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            ?: error("Selected bill file नहीं खुली")

                        Uri.fromFile(billFile).toString()
                    }

                    billAttachmentUri = savedUri

                    Toast.makeText(
                        context,
                        "Bill attach हो गया",
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (error: Exception) {

                    billAttachmentUri = ""

                    Toast.makeText(
                        context,
                        "Bill attach नहीं हुआ: " +
                                (error.message ?: "File error"),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    var pendingCameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var pendingCameraFile by remember {
        mutableStateOf<File?>(null)
    }

    val billCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->

        if (success) {

            billAttachmentUri = pendingCameraFile
                ?.let { Uri.fromFile(it).toString() }
                .orEmpty()

            Toast.makeText(
                context,
                "Bill photo attach हो गया",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            pendingCameraUri = null

            Toast.makeText(
                context,
                "Camera photo cancel हो गई",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    var showItemSearch by remember {
        mutableStateOf(false)
    }

    var itemSearchCategory by remember {
        mutableStateOf("")
    }

    var selectedMasterItem by remember {
        mutableStateOf<ItemMaster?>(null)
    }

    val billItems = remember {
        mutableStateListOf<DraftExpenseItem>()
    }

    val billTotal = billItems.sumOf {
        it.amount
    }

    if (showAddNewItem) {

        AddCustomItemScreen(
            itemMasterDao = itemMasterDao,
            categoryMasterDao = categoryMasterDao,
            onBack = {
                showAddNewItem = false
            }
        )

        return
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
                        text = "Cash Out",
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

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Button(
                    onClick = {
                        itemSearchCategory = ""
                        showItemSearch = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD9480F),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    AppText(
                        text = "  Add New Bill",
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {

                AppText(
                    text = "नया Bill / New Expense",
                    modifier = Modifier.padding(top = 10.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                ExpenseItemsTop(
                    items = billItems,
                    total = billTotal,
                    onRemove = { billItems.removeAt(it) }
                )
            }

            item {

                DynamicCategoryDropdown(
                    categories = activeCategories,
                    selectedCategory = selectedCategory,

                    onCategoryTextChanged = {
                        selectedCategory = it
                    },

                    onCategorySelected = { newCategory ->
                        selectedCategory = newCategory
                    }
                )
            }

            item {

                Button(
                    onClick = {
                        showAddNewItem = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )

                    AppText("  Add New Item")
                }
            }

            item {

                OutlinedTextField(
                    value = shopName,
                    onValueChange = {
                        shopName = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        AppText("दुकान / Shop or Vendor (Optional)")
                    },
                    singleLine = true
                )
            }

            item {

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        OutlinedButton(
                            onClick = {

                                try {

                                    val billsDirectory = File(
                                        context.filesDir,
                                        "bills"
                                    ).apply {
                                        mkdirs()
                                    }

                                    val cameraFile = File(
                                        billsDirectory,
                                        "bill_camera_${System.currentTimeMillis()}.jpg"
                                    )

                                    val cameraUri =
                                        FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            cameraFile
                                        )

                                    pendingCameraUri = cameraUri
                                    pendingCameraFile = cameraFile

                                    billCameraLauncher.launch(
                                        cameraUri
                                    )

                                } catch (error: Exception) {

                                    Toast.makeText(
                                        context,
                                        "Camera नहीं खुला: " +
                                                (error.message ?: "Camera error"),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },

                            modifier = Modifier.weight(1f)
                        ) {

                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera"
                            )

                            AppText(" Camera")
                        }

                        OutlinedButton(
                            onClick = {
                                billPicker.launch("*/*")
                            },

                            modifier = Modifier.weight(1f)
                        ) {

                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Gallery or PDF"
                            )

                            AppText(
                                if (billAttachmentUri.isBlank()) {
                                    " Gallery / PDF"
                                } else {
                                    " Bill Selected"
                                }
                            )
                        }
                    }

                    if (billAttachmentUri.isNotBlank()) {

                        OutlinedButton(
                            onClick = {
                                billAttachmentUri = ""
                                billNumber = ""
                                pendingCameraUri = null
                                pendingCameraFile = null
                            },

                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove attachment"
                            )

                            AppText(" Attached Bill हटाएँ")
                        }
                    }
                }
            }

            item {

                OutlinedTextField(
                    value = billNumber,
                    onValueChange = { billNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { AppText("Bill / Invoice Number") },
                    singleLine = true
                )
            }

            item {

                OutlinedButton(
                    onClick = {
                        showDatePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Select Date"
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    AppText(
                        text = "तारीख: ${formatSelectedDate(selectedDate)}"
                    )
                }
            }

            item {

                PaymentDropdown(
                    selectedPayment = selectedPayment,

                    onPaymentSelected = {
                        selectedPayment = it
                    }
                )
            }

            if (false && billItems.isEmpty()) {

                item {

                    AppText(
                        text = "अभी कोई item नहीं जोड़ा गया है।",
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

            } else if (false) {

                itemsIndexed(
                    items = billItems
                ) { index, item ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
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
                                    text = item.itemNameHindi,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                AppText(
                                    text = item.itemNameEnglish,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )

                                AppText(
                                    text =
                                        "${item.quantity} ${item.unit} × " +
                                                formatAmount(item.rate),

                                    fontSize = 13.sp
                                )

                                AppText(
                                    text = formatAmount(item.amount),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = {
                                    billItems.removeAt(index)
                                }
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Item"
                                )
                            }
                        }
                    }
                }
            }

            if (false) item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        AppText(
                            text = "Bill Total",
                            fontSize = 14.sp
                        )

                        AppText(
                            text = formatAmount(billTotal),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        AppText(
                            text = "Items: ${billItems.size}",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {

                OutlinedTextField(
                    value = description,

                    onValueChange = {
                        description = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        AppText("नोट / Description")
                    },

                    minLines = 2
                )
            }

            item {

                Button(
                    onClick = {

                        coroutineScope.launch {

                            val cleanCategory =
                                selectedCategory.trim()

                            if (
                                categoryMasterDao
                                    .categoryNameExists(
                                        cleanCategory
                                    ) == 0
                            ) {
                                categoryMasterDao.insertCategory(
                                    CategoryMaster(
                                        nameHindi = cleanCategory,
                                        nameEnglish = "",
                                        searchAliases = cleanCategory,
                                        isCustom = true,
                                        isActive = true,
                                        sortOrder = 1000
                                    )
                                )
                            }

                            onSave(
                                cleanCategory,
                                selectedPayment,
                                description.trim(),
                                shopName.trim(),
                                billAttachmentUri,
                                billNumber.trim(),
                                selectedDate,
                                billItems.toList()
                            )
                        }
                    },

                    enabled =
                        billItems.isNotEmpty() &&
                                selectedCategory.isNotBlank(),

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                    shape = RoundedCornerShape(14.dp)
                ) {

                    AppText(
                        text = "पूरा Bill सेव करें",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showItemSearch) {

        ItemSearchDialog(
            category = itemSearchCategory,
            itemMasterDao = itemMasterDao,

            onItemSelected = { item ->
                selectedCategory = item.category
                selectedMasterItem = item
                showItemSearch = false
            },

            onDismiss = {
                showItemSearch = false
            }
        )
    }
    selectedMasterItem?.let { item ->

        AddBillItemDialog(
            item = item,

            onAdd = { draftItem ->
                billItems.add(draftItem)
                selectedMasterItem = null
            },

            onDismiss = {
                selectedMasterItem = null
            }
        )
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

    scannedBill?.let { initial ->
        BillScanReviewDialog(
            initial = initial,
            onConfirm = { updated ->
                coroutineScope.launch {
                    val masterItems = itemMasterDao.getAllItemsOnce()
                    billNumber = updated.billNumber
                    if (updated.shopName.isNotBlank()) shopName = updated.shopName
                    updated.billDate?.let { selectedDate = it }
                    billItems.clear()
                    updated.items.forEach { imported ->
                        val matched = findBestItemMatch(imported.description, masterItems)
                        billItems += DraftExpenseItem(
                            itemMasterId = matched?.id,
                            itemNameHindi = matched?.nameHindi ?: imported.description,
                            itemNameEnglish = matched?.nameEnglish ?: imported.description,
                            quantity = imported.quantity,
                            unit = imported.unit,
                            rate = if (imported.quantity > 0) imported.amount / imported.quantity else imported.rate
                        )
                    }
                    scannedBill = null
                    Toast.makeText(context, "Bill details Expense में भर दी गई हैं", Toast.LENGTH_SHORT).show()
                }
            },
            onDismiss = { scannedBill = null }
        )
    }
}

@Composable
private fun ExpenseItemsTop(
    items: List<DraftExpenseItem>,
    total: Double,
    onRemove: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppText("Items (${items.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (items.isEmpty()) {
            AppText("अभी कोई item नहीं जोड़ा गया है।")
        }
        items.forEachIndexed { index, item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(Modifier.fillMaxWidth().padding(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        AppText(item.itemNameHindi, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        AppText(item.itemNameEnglish, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                        AppText("${item.quantity} ${item.unit} × ${formatAmount(item.rate)} = ${formatAmount(item.amount)}")
                    }
                    IconButton({ onRemove(index) }) { Icon(Icons.Default.Delete, "Remove Item") }
                }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                AppText("Bill Total", fontSize = 13.sp)
                AppText(formatAmount(total), fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BillScanReviewDialog(
    initial: ImportedBillData,
    onConfirm: (ImportedBillData) -> Unit,
    onDismiss: () -> Unit
) {
    var billNumber by remember(initial) { mutableStateOf(initial.billNumber) }
    var shopName by remember(initial) { mutableStateOf(initial.shopName) }
    var billDate by remember(initial) { mutableStateOf(initial.billDate ?: System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var itemText by remember(initial) {
        mutableStateOf(
            initial.items.joinToString("\n") { item ->
                "${item.description} | ${item.quantity} | ${item.unit} | ${item.rate} | ${item.amount}"
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppText("Scanned Bill जाँचें", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                AppText("गलत जानकारी को Expense में जोड़ने से पहले Edit करें।")
                OutlinedTextField(
                    billNumber,
                    { billNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { AppText("Bill / Invoice Number") }
                )
                OutlinedTextField(
                    shopName,
                    { shopName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { AppText("Shop / Vendor") }
                )
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) { AppText("Date: ${formatSelectedDate(billDate)}") }
                AppText("Format: Item | Qty | Unit | Rate | Amount")
                OutlinedTextField(
                    itemText,
                    { itemText = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 10,
                    label = { AppText("Bill Items") }
                )
                val parsedItems = itemText.lineSequence().mapNotNull(::parseBillReviewLine).toList()
                AppText("Recognized Items: ${parsedItems.size}")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) { AppText("रद्द करें") }
                    Spacer(modifier = Modifier.padding(4.dp))
                    Button(
                        onClick = {
                            onConfirm(
                                initial.copy(
                                    billNumber = billNumber.trim(),
                                    billDate = billDate,
                                    shopName = shopName.trim(),
                                    items = parsedItems
                                )
                            )
                        },
                        enabled = parsedItems.isNotEmpty()
                    ) { AppText("Expense में भरें") }
                }
            }
        }
    }

    if (showDatePicker) {
        ExpenseDatePickerDialog(
            initialDate = billDate,
            onDateSelected = {
                billDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DynamicCategoryDropdown(
        categories: List<CategoryMaster>,
        selectedCategory: String,
        onCategoryTextChanged: (String) -> Unit,
        onCategorySelected: (String) -> Unit
    ) {
        var expanded by remember {
            mutableStateOf(false)
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = onCategoryTextChanged,
                readOnly = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = {
                    AppText("Category / श्रेणी")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                categories.forEach { category ->

                    DropdownMenuItem(
                        text = {
                            Column {
                                AppText(
                                    text = category.nameHindi,
                                    fontWeight = FontWeight.Medium
                                )

                                if (category.nameEnglish.isNotBlank()) {
                                    AppText(
                                        text = category.nameEnglish,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        },

                        onClick = {
                            onCategorySelected(category.nameHindi)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
