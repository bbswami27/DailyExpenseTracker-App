package com.bharatbhushan.dailyexpensetracker

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expense_line_items",

    foreignKeys = [
        ForeignKey(
            entity = Expense::class,
            parentColumns = ["id"],
            childColumns = ["expenseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["expenseId"]),
        Index(value = ["itemMasterId"])
    ]
)
data class ExpenseLineItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val expenseId: Int,

    val itemMasterId: Int?,

    val itemNameHindi: String,

    val itemNameEnglish: String,

    val quantity: Double,

    val unit: String,

    val rate: Double,

    val amount: Double
)