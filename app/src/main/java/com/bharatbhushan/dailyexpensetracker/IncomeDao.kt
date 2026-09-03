package com.bharatbhushan.dailyexpensetracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Insert
    suspend fun insertIncome(income: Income)

    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

    @Query(
        """
        SELECT *
        FROM income
        ORDER BY receivedAt DESC
        """
    )
    fun getAllIncome(): Flow<List<Income>>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0.0)
        FROM income
        WHERE receivedAt >= :startTime
        AND receivedAt < :endTime
        """
    )
    fun getIncomeTotalBetween(
        startTime: Long,
        endTime: Long
    ): Flow<Double>
    @Query(
        """
    SELECT
        strftime(
            '%Y-%m',
            receivedAt / 1000,
            'unixepoch',
            'localtime'
        ) AS monthKey,
        COALESCE(SUM(amount), 0.0) AS total
    FROM income
    GROUP BY monthKey
    ORDER BY monthKey DESC
    LIMIT 6
    """
    )
    fun getMonthlyIncomeTotals():
            Flow<List<MonthlyTotal>>
}