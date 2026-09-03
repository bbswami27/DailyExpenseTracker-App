package com.bharatbhushan.dailyexpensetracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Insert
    suspend fun insertShoppingList(
        shoppingList: ShoppingList
    ): Long

    @Insert
    suspend fun insertShoppingListItem(
        item: ShoppingListItem
    )

    @Update
    suspend fun updateShoppingListItem(
        item: ShoppingListItem
    )

    @Query("UPDATE shopping_lists SET attachmentUri = :attachmentUri WHERE id = :shoppingListId")
    suspend fun updateAttachment(
        shoppingListId: Int,
        attachmentUri: String
    )

    @Delete
    suspend fun deleteShoppingList(
        shoppingList: ShoppingList
    )

    @Delete
    suspend fun deleteShoppingListItem(
        item: ShoppingListItem
    )

    @Query(
        """
        SELECT *
        FROM shopping_lists
        ORDER BY createdAt DESC
        """
    )
    fun getAllShoppingLists():
            Flow<List<ShoppingList>>

    @Query(
        """
        SELECT *
        FROM shopping_list_items
        WHERE shoppingListId = :shoppingListId
        ORDER BY isPurchased ASC, id ASC
        """
    )
    fun getItemsForShoppingList(
        shoppingListId: Int
    ): Flow<List<ShoppingListItem>>

    @Query(
        """
        UPDATE shopping_list_items
        SET isPurchased = :isPurchased
        WHERE id = :itemId
        """
    )
    suspend fun updatePurchasedStatus(
        itemId: Int,
        isPurchased: Boolean
    )
}
