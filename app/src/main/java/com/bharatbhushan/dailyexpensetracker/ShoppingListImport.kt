package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ShoppingImportResult(
    val storedUri: String,
    val suggestedItems: List<String>
)

data class ImportedShoppingItem(
    val itemName: String,
    val brand: String,
    val quantity: Double,
    val unit: String
)

suspend fun importShoppingDocument(
    context: Context,
    sourceUri: Uri
): ShoppingImportResult = withContext(Dispatchers.IO) {
    val mimeType = context.contentResolver.getType(sourceUri).orEmpty()
    val extension = if (mimeType == "application/pdf") "pdf" else "jpg"
    val directory = File(context.filesDir, "shopping_attachments").apply { mkdirs() }
    val storedFile = File(directory, "shopping_${System.currentTimeMillis()}.$extension")

    context.contentResolver.openInputStream(sourceUri).use { input ->
        requireNotNull(input) { "Selected file could not be opened" }
        FileOutputStream(storedFile).use { output -> input.copyTo(output) }
    }

    val rawLines = if (extension == "pdf") {
        recognizePdf(storedFile)
    } else {
        recognizeBitmap(InputImage.fromFilePath(context, Uri.fromFile(storedFile)))
    }

    ShoppingImportResult(
        storedUri = Uri.fromFile(storedFile).toString(),
        suggestedItems = cleanShoppingLines(rawLines)
    )
}

private suspend fun recognizePdf(file: File): List<String> {
    val lines = mutableListOf<String>()
    val descriptor = android.os.ParcelFileDescriptor.open(
        file,
        android.os.ParcelFileDescriptor.MODE_READ_ONLY
    )
    PdfRenderer(descriptor).use { renderer ->
        val pageLimit = minOf(renderer.pageCount, 10)
        for (index in 0 until pageLimit) {
            renderer.openPage(index).use { page ->
                val scale = 2
                val bitmap = Bitmap.createBitmap(
                    page.width * scale,
                    page.height * scale,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.eraseColor(Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                lines += recognizeBitmap(InputImage.fromBitmap(bitmap, 0))
                bitmap.recycle()
            }
        }
    }
    return lines
}

private suspend fun recognizeBitmap(image: InputImage): List<String> =
    suspendCancellableCoroutine { continuation ->
        val recognizer = TextRecognition.getClient(
            DevanagariTextRecognizerOptions.Builder().build()
        )
        recognizer.process(image)
            .addOnSuccessListener { result ->
                val lines = result.textBlocks.flatMap { block ->
                    block.lines.map { it.text }
                }
                recognizer.close()
                if (continuation.isActive) continuation.resume(lines)
            }
            .addOnFailureListener { error ->
                recognizer.close()
                if (continuation.isActive) continuation.resumeWithException(error)
            }
    }

private fun cleanShoppingLines(lines: List<String>): List<String> =
    lines.asSequence()
        .map { it.trim().replace(Regex("^[•*\\-–—]+\\s*"), "") }
        .map { it.replace(Regex("\\s+"), " ") }
        .filter { it.length in 2..80 }
        .filterNot { it.matches(Regex("^(₹|[$€£¥]|C[$]|A[$]|S[$]|CN¥|द.إ|ر.س|रु)?\\s*[0-9.,]+$")) }
        .distinctBy { it.lowercase() }
        .take(100)
        .toList()

fun matchShoppingItems(
    lines: List<String>,
    masterItems: List<ItemMaster>
): List<ImportedShoppingItem> = lines.mapNotNull { line ->
    val quantityMatch = Regex(
        "(?i)([0-9]+(?:\\.[0-9]+)?)\\s*(kg|kgs|g|gm|gram|l|ltr|litre|ml|pcs|pc|piece|pieces|pack|packet|dozen|किलो|किग्रा|ग्राम|लीटर|मिली|पीस|पैकेट)\\b"
    ).findAll(line).lastOrNull()

    val quantity = quantityMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 1.0
    val unit = quantityMatch?.groupValues?.get(2) ?: "pcs"
    val withoutQuantity = quantityMatch?.let { line.removeRange(it.range) }?.trim() ?: line.trim()
    if (withoutQuantity.isBlank()) return@mapNotNull null

    val matched = findBestItemMatch(withoutQuantity, masterItems)
    val itemName = matched?.nameHindi ?: withoutQuantity
    val brand = if (matched == null) {
        ""
    } else {
        removeMasterWords(withoutQuantity, matched).trim(' ', '-', ':', ',', '(', ')')
    }

    ImportedShoppingItem(
        itemName = itemName,
        brand = brand,
        quantity = quantity,
        unit = normalizeUnit(unit)
    )
}

fun findBestItemMatch(
    text: String,
    masterItems: List<ItemMaster>
): ItemMaster? {
    val normalizedText = normalizeForMatch(text)
    return masterItems.mapNotNull { item ->
        val candidates = buildList {
            add(item.nameHindi)
            add(item.nameEnglish)
            addAll(item.searchAliases.split(',', ' '))
        }.map(::normalizeForMatch).filter { it.length >= 2 }
        val best = candidates.filter { normalizedText.contains(it) }.maxOfOrNull { it.length }
        if (best == null) null else item to best
    }.maxByOrNull { it.second }?.first
}

fun parseReviewedShoppingLine(line: String): ImportedShoppingItem? {
    val parts = line.split('|').map { it.trim() }
    if (parts.isEmpty() || parts[0].isBlank()) return null
    val quantity = parts.getOrNull(2)?.toDoubleOrNull() ?: 1.0
    return ImportedShoppingItem(
        itemName = parts[0],
        brand = parts.getOrNull(1).orEmpty(),
        quantity = quantity.coerceAtLeast(0.01),
        unit = parts.getOrNull(3).orEmpty().ifBlank { "pcs" }
    )
}

private fun removeMasterWords(text: String, item: ItemMaster): String {
    var result = text
    listOf(item.nameHindi, item.nameEnglish).forEach { name ->
        result = result.replace(name, "", ignoreCase = true)
    }
    return result
}

private fun normalizeForMatch(value: String): String = value
    .lowercase()
    .replace(Regex("[^\\p{L}\\p{N}]+"), " ")
    .trim()

private fun normalizeUnit(unit: String): String = when (unit.lowercase()) {
    "kgs", "किलो", "किग्रा" -> "kg"
    "gm", "gram", "ग्राम" -> "g"
    "ltr", "litre", "लीटर" -> "L"
    "मिली" -> "ml"
    "pc", "piece", "pieces", "पीस" -> "pcs"
    "pack", "पैकेट" -> "packet"
    else -> unit
}
