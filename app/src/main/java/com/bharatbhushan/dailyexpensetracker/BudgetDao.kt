package com.bharatbhushan.dailyexpensetracker

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Upsert
    suspend fun saveBudget(budget: Budget)

    @Query(
        """
        SELECT *
        FROM budgets
        WHERE monthKey = :monthKey
        ORDER BY category ASC
        """
    )
    fun getBudgetsForMonth(
        monthKey: String
    ): Flow<List<Budget>>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0.0)
        FROM budgets
        WHERE monthKey = :monthKey
        """
    )
    fun getTotalBudgetForMonth(
        monthKey: String
    ): Flow<Double>
}