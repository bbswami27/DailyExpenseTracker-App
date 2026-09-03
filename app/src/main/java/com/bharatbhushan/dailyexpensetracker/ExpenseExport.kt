package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val EXPORT_PAGE_WIDTH = 1240
private const val EXPORT_PAGE_HEIGHT = 1754

fun exportBillsAsPdf(
    context: Context,
    bills: List<ExpenseWithItems>,
    filePrefix: String,
    title: String
) {
    val lines = buildExpenseExportLines(bills, title)
    val file = exportFile(context, filePrefix, "pdf")
    val document = PdfDocument()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 26f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }
    var pageNumber = 1
    var page = document.startPage(
        PdfDocument.PageInfo.Builder(EXPORT_PAGE_WIDTH, EXPORT_PAGE_HEIGHT, pageNumber).create()
    )
    var y = 70f
    lines.forEach { line ->
        if (y > EXPORT_PAGE_HEIGHT - 70) {
            document.finishPage(page)
            pageNumber++
            page = document.startPage(
                PdfDocument.PageInfo.Builder(EXPORT_PAGE_WIDTH, EXPORT_PAGE_HEIGHT, pageNumber).create()
            )
            y = 70f
        }
        page.canvas.drawText(line.take(95), 55f, y, paint)
        y += 38f
    }
    document.finishPage(page)
    FileOutputStream(file).use(document::writeTo)
    document.close()
    shareFiles(context, arrayListOf(file), "application/pdf", "PDF Export")
}

fun exportBillsAsExcel(
    context: Context,
    bills: List<ExpenseWithItems>,
    filePrefix: String
) {
    val file = exportFile(context, filePrefix, "xls")
    val rows = buildString {
        bills.forEach { bill ->
            val expense = bill.expense
            if (bill.items.isEmpty()) {
                appendExcelRow(
                    listOf(
                        expense.id, formatExportDate(expense.createdAt), expense.billNumber,
                        expense.shopName, expense.category, expense.paymentMode,
                        "", "", "", "", "", expense.amount, expense.description
                    )
                )
            } else {
                bill.items.forEach { item ->
                    appendExcelRow(
                        listOf(
                            expense.id, formatExportDate(expense.createdAt), expense.billNumber,
                            expense.shopName, expense.category, expense.paymentMode,
                            item.itemNameHindi, item.itemNameEnglish, item.quantity,
                            item.unit, item.rate, item.amount, expense.description
                        )
                    )
                }
            }
        }
    }
    val xml = """<?xml version="1.0"?>
        <Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
         xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
        <Worksheet ss:Name="Expenses"><Table>
        ${excelHeaderRow()}
        $rows
        </Table></Worksheet></Workbook>
    """.trimIndent()
    file.writeText(xml, Charsets.UTF_8)
    shareFiles(context, arrayListOf(file), "application/vnd.ms-excel", "Excel Export")
}

fun exportBillsAsJpg(
    context: Context,
    bills: List<ExpenseWithItems>,
    filePrefix: String,
    title: String
) {
    val lines = buildExpenseExportLines(bills, title)
    val linesPerPage = 39
    val files = arrayListOf<File>()
    lines.chunked(linesPerPage).forEachIndexed { index, pageLines ->
        val bitmap = Bitmap.createBitmap(EXPORT_PAGE_WIDTH, EXPORT_PAGE_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        var y = 70f
        pageLines.forEach { line ->
            canvas.drawText(line.take(88), 55f, y, paint)
            y += 41f
        }
        val file = exportFile(context, "${filePrefix}_page_${index + 1}", "jpg")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 94, it) }
        bitmap.recycle()
        files += file
    }
    shareFiles(context, files, "image/jpeg", "JPG Export")
}

fun exportPriceComparisonAsPdf(context: Context, records: List<MonthlyItemRate>) {
    exportTextLinesAsPdf(context, priceComparisonLines(records), "item_price_comparison")
}

fun exportPriceComparisonAsJpg(context: Context, records: List<MonthlyItemRate>) {
    exportTextLinesAsJpg(context, priceComparisonLines(records), "item_price_comparison")
}

fun exportPriceComparisonAsExcel(context: Context, records: List<MonthlyItemRate>) {
    val file = exportFile(context, "item_price_comparison", "xls")
    val body = buildString {
        appendExcelRow(listOf("Item Hindi", "Item English", "Month", "Average Rate", "Lowest Rate", "Highest Rate", "Purchases", "Total Qty", "Total Amount"))
        records.sortedWith(compareBy<MonthlyItemRate> { it.itemNameHindi }.thenBy { it.monthKey })
            .forEach { appendExcelRow(listOf(it.itemNameHindi, it.itemNameEnglish, it.monthKey, it.averageRate, it.lowestRate, it.highestRate, it.purchaseCount, it.totalQuantity, it.totalAmount)) }
    }
    file.writeText("""<?xml version="1.0"?><Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet" xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet"><Worksheet ss:Name="Price Comparison"><Table>$body</Table></Worksheet></Workbook>""", Charsets.UTF_8)
    shareFiles(context, arrayListOf(file), "application/vnd.ms-excel", "Price Comparison Excel")
}

private fun priceComparisonLines(records: List<MonthlyItemRate>): List<String> = buildList {
    add("Items Price Comparison - Last Six Months")
    records.groupBy { it.itemNameHindi to it.itemNameEnglish }.toSortedMap(compareBy { it.first }).forEach { (name, rows) ->
        add("")
        add("${name.first} / ${name.second}")
        rows.sortedBy { it.monthKey }.forEach {
            add("${it.monthKey} | Avg ${formatAmount(it.averageRate)} | Low ${formatAmount(it.lowestRate)} | High ${formatAmount(it.highestRate)} | ${it.purchaseCount} purchases")
        }
    }
}

private fun exportTextLinesAsPdf(context: Context, lines: List<String>, prefix: String) {
    val file = exportFile(context, prefix, "pdf")
    val document = PdfDocument()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; textSize = 24f }
    var number = 1
    var page = document.startPage(PdfDocument.PageInfo.Builder(EXPORT_PAGE_WIDTH, EXPORT_PAGE_HEIGHT, number).create())
    var y = 65f
    lines.forEach { line ->
        if (y > EXPORT_PAGE_HEIGHT - 60) { document.finishPage(page); number++; page = document.startPage(PdfDocument.PageInfo.Builder(EXPORT_PAGE_WIDTH, EXPORT_PAGE_HEIGHT, number).create()); y = 65f }
        page.canvas.drawText(line.take(100), 45f, y, paint); y += 36f
    }
    document.finishPage(page); FileOutputStream(file).use(document::writeTo); document.close()
    shareFiles(context, arrayListOf(file), "application/pdf", "Price Comparison PDF")
}

private fun exportTextLinesAsJpg(context: Context, lines: List<String>, prefix: String) {
    val files = arrayListOf<File>()
    lines.chunked(42).forEachIndexed { index, pageLines ->
        val bitmap = Bitmap.createBitmap(EXPORT_PAGE_WIDTH, EXPORT_PAGE_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap).apply { drawColor(Color.WHITE) }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; textSize = 24f }
        var y = 65f; pageLines.forEach { canvas.drawText(it.take(100), 45f, y, paint); y += 36f }
        val file = exportFile(context, "${prefix}_${index + 1}", "jpg")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 94, it) }; bitmap.recycle(); files += file
    }
    shareFiles(context, files, "image/jpeg", "Price Comparison JPG")
}

private fun buildExpenseExportLines(
    bills: List<ExpenseWithItems>,
    title: String
): List<String> = buildList {
    add(title)
    add("Bills: ${bills.size}   Total: ${formatAmount(bills.sumOf { it.expense.amount })}")
    add("Generated: ${formatExportDate(System.currentTimeMillis())}")
    add("")
    bills.forEachIndexed { index, bill ->
        val expense = bill.expense
        add("${index + 1}. Bill ${expense.billNumber.ifBlank { "#${expense.id}" }} | ${formatExportDate(expense.createdAt)}")
        add("Shop: ${expense.shopName.ifBlank { "-" }} | Category: ${expense.category}")
        add("Payment: ${expense.paymentMode} | Bill Total: ${formatAmount(expense.amount)}")
        if (expense.description.isNotBlank()) add("Note: ${expense.description}")
        if (bill.items.isEmpty()) {
            add("Items: Details unavailable")
        } else {
            add("Items:")
            bill.items.forEach { item ->
                add("- ${item.itemNameHindi} / ${item.itemNameEnglish} | ${item.quantity} ${item.unit} x ${formatAmount(item.rate)} = ${formatAmount(item.amount)}")
            }
        }
        add("------------------------------------------------------------")
    }
}

private fun StringBuilder.appendExcelRow(values: List<Any?>) {
    append("<Row>")
    values.forEach { value ->
        val type = if (value is Number) "Number" else "String"
        append("<Cell><Data ss:Type=\"").append(type).append("\">")
        append(xmlEscape(value?.toString().orEmpty()))
        append("</Data></Cell>")
    }
    append("</Row>\n")
}

private fun excelHeaderRow(): String = buildString {
    appendExcelRow(
        listOf(
            "Expense ID", "Date", "Bill Number", "Shop/Vendor", "Category",
            "Payment Mode", "Item Hindi", "Item English", "Quantity", "Unit",
            "Rate", "Amount", "Description"
        )
    )
}

private fun xmlEscape(value: String): String = value
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")

private fun exportFile(context: Context, prefix: String, extension: String): File {
    val directory = File(context.cacheDir, "exports").apply { mkdirs() }
    return File(directory, "${prefix}_${System.currentTimeMillis()}.$extension")
}

private fun formatExportDate(time: Long): String =
    SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault()).format(Date(time))

private fun shareFiles(
    context: Context,
    files: ArrayList<File>,
    mimeType: String,
    chooserTitle: String
) {
    val uris = ArrayList(files.map { file ->
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    })
    val intent = if (uris.size == 1) {
        Intent(Intent.ACTION_SEND).apply { putExtra(Intent.EXTRA_STREAM, uris.first()) }
    } else {
        Intent(Intent.ACTION_SEND_MULTIPLE).apply { putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris) }
    }.apply {
        type = mimeType
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, chooserTitle))
}
