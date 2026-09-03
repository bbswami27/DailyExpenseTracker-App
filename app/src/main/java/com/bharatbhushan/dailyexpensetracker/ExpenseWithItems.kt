package com.bharatbhushan.dailyexpensetracker

import androidx.room.Embedded
import androidx.room.Relation

data class ExpenseWithItems(
    @Embedded
    val expense: Expense,

    @Relation(
        parentColumn = "id",
        entityColumn = "expenseId"
    )
    val items: List<ExpenseLineItem>
)

data class ItemPriceHistory(
    val expenseId: Int,
    val itemNameHindi: String,
    val itemNameEnglish: String,
    val quantity: Double,
    val unit: String,
    val rate: Double,
    val amount: Double,
    val purchasedAt: Long,
    val shopName: String
)

data class MonthlyItemRate(
    val itemMasterId: Int?,
    val itemNameHindi: String,
    val itemNameEnglish: String,
    val monthKey: String,
    val averageRate: Double,
    val lowestRate: Double,
    val highestRate: Double,
    val purchaseCount: Int,
    val totalQuantity: Double,
    val totalAmount: Double
)

data class MonthlyCategoryExpense(
    val monthKey: String,
    val category: String,
    val totalAmount: Double,
    val entryCount: Int
)
