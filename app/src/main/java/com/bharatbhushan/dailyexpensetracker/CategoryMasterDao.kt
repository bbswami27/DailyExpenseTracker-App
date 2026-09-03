package com.bharatbhushan.dailyexpensetracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryMasterDao {

    @Insert(
        onConflict = OnConflictStrategy.IGNORE
    )
    suspend fun insertCategory(
        category: CategoryMaster
    ): Long

    @Update
    suspend fun updateCategory(
        category: CategoryMaster
    )

    @Query(
        """
        SELECT *
        FROM category_master
        WHERE isActive = 1
        ORDER BY sortOrder ASC, nameHindi ASC
        """
    )
    fun getActiveCategories():
            Flow<List<CategoryMaster>>

    @Query(
        """
        UPDATE category_master
        SET isActive = 0
        WHERE id = :categoryId
        """
    )
    suspend fun deactivateCategory(
        categoryId: Int
    )

    @Query(
        """
        SELECT COUNT(*)
        FROM category_master
        WHERE nameHindi = :nameHindi
        """
    )
    suspend fun categoryNameExists(
        nameHindi: String
    ): Int
}