package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

data class ImportedBillItem(
    val description: String,
    val quantity: Double,
    val unit: String,
    val rate: Double,
    val amount: Double
)

data class ImportedBillData(
    val billNumber: String,
    val billDate: Long?,
    val shopName: String,
    val items: List<ImportedBillItem>,
    val rawText: String
)

suspend fun scanPrintedBill(
    context: Context,
    storedUri: String
): ImportedBillData = withContext(Dispatchers.IO) {
    val uri = Uri.parse(storedUri)
    val file = File(requireNotNull(uri.path) { "Bill file path missing" })
    require(file.exists()) { "Bill file उपलब्ध नहीं है" }

    val pageResults = if (file.extension.equals("pdf", true)) {
        recognizePdfPages(file)
    } else {
        listOf(recognizeImageInBestOrientation(file))
    }
    parseBillText(pageResults.joinToString("\n"))
}

private suspend fun recognizeImageInBestOrientation(file: File): String {
    val source = requireNotNull(BitmapFactory.decodeFile(file.absolutePath)) {
        "Bill image खोली नहीं जा सकी"
    }
    return try {
        listOf(0, 90, 180, 270).map { degrees ->
            val bitmap = if (degrees == 0) {
                source
            } else {
                Bitmap.createBitmap(
                    source,
                    0,
                    0,
                    source.width,
                    source.height,
                    Matrix().apply { postRotate(degrees.toFloat()) },
                    true
                )
            }
            try {
                recognizeBestText(InputImage.fromBitmap(bitmap, 0))
            } finally {
                if (bitmap !== source) bitmap.recycle()
            }
        }.maxByOrNull(::billTextQuality).orEmpty()
    } finally {
        source.recycle()
    }
}

private fun billTextQuality(text: String): Int {
    val lower = text.lowercase(Locale.ROOT)
    val keywords = listOf(
        "tax invoice", "bill no", "bill date", "article description",
        "qty", "uom", "sell price", "discount", "amount", "total"
    )
    return text.count { it.isLetterOrDigit() } +
            keywords.sumOf { if (lower.contains(it)) 250 else 0 } +
            text.lineSequence().count { it.length >= 8 } * 10
}

private suspend fun recognizeBestText(image: InputImage): String {
    val latin = recognizeWith(
        image,
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    )
    val devanagari = recognizeWith(
        image,
        TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
    )
    val latinScore = latin.count { it.isLetterOrDigit() }
    val devanagariScore = devanagari.count { it.isLetterOrDigit() }
    return if (devanagariScore > latinScore) devanagari else latin
}

private suspend fun recognizeWith(
    image: InputImage,
    recognizer: TextRecognizer
): String = suspendCancellableCoroutine { continuation ->
    recognizer.process(image)
        .addOnSuccessListener { result ->
            recognizer.close()
            if (continuation.isActive) continuation.resume(result.text)
        }
        .addOnFailureListener { error ->
            recognizer.close()
            if (continuation.isActive) continuation.resumeWithException(error)
        }
}

private suspend fun recognizePdfPages(file: File): List<String> {
    val results = mutableListOf<String>()
    val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    PdfRenderer(descriptor).use { renderer ->
        for (index in 0 until minOf(renderer.pageCount, 5)) {
            renderer.openPage(index).use { page ->
                val bitmap = Bitmap.createBitmap(
                    page.width * 2,
                    page.height * 2,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.eraseColor(Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                results += recognizeBestText(InputImage.fromBitmap(bitmap, 0))
                bitmap.recycle()
            }
        }
    }
    return results
}

fun parseBillText(rawText: String): ImportedBillData {
    val lines = rawText.lineSequence()
        .map { it.trim().replace(Regex("\\s+"), " ") }
        .filter { it.isNotBlank() }
        .toList()

    val billNumberRegex = Regex(
        "(?i)\\b(?:invoice|bill|receipt|inv)\\s*(?:no\\.?|number|#|num)\\s*[:#.-]?\\s*([A-Z0-9][A-Z0-9/-]{2,})"
    )
    val billNumber = lines.firstNotNullOfOrNull { line ->
        billNumberRegex.find(line)?.groupValues?.getOrNull(1)
    }.orEmpty()

    val numericDateRegex = Regex("\\b[0-3]?\\d[./-][01]?\\d[./-](?:20\\d{2}|\\d{2})\\b")
    val namedDateRegex = Regex("(?i)\\b[0-3]?\\d[- ](?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*[- ]20\\d{2}\\b")
    val dateLines = lines.sortedByDescending { it.contains(Regex("(?i)bill\\s*date|invoice\\s*date")) }
    val billDate = dateLines.firstNotNullOfOrNull { line ->
        (namedDateRegex.find(line)?.value ?: numericDateRegex.find(line)?.value)?.let(::parseBillDate)
    }

    val header = lines.take((lines.indexOfFirst { it.contains("invoice", true) }.takeIf { it >= 0 } ?: 12) + 1)
    val vendorWords = Regex("(?i)mart|store|retail|trader|supermarket|enterprise|private|limited|pvt|ltd")
    val rejectedVendor = Regex("(?i)invoice|bill|receipt|gstin|tax|date|omnipay|mnipay|upi|payment|cashier|address|contact")
    val vendorCandidates = header.filter { line ->
        line.length in 3..80 && !line.contains(rejectedVendor) &&
                !line.matches(Regex("^[0-9 .:/()-]+$"))
    }
    val shopName = vendorCandidates.firstOrNull { it.contains(vendorWords) }
        ?: vendorCandidates.firstOrNull().orEmpty()

    val items = parseBillItems(lines)

    return ImportedBillData(
        billNumber = billNumber,
        billDate = billDate,
        shopName = shopName,
        items = items,
        rawText = rawText
    )
}

private fun parseBillItems(
    lines: List<String>
): List<ImportedBillItem> {

    val items = mutableListOf<ImportedBillItem>()

    var index = 0

    while (index < lines.size) {

        var recognizedItem: ImportedBillItem? = null
        var usedLines = 1

        // पहले single line check करें,
        // फिर अगली 2–3 OCR lines को जोड़कर check करें
        for (lineCount in 1..3) {

            if (index + lineCount > lines.size) {
                break
            }

            val combinedLine = lines
                .subList(index, index + lineCount)
                .joinToString(" ")
                .replace(Regex("\\s+"), " ")
                .trim()

            val parsed = parseBillItemLine(combinedLine)

            if (parsed != null) {
                recognizedItem = parsed
                usedLines = lineCount
                break
            }
        }

        if (recognizedItem != null) {
            items.add(recognizedItem)
            index += usedLines
        } else {
            index++
        }
    }

    return items.distinctBy {
        "${it.description.lowercase()}|" +
                "${it.quantity}|" +
                "${it.rate}|" +
                "${it.amount}"
    }
}
fun parseBillReviewLine(line: String): ImportedBillItem? {
    val parts = line.split('|').map { it.trim() }
    if (parts.size < 4 || parts[0].isBlank()) return null
    val quantity = parts[1].toDoubleOrNull() ?: return null
    val unit = parts[2].ifBlank { "pcs" }
    val rate = parts[3].toDoubleOrNull() ?: return null
    val amount = parts.getOrNull(4)?.toDoubleOrNull() ?: quantity * rate
    if (quantity <= 0 || rate < 0 || amount < 0) return null
    return ImportedBillItem(parts[0], quantity, unit, rate, amount)
}

private fun parseBillItemLine(line: String): ImportedBillItem? {
    if (line.contains(Regex("(?i)subtotal|grand total|total|discount|cgst|sgst|igst|gst|gstin|hsn|taxable|tax summary|round off|cash|change|payment|omnipay|mnipay|invoice|bill|mobile|cashier|qty.*rate|article description|your total savings"))) {
        return null
    }

    // Common supermarket POS row:
    // Description [article/HSN code] Qty UOM SellPrice Discount Amount
    val posRow = Regex(
        "(?i)^(.+?)\\s+" +

                // Article code या product code
                "(?:\\d{5,13}\\s+){0,2}" +

                // Quantity
                "(\\d+(?:\\.\\d{1,3})?)\\s+" +

                // Unit
                "(EA|PCS?|PC|NOS?|KG|GM?|G|LTR?|L|ML|PACK|PKT)\\s+" +

                // Sell Price
                "(\\d+(?:\\.\\d{1,2})?)\\s+" +

                // Discount
                "(\\d+(?:\\.\\d{1,2})?)\\s+" +

                // Amount
                "(\\d+(?:\\.\\d{1,2})?)" +

                // आखिर में आने वाला optional HSN code
                "(?:\\s+\\d{4,8})?$"
    ).find(line)
    if (posRow != null) {
        val description = posRow.groupValues[1].trim(' ', '-', ':', '|')
            .replace(Regex("^\\d+[.)-]?\\s*"), "")
        val quantity = posRow.groupValues[2].toDoubleOrNull() ?: return null
        val unit = posRow.groupValues[3]
        val rate = posRow.groupValues[4].toDoubleOrNull() ?: return null
        val amount = posRow.groupValues[6].toDoubleOrNull() ?: return null
        if (
            description.length >= 2 &&
            description.any { it.isLetter() } &&
            quantity > 0 &&
            rate >= 0 &&
            amount >= 0
        ) {
            return ImportedBillItem(description, quantity, unit, rate, amount)
        }
    }

    val unitToken = Regex("(?i)\\b(EA|PCS?|PC|NOS?|KG|GM?|G|LTR?|L|ML|PACK|PKT|packet)\\b").find(line)
        ?: return null
    val matches = Regex("(?<![A-Za-z])([0-9]+(?:\\.[0-9]{1,2})?)(?![A-Za-z])")
        .findAll(line).toList()
    if (matches.size < 3) return null

    val amount = matches.last().value.toDoubleOrNull() ?: return null
    val rate = matches[matches.lastIndex - 1].value.toDoubleOrNull() ?: return null
    val quantity = matches[matches.lastIndex - 2].value.toDoubleOrNull() ?: return null
    if (quantity <= 0 || rate < 0 || amount < 0) return null

    val description = line.substring(0, matches[matches.lastIndex - 2].range.first)
        .trim(' ', '-', ':', '|')
        .replace(Regex("^\\d+[.)-]?\\s*"), "")
    if (description.length < 2) return null

    if (unitToken.range.first < matches[matches.lastIndex - 2].range.first) return null
    val unit = unitToken.value

    return ImportedBillItem(description, quantity, unit, rate, amount)
}

private fun parseBillDate(value: String): Long? {
    val formats = listOf("d-MMM-yyyy", "d MMM yyyy", "d/M/yyyy", "d/M/yy")
    val normalized = value.replace('.', '/').trim()
    return formats.firstNotNullOfOrNull { pattern ->
        runCatching {
            SimpleDateFormat(pattern, Locale.US).apply { isLenient = false }.parse(normalized)?.time
        }.getOrNull()
    }
}
