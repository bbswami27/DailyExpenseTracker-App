package com.bharatbhushan.dailyexpensetracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseLineItemDao {

    @Insert
    suspend fun insertLineItems(
        items: List<ExpenseLineItem>
    )

    @Query(
        """
        SELECT *
        FROM expense_line_items
        WHERE expenseId = :expenseId
        ORDER BY id ASC
        """
    )
    fun getItemsForExpense(
        expenseId: Int
    ): Flow<List<ExpenseLineItem>>

    @Query(
        """
        DELETE FROM expense_line_items
        WHERE expenseId = :expenseId
        """
    )
    suspend fun deleteItemsForExpense(
        expenseId: Int
    )

    @Query(
        """
        SELECT
            lineItem.expenseId AS expenseId,
            lineItem.itemNameHindi AS itemNameHindi,
            lineItem.itemNameEnglish AS itemNameEnglish,
            lineItem.quantity AS quantity,
            lineItem.unit AS unit,
            lineItem.rate AS rate,
            lineItem.amount AS amount,
            expense.createdAt AS purchasedAt,
            expense.shopName AS shopName
        FROM expense_line_items AS lineItem
        INNER JOIN expenses AS expense
            ON expense.id = lineItem.expenseId
        WHERE
            (
                :itemMasterId IS NOT NULL
                AND lineItem.itemMasterId = :itemMasterId
            )
            OR
            (
                :itemMasterId IS NULL
                AND (
                    lineItem.itemNameHindi = :itemNameHindi
                    OR LOWER(lineItem.itemNameEnglish) =
                       LOWER(:itemNameEnglish)
                )
            )
        ORDER BY expense.createdAt DESC
        """
    )
    fun getItemPriceHistory(
        itemMasterId: Int?,
        itemNameHindi: String,
        itemNameEnglish: String
    ): Flow<List<ItemPriceHistory>>

    @Query(
        """
        SELECT
            lineItem.itemMasterId AS itemMasterId,
            lineItem.itemNameHindi AS itemNameHindi,
            lineItem.itemNameEnglish AS itemNameEnglish,
            strftime(
                '%Y-%m',
                expense.createdAt / 1000,
                'unixepoch',
                'localtime'
            ) AS monthKey,
            COALESCE(AVG(lineItem.rate), 0.0) AS averageRate,
            COALESCE(MIN(lineItem.rate), 0.0) AS lowestRate,
            COALESCE(MAX(lineItem.rate), 0.0) AS highestRate,
            COUNT(*) AS purchaseCount,
            COALESCE(SUM(lineItem.quantity), 0.0) AS totalQuantity,
            COALESCE(SUM(lineItem.amount), 0.0) AS totalAmount
        FROM expense_line_items AS lineItem
        INNER JOIN expenses AS expense
            ON expense.id = lineItem.expenseId
        WHERE expense.createdAt >= :startTime
        AND expense.createdAt < :endTime
        GROUP BY
            lineItem.itemMasterId,
            lineItem.itemNameHindi,
            lineItem.itemNameEnglish,
            monthKey
        ORDER BY monthKey DESC, lineItem.itemNameHindi ASC
        """
    )
    fun getMonthlyItemRates(
        startTime: Long,
        endTime: Long
    ): Flow<List<MonthlyItemRate>>
}
