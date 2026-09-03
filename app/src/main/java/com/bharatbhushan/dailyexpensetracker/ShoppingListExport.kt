package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun shareShoppingListAsText(
    context: Context,
    shoppingList: ShoppingList,
    items: List<ShoppingListItem>
) {

        val message = buildString {

        appendLine("🛒 ${shoppingList.name}")
        appendLine("────────────────")

        items.forEachIndexed { index, item ->

            val status =
                if (item.isPurchased) "Purchased" else "Pending"

            appendLine(
                "${index + 1}. $status " +
                        "${item.itemNameHindi} / " +
                        item.itemNameEnglish
            )

            appendLine(
                "   मात्रा: ${item.quantity} ${item.unit}"
            )
            if (item.brand.isNotBlank()) {
                appendLine("   Brand: ${item.brand}")
            }
        }

        appendLine("────────────────")
        appendLine("Daily Expense Tracker Shopping List")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, shoppingList.name)
        putExtra(Intent.EXTRA_TEXT, message)
    }

    context.startActivity(
        Intent.createChooser(
            intent,
            "Shopping List Share करें"
        )
    )
}

fun exportShoppingListAsCsv(
    context: Context,
    shoppingList: ShoppingList,
    items: List<ShoppingListItem>
) {

    val safeName = safeFileName(
        shoppingList.name
    )

    val file = File(
        context.cacheDir,
        "${safeName}_shopping_list.csv"
    )

    val csvContent = buildString {

        appendLine(
            "Sr No,Hindi Item,English Item,Brand,Quantity,Unit,Purchased"
        )

        items.forEachIndexed { index, item ->

            appendLine(
                listOf(
                    (index + 1).toString(),
                    csvValue(item.itemNameHindi),
                    csvValue(item.itemNameEnglish),
                    csvValue(item.brand),
                    item.quantity.toString(),
                    csvValue(item.unit),
                    if (item.isPurchased) "Yes" else "No"
                ).joinToString(",")
            )
        }
    }

    file.writeText(
        text = csvContent,
        charset = Charsets.UTF_8
    )

    shareGeneratedFile(
        context = context,
        file = file,
        mimeType = "text/csv",
        chooserTitle = "CSV Export करें"
    )
}

fun exportShoppingListAsPdf(
    context: Context,
    shoppingList: ShoppingList,
    items: List<ShoppingListItem>
) {

    val safeName = safeFileName(
        shoppingList.name
    )

    val file = File(
        context.cacheDir,
        "${safeName}_shopping_list.pdf"
    )

    val document = PdfDocument()

    val pageWidth = 595
    val pageHeight = 842
    val leftMargin = 36f
    val bottomLimit = 790f

    val titlePaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = 22f
        typeface = Typeface.DEFAULT_BOLD
    }

    val headingPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = 13f
        typeface = Typeface.DEFAULT_BOLD
    }

    val normalPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = 11f
        typeface = Typeface.DEFAULT
    }

    var pageNumber = 1

    var pageInfo = PdfDocument.PageInfo
        .Builder(
            pageWidth,
            pageHeight,
            pageNumber
        )
        .create()

    var page = document.startPage(pageInfo)
    var canvas = page.canvas
    var y = 48f

    fun startNewPage() {

        document.finishPage(page)

        pageNumber += 1

        pageInfo = PdfDocument.PageInfo
            .Builder(
                pageWidth,
                pageHeight,
                pageNumber
            )
            .create()

        page = document.startPage(pageInfo)
        canvas = page.canvas
        y = 48f

        canvas.drawText(
            "Daily Expense Tracker Shopping List",
            leftMargin,
            y,
            headingPaint
        )

        y += 28f
    }

    canvas.drawText(
        shoppingList.name,
        leftMargin,
        y,
        titlePaint
    )

    y += 28f

    canvas.drawText(
        "Daily Expense Tracker Shopping List",
        leftMargin,
        y,
        normalPaint
    )

    y += 30f

    items.forEachIndexed { index, item ->

        if (y > bottomLimit - 55f) {
            startNewPage()
        }

        val status =
            if (item.isPurchased) {
                "✅ खरीदा / Purchased"
            } else {
                "⬜ बाकी / Pending"
            }

        val itemTitle =
            "${index + 1}. $status " +
                    "${item.itemNameHindi} / " +
                    item.itemNameEnglish

        canvas.drawText(
            itemTitle.take(75),
            leftMargin,
            y,
            headingPaint
        )

        y += 18f

        val detail = buildString {
            if (item.brand.isNotBlank()) append("Brand: ${item.brand} • ")
            append("Quantity: ${item.quantity} ${item.unit}")
        }

        canvas.drawText(
            detail.take(85),
            leftMargin + 18f,
            y,
            normalPaint
        )

        y += 25f
    }

     document.finishPage(page)

    FileOutputStream(file).use {
        document.writeTo(it)
    }

    document.close()

    shareGeneratedFile(
        context = context,
        file = file,
        mimeType = "application/pdf",
        chooserTitle = "PDF Export करें"
    )
}

private fun shareGeneratedFile(
    context: Context,
    file: File,
    mimeType: String,
    chooserTitle: String
) {

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    context.startActivity(
        Intent.createChooser(
            intent,
            chooserTitle
        )
    )
}

private fun csvValue(
    value: String
): String {

    val escaped = value.replace(
        "\"",
        "\"\""
    )

    return "\"$escaped\""
}

private fun safeFileName(
    value: String
): String {

    return value
        .replace(
            Regex("[^a-zA-Z0-9\u0900-\u097F_-]"),
            "_"
        )
        .take(50)
        .ifBlank {
            "shopping_list"
        }
}
