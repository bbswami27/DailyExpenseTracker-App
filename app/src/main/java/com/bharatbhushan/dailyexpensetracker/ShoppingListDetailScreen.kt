package com.bharatbhushan.dailyexpensetracker

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListDetailScreen(
    shoppingList: ShoppingList,
    shoppingListDao: ShoppingListDao,
    itemMasterDao: ItemMasterDao,
    categoryMasterDao: CategoryMasterDao,
    onBack: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val shoppingItems by shoppingListDao
        .getItemsForShoppingList(shoppingList.id)
        .collectAsState(initial = emptyList())


    var showItemSearch by remember {
        mutableStateOf(false)
    }

    var showAddNewItem by remember {
        mutableStateOf(false)
    }

    var selectedMasterItem by remember {
        mutableStateOf<ItemMaster?>(null)
    }

    var attachmentUri by remember(shoppingList.id) {
        mutableStateOf(shoppingList.attachmentUri)
    }

    var importedText by remember {
        mutableStateOf("")
    }

    var showImportReview by remember {
        mutableStateOf(false)
    }

    var isImporting by remember {
        mutableStateOf(false)
    }

    var editingItem by remember {
        mutableStateOf<ShoppingListItem?>(null)
    }

    val documentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { selectedUri ->
        if (selectedUri != null) {
            isImporting = true
            coroutineScope.launch {
                runCatching {
                    importShoppingDocument(context, selectedUri)
                }.onSuccess { result ->
                    attachmentUri = result.storedUri
                    shoppingListDao.updateAttachment(shoppingList.id, result.storedUri)
                    val masterItems = itemMasterDao.getAllItemsOnce()
                    importedText = matchShoppingItems(
                        result.suggestedItems,
                        masterItems
                    ).joinToString("\n") { item ->
                        "${item.itemName} | ${item.brand} | ${item.quantity} | ${item.unit}"
                    }
                    showImportReview = true
                }.onFailure { error ->
                    Toast.makeText(
                        context,
                        "Import नहीं हुआ: ${error.message ?: "File error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                isImporting = false
            }
        }
    }

    val purchasedCount = shoppingItems.count {
        it.isPurchased
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
                    Column {

                        AppText(
                            text = shoppingList.name,
                            fontWeight = FontWeight.Bold
                        )

                        AppText(
                            text = "$purchasedCount/${shoppingItems.size} खरीदे",
                            fontSize = 12.sp
                        )
                    }
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

                Button(
                    onClick = {
                        showItemSearch = true
                    },

                    modifier = Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Shopping Item"
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    AppText("Shopping Item जोड़ें")
                }
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

                Button(
                    onClick = {
                        shareShoppingListAsText(
                            context = context,
                            shoppingList = shoppingList,
                            items = shoppingItems
                        )
                    },

                    enabled = shoppingItems.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    AppText("WhatsApp / Text Share")
                }
            }

            item {

                Button(
                    onClick = {
                        exportShoppingListAsCsv(
                            context = context,
                            shoppingList = shoppingList,
                            items = shoppingItems
                        )
                    },

                    enabled = shoppingItems.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    AppText("CSV Export")
                }
            }

            item {

                Button(
                    onClick = {
                        exportShoppingListAsPdf(
                            context = context,
                            shoppingList = shoppingList,
                            items = shoppingItems
                        )
                    },

                    enabled = shoppingItems.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    AppText("PDF Export")
                }
            }
            if (shoppingItems.isEmpty()) {

                item {

                    AppText(
                        text = "इस list में अभी कोई item नहीं है।",
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }

            } else {

                items(
                    items = shoppingItems,
                    key = { item -> item.id }
                ) { item ->

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
                                .padding(12.dp)
                        ) {

                            Checkbox(
                                checked = item.isPurchased,

                                onCheckedChange = { checked ->

                                    coroutineScope.launch {

                                        shoppingListDao
                                            .updatePurchasedStatus(
                                                itemId = item.id,
                                                isPurchased = checked
                                            )
                                    }
                                }
                            )

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
                                    fontSize = 12.sp
                                )

                                if (item.brand.isNotBlank()) {
                                    AppText(
                                        text = "Brand: ${item.brand}",
                                        fontSize = 12.sp
                                    )
                                }

                                AppText(
    text = "${item.quantity} ${item.unit}",
    fontSize = 14.sp,
    fontWeight = FontWeight.Medium
)
}
                            AppText(
                                text =
                                    if (item.isPurchased) {
                                        "✅ खरीदा / Purchased"
                                    } else {
                                        "⬜ बाकी / Pending"
                                    },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            IconButton(
                                onClick = { editingItem = item }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Item"
                                )
                            }

                            IconButton(
                                onClick = {

                                    coroutineScope.launch {

                                        shoppingListDao
                                            .deleteShoppingListItem(
                                                item
                                            )
                                    }
                                }
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Item"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showItemSearch) {

        ItemSearchDialog(
            category = "",
            itemMasterDao = itemMasterDao,

            onItemSelected = { item ->
                selectedMasterItem = item
                showItemSearch = false
            },

            onDismiss = {
                showItemSearch = false
            }
        )
    }

    selectedMasterItem?.let { item ->

        AddShoppingItemDialog(
            item = item,

            onAdd = { quantity, unit ->

                coroutineScope.launch {

                    shoppingListDao.insertShoppingListItem(
                        ShoppingListItem(
                            shoppingListId = shoppingList.id,
                            itemMasterId = item.id,
                            itemNameHindi = item.nameHindi,
                            itemNameEnglish = item.nameEnglish,
                            quantity = quantity,
                            unit = unit,
                            estimatedRate = 0.0
                        )
                    )

                    selectedMasterItem = null
                }
            },

            onDismiss = {
                selectedMasterItem = null
            }
        )
    }

    if (showImportReview) {
        AlertDialog(
            onDismissRequest = { showImportReview = false },
            title = { AppText("Import किए गए Items जाँचें") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppText("Format: Item | Brand | Quantity | Unit. गलत line बदलें या हटाएँ।")
                    OutlinedTextField(
                        value = importedText,
                        onValueChange = { importedText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 8,
                        label = { AppText("Shopping Items") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val importedItems = importedText.lineSequence()
                            .mapNotNull(::parseReviewedShoppingLine)
                            .distinctBy { "${it.itemName.lowercase()}|${it.brand.lowercase()}" }
                            .toList()
                        coroutineScope.launch {
                            val existing = shoppingItems.map { it.itemNameHindi.lowercase() }.toSet()
                            val masterItems = itemMasterDao.getAllItemsOnce()
                            importedItems.filterNot { it.itemName.lowercase() in existing }.forEach { imported ->
                                val matched = findBestItemMatch(imported.itemName, masterItems)
                                shoppingListDao.insertShoppingListItem(
                                    ShoppingListItem(
                                        shoppingListId = shoppingList.id,
                                        itemMasterId = matched?.id,
                                        itemNameHindi = matched?.nameHindi ?: imported.itemName,
                                        itemNameEnglish = matched?.nameEnglish ?: imported.itemName,
                                        brand = imported.brand,
                                        quantity = imported.quantity,
                                        unit = imported.unit,
                                        estimatedRate = 0.0
                                    )
                                )
                            }
                            showImportReview = false
                            Toast.makeText(context, "Items Shopping List में जोड़ दिए गए", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = importedText.isNotBlank()
                ) { AppText("List में जोड़ें") }
            },
            dismissButton = {
                TextButton(onClick = { showImportReview = false }) {
                    AppText("केवल Attachment रखें")
                }
            }
        )
    }

    editingItem?.let { item ->
        EditShoppingItemDialog(
            item = item,
            onSave = { updated ->
                coroutineScope.launch {
                    shoppingListDao.updateShoppingListItem(updated)
                    editingItem = null
                }
            },
            onDismiss = { editingItem = null }
        )
    }

}

@Composable
private fun EditShoppingItemDialog(
    item: ShoppingListItem,
    onSave: (ShoppingListItem) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember(item.id) { mutableStateOf(item.itemNameHindi) }
    var brand by remember(item.id) { mutableStateOf(item.brand) }
    var quantity by remember(item.id) { mutableStateOf(item.quantity.toString()) }
    var unit by remember(item.id) { mutableStateOf(item.unit) }
    var rate by remember(item.id) { mutableStateOf(item.estimatedRate.toString()) }
    val quantityValue = quantity.toDoubleOrNull()
    val rateValue = rate.toDoubleOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { AppText("Shopping Item Edit करें") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { AppText("Item Name") })
                OutlinedTextField(brand, { brand = it }, label = { AppText("Brand") })
                OutlinedTextField(
                    quantity,
                    { quantity = it },
                    label = { AppText("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(unit, { unit = it }, label = { AppText("Unit") })
                OutlinedTextField(
                    rate,
                    { rate = it },
                    label = { AppText("Rate") },
                    prefix = { AppText("${currentCurrencySymbol()} ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        item.copy(
                            itemNameHindi = name.trim(),
                            itemNameEnglish = name.trim(),
                            brand = brand.trim(),
                            quantity = quantityValue!!,
                            unit = unit.trim(),
                            estimatedRate = rateValue!!
                        )
                    )
                },
                enabled = name.isNotBlank() && unit.isNotBlank() &&
                        quantityValue != null && quantityValue > 0 &&
                        rateValue != null && rateValue >= 0
            ) { AppText("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { AppText("रद्द करें") } }
    )
}
