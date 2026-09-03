package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import java.util.UUID

data class BudgetBook(
    val id: String,
    val name: String
)

object BudgetBookManager {
    private const val PREFS = "ghar_budget_books"

    fun getBooks(context: Context, userId: String): List<BudgetBook> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val values = prefs.getStringSet("books_$userId", emptySet()).orEmpty()
        if (values.isEmpty()) {
            val default = BudgetBook(id = "default", name = "मेरा घर")
            saveBooks(context, userId, listOf(default))
            return listOf(default)
        }
        return values.mapNotNull { value ->
            val parts = value.split("|||", limit = 2)
            if (parts.size == 2) BudgetBook(parts[0], parts[1]) else null
        }.sortedBy { it.name }
    }

    fun addBook(context: Context, userId: String, name: String): BudgetBook {
        val cleanName = name.trim().ifBlank { "नया घर बजट" }
        val book = BudgetBook(
            id = UUID.randomUUID().toString().replace("-", "").take(16),
            name = cleanName
        )
        saveBooks(context, userId, getBooks(context, userId) + book)
        selectBook(context, userId, book)
        return book
    }

    fun selectBook(context: Context, userId: String, book: BudgetBook) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString("selected_${userId}", book.id)
            .apply()
    }

    fun selectedBook(context: Context, userId: String): BudgetBook {
        val books = getBooks(context, userId)
        val selectedId = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString("selected_${userId}", null)
        return books.firstOrNull { it.id == selectedId } ?: books.first()
    }

    private fun saveBooks(
        context: Context,
        userId: String,
        books: List<BudgetBook>
    ) {
        val values = books.map { "${it.id}|||${it.name}" }.toSet()
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putStringSet("books_$userId", values)
            .apply()
    }
}
