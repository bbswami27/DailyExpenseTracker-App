package com.bharatbhushan.dailyexpensetracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query(
        """
        SELECT *
        FROM expenses
        ORDER BY createdAt DESC
        """
    )
    fun getAllExpenses(): Flow<List<Expense>>

    @Transaction
    @Query(
        """
        SELECT *
        FROM expenses
        ORDER BY createdAt DESC
        """
    )
    fun getAllExpensesWithItems():
            Flow<List<ExpenseWithItems>>

    @Query(
        """
        SELECT *
        FROM expenses
        WHERE createdAt >= :startTime
        AND createdAt < :endTime
        AND (:category = '' OR category = :category)
        AND (:paymentMode = '' OR paymentMode = :paymentMode)
        ORDER BY createdAt DESC
        """
    )
    fun getFilteredExpenses(
        startTime: Long,
        endTime: Long,
        category: String,
        paymentMode: String
    ): Flow<List<Expense>>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0.0)
        FROM expenses
        WHERE createdAt >= :startTime
        AND createdAt < :endTime
        """
    )
    fun getTotalBetween(
        startTime: Long,
        endTime: Long
    ): Flow<Double>

    @Query(
        """
        SELECT category, COALESCE(SUM(amount), 0.0) AS total
        FROM expenses
        WHERE createdAt >= :startTime
        AND createdAt < :endTime
        GROUP BY category
        ORDER BY total DESC
        """
    )
    fun getCategoryTotals(
        startTime: Long,
        endTime: Long
    ): Flow<List<CategoryTotal>>
    @Query(
        """
    SELECT
        strftime(
            '%Y-%m',
            createdAt / 1000,
            'unixepoch',
            'localtime'
        ) AS monthKey,
        COALESCE(SUM(amount), 0.0) AS total
    FROM expenses
    GROUP BY monthKey
    ORDER BY monthKey DESC
    LIMIT 6
    """
    )
    fun getMonthlyExpenseTotals():
            Flow<List<MonthlyTotal>>

    @Query(
        """
        SELECT
            strftime(
                '%Y-%m',
                createdAt / 1000,
                'unixepoch',
                'localtime'
            ) AS monthKey,
            category AS category,
            COALESCE(SUM(amount), 0.0) AS totalAmount,
            COUNT(*) AS entryCount
        FROM expenses
        WHERE createdAt >= :startTime
        AND createdAt < :endTime
        GROUP BY monthKey, category
        ORDER BY monthKey DESC, totalAmount DESC
        """
    )
    fun getMonthlyCategoryExpenses(
        startTime: Long,
        endTime: Long
    ): Flow<List<MonthlyCategoryExpense>>
}
