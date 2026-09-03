package com.bharatbhushan.dailyexpensetracker

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_lists")
data class ShoppingList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    @ColumnInfo(defaultValue = "''")
    val attachmentUri: String = ""
)
