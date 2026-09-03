package com.bharatbhushan.dailyexpensetracker

import androidx.room.withTransaction

class ExpenseRepository(
    private val database: ExpenseDatabase
) {

    suspend fun saveExpenseWithItems(
        category: String,
        paymentMode: String,
        description: String,
        shopName: String,
        billAttachmentUri: String,
        billNumber: String,
        expenseDate: Long,
        items: List<DraftExpenseItem>
    ) {

        database.withTransaction {

            val totalAmount = items.sumOf {
                it.amount
            }

            val expenseId = database
                .expenseDao()
                .insertExpense(
                    Expense(
                        amount = totalAmount,
                        category = category,
                        paymentMode = paymentMode,
                        description = description,
                        shopName = shopName,
                        billAttachmentUri = billAttachmentUri,
                        billNumber = billNumber,
                        createdAt = expenseDate
                    )
                )
                .toInt()

            val lineItems = items.map { item ->

                ExpenseLineItem(
                    expenseId = expenseId,
                    itemMasterId = item.itemMasterId,
                    itemNameHindi = item.itemNameHindi,
                    itemNameEnglish = item.itemNameEnglish,
                    quantity = item.quantity,
                    unit = item.unit,
                    rate = item.rate,
                    amount = item.amount
                )
            }

            database
                .expenseLineItemDao()
                .insertLineItems(lineItems)
        }
    }
}
