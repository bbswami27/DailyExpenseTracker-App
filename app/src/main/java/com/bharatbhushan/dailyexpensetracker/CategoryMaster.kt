package com.bharatbhushan.dailyexpensetracker

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "category_master",
    indices = [
        Index(
            value = ["nameHindi"],
            unique = true
        )
    ]
)
data class CategoryMaster(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nameHindi: String,
    val nameEnglish: String,
    val searchAliases: String,
    val isCustom: Boolean = true,
    val isActive: Boolean = true,
    val sortOrder: Int = 100
)