package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
fun AddShoppingItemDialog(
    item: ItemMaster,
    onAdd: (
        quantity: Double,
        unit: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var quantity by remember {
        mutableStateOf("1")
    }

    var selectedUnit by remember(item.id) {
        mutableStateOf(item.defaultUnit)
    }

    val enteredQuantity = quantity.toDoubleOrNull()

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

                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        quantity = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        AppText("Quantity / मात्रा")
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
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    if (
                        enteredQuantity != null &&
                        enteredQuantity > 0
                    ) {
                        onAdd(
                            enteredQuantity,
                            selectedUnit
                        )
                    }
                },
                enabled =
                    enteredQuantity != null &&
                            enteredQuantity > 0
            ) {
                AppText("List में जोड़ें")
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
