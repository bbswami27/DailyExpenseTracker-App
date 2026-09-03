package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddBillItemDialog(
    item: ItemMaster,
    onAdd: (DraftExpenseItem) -> Unit,
    onDismiss: () -> Unit
) {

    var quantity by remember {
        mutableStateOf("1")
    }

    var rate by remember {
        mutableStateOf("")
    }

    var selectedUnit by remember(item.id) {
        mutableStateOf(item.defaultUnit)
    }

    val calculatedAmount =
        (quantity.toDoubleOrNull() ?: 0.0) *
                (rate.toDoubleOrNull() ?: 0.0)

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Column {
                AppText(item.nameHindi)
                AppText(item.nameEnglish)
            }
        },

        text = {

            Column {

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = quantity,

                        onValueChange = {
                            quantity = it
                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),

                        label = {
                            AppText("Quantity")
                        },

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),

                        singleLine = true
                    )

                    UnitDropdown(
                        selectedUnit = selectedUnit,

                        onUnitSelected = {
                            selectedUnit = it
                        },

                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )
                }

                OutlinedTextField(
                    value = rate,

                    onValueChange = {
                        rate = it
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),

                    label = {
                        AppText("Rate / दर")
                    },

                    prefix = {
                        AppText("${currentCurrencySymbol()} ")
                    },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),

                    singleLine = true
                )

                AppText(
                    text = "Amount: ${formatAmount(calculatedAmount)}",
                    modifier = Modifier.padding(top = 14.dp)
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    val enteredQuantity =
                        quantity.toDoubleOrNull()

                    val enteredRate =
                        rate.toDoubleOrNull()

                    if (
                        enteredQuantity != null &&
                        enteredQuantity > 0 &&
                        enteredRate != null &&
                        enteredRate >= 0
                    ) {

                        onAdd(
                            DraftExpenseItem(
                                itemMasterId = item.id,
                                itemNameHindi = item.nameHindi,
                                itemNameEnglish = item.nameEnglish,
                                quantity = enteredQuantity,
                                unit = selectedUnit,
                                rate = enteredRate
                            )
                        )
                    }
                }
            ) {
                AppText("Item जोड़ें")
            }
        },

        dismissButton = {

            TextButton(onClick = onDismiss) {
                AppText("रद्द करें")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val units = listOf(
        "Kg",
        "Gram",
        "Litre",
        "Millilitre",
        "Piece",
        "Packet",
        "Box",
        "Bottle",
        "Jar",
        "Can",
        "Dozen",
        "Pair",
        "Roll",
        "Bunch",
        "Meal",
        "Visit",
        "Test",
        "Session",
        "Ticket",
        "Trip",
        "Night",
        "Recharge",
        "Bill",
        "Payment"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,

        onExpandedChange = {
            expanded = !expanded
        },

        modifier = modifier
    ) {

        OutlinedTextField(
            value = selectedUnit,
            onValueChange = {},
            readOnly = true,

            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),

            label = {
                AppText("Unit")
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

            units.forEach { unit ->

                DropdownMenuItem(
                    text = {
                        AppText(unit)
                    },

                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
