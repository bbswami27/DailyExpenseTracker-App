package com.bharatbhushan.dailyexpensetracker

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDatePickerDialog(
    initialDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,

        confirmButton = {
            TextButton(
                onClick = {

                    val selectedDate =
                        datePickerState.selectedDateMillis

                    if (selectedDate != null) {
                        onDateSelected(selectedDate)
                    }
                }
            ) {
                AppText("तारीख चुनें")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                AppText("रद्द करें")
            }
        }
    ) {

        DatePicker(
            state = datePickerState
        )
    }
}

fun formatSelectedDate(
    time: Long
): String {

    val formatter = SimpleDateFormat(
        "dd MMMM yyyy",
        Locale("hi", "IN")
    )

    return formatter.format(Date(time))
}
