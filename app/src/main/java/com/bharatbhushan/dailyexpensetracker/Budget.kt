package com.bharatbhushan.dailyexpensetracker

import androidx.room.Entity

@Entity(
    tableName = "budgets",
    primaryKeys = ["monthKey", "category"]
)
data class Budget(
    val monthKey: String,
    val category: String,
    val amount: Double
)