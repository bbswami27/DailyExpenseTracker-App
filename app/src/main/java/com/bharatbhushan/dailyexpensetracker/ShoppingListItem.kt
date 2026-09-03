package com.bharatbhushan.dailyexpensetracker

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_list_items",

    foreignKeys = [
        ForeignKey(
            entity = ShoppingList::class,
            parentColumns = ["id"],
            childColumns = ["shoppingListId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["shoppingListId"]),
        Index(value = ["itemMasterId"])
    ]
)
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val shoppingListId: Int,
    val itemMasterId: Int?,
    val itemNameHindi: String,
    val itemNameEnglish: String,
    @ColumnInfo(defaultValue = "''")
    val brand: String = "",
    val quantity: Double,
    val unit: String,
    val estimatedRate: Double = 0.0,
    val isPurchased: Boolean = false
) {

    val estimatedAmount: Double
        get() = quantity * estimatedRate
}
