package com.bharatbhushan.dailyexpensetracker

data class DraftExpenseItem(
    val itemMasterId: Int?,
    val itemNameHindi: String,
    val itemNameEnglish: String,
    val quantity: Double,
    val unit: String,
    val rate: Double
) {

    val amount: Double
        get() = quantity * rate
}