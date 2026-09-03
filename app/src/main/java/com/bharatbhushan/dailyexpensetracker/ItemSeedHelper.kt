package com.bharatbhushan.dailyexpensetracker

data class SeedItem(
    val hindi: String,
    val english: String,
    val aliases: String,
    val unit: String
)

fun SeedItem.toItemMaster(
    category: String
): ItemMaster {

    return ItemMaster(
        nameHindi = hindi,
        nameEnglish = english,
        searchAliases = aliases,
        category = category,
        defaultUnit = unit,
        isCustom = false
    )
}