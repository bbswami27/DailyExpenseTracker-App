package com.bharatbhushan.dailyexpensetracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemMasterDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(
        items: List<ItemMaster>
    )

    @Insert
    suspend fun insertCustomItem(
        item: ItemMaster
    )

    @Query(
        """
        SELECT *
        FROM item_master
        WHERE (
            :category = ''
            OR category = :category
        )
        AND (
            :search = ''
            OR nameHindi LIKE '%' || :search || '%'
            OR nameEnglish LIKE '%' || :search || '%'
            OR searchAliases LIKE '%' || :search || '%'
        )
        ORDER BY category ASC, nameHindi ASC
        """
    )
    fun searchItems(
        category: String,
        search: String
    ): Flow<List<ItemMaster>>

    @Query(
        """
        SELECT COUNT(*)
        FROM item_master
        """
    )
    suspend fun getItemCount(): Int

    @Query("SELECT * FROM item_master ORDER BY nameHindi ASC")
    suspend fun getAllItemsOnce(): List<ItemMaster>

    @Query("SELECT * FROM item_master ORDER BY category ASC, nameHindi ASC")
    fun getAllItems(): Flow<List<ItemMaster>>

    @Query("UPDATE item_master SET category = :category WHERE id = :itemId")
    suspend fun updateItemCategory(itemId: Int, category: String)

    @Query(
        """
        SELECT COUNT(*)
        FROM item_master
        WHERE category = :category
        """
    )
    fun getCategoryItemCount(
        category: String
    ): Flow<Int>
}
