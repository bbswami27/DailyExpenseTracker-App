package com.bharatbhushan.dailyexpensetracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsGoalDao {

    @Insert
    suspend fun insertGoal(
        goal: SavingsGoal
    ): Long

    @Update
    suspend fun updateGoal(
        goal: SavingsGoal
    )

    @Delete
    suspend fun deleteGoal(
        goal: SavingsGoal
    )

    @Query(
        """
        SELECT *
        FROM savings_goals
        ORDER BY isCompleted ASC, createdAt DESC
        """
    )
    fun getAllGoals():
            Flow<List<SavingsGoal>>

    @Query(
        """
        UPDATE savings_goals
        SET savedAmount = savedAmount + :amount,
            isCompleted =
                CASE
                    WHEN savedAmount + :amount >= targetAmount
                    THEN 1
                    ELSE 0
                END
        WHERE id = :goalId
        """
    )
    suspend fun addMoney(
        goalId: Int,
        amount: Double
    )
}