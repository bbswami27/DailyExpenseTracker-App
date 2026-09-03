package com.bharatbhushan.dailyexpensetracker

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val category: String,
    val paymentMode: String,
    val description: String,
    @ColumnInfo(defaultValue = "''")
    val shopName: String = "",
    @ColumnInfo(defaultValue = "''")
    val billAttachmentUri: String = "",
    @ColumnInfo(defaultValue = "''")
    val billNumber: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
