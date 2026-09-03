package com.bharatbhushan.dailyexpensetracker

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "item_master",
    indices = [
        Index(value = ["category"]),
        Index(value = ["nameHindi"]),
        Index(value = ["nameEnglish"])
    ]
)
data class ItemMaster(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nameHindi: String,
    val nameEnglish: String,
    val searchAliases: String,
    val category: String,
    val defaultUnit: String,
    val isCustom: Boolean = false
)