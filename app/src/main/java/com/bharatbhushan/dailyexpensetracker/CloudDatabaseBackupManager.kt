package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

object CloudDatabaseBackupManager {

    suspend fun uploadBillAttachment(
        userId: String,
        bookId: String,
        localUri: String
    ): String = withContext(Dispatchers.IO) {
        if (localUri.isBlank() || localUri.startsWith("http")) {
            return@withContext localUri
        }

        runCatching {
            val reference = FirebaseStorage.getInstance()
                .reference
                .child("user-backups")
                .child(userId)
                .child(bookId)
                .child("bills")
                .child("bill_${System.currentTimeMillis()}")
            Tasks.await(
                reference.putFile(Uri.parse(localUri)),
                30,
                TimeUnit.SECONDS
            )
            Tasks.await(
                reference.downloadUrl,
                15,
                TimeUnit.SECONDS
            ).toString()
        }.getOrDefault(localUri)
    }

    suspend fun upload(
        context: Context,
        userId: String,
        bookId: String,
        database: ExpenseDatabase
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            database.openHelper.writableDatabase
                .query("PRAGMA wal_checkpoint(FULL)")
                .close()

            val source = context.getDatabasePath(
                ExpenseDatabase.userDatabaseName(userId, bookId)
            )
            check(source.exists()) { "Local database नहीं मिली" }

            val snapshot = File(context.cacheDir, "ghar_budget_cloud_backup.db")
            source.copyTo(snapshot, overwrite = true)

            val reference = FirebaseStorage.getInstance()
                .reference
                .child("user-backups")
                .child(userId)
                .child(bookId)
                .child("ghar_budget.db")

            Tasks.await(
                reference.putFile(Uri.fromFile(snapshot)),
                30,
                TimeUnit.SECONDS
            )
            snapshot.delete()
            Unit
        }
    }

    suspend fun restoreIfLocalMissing(
        context: Context,
        userId: String,
        bookId: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val target = context.getDatabasePath(
                ExpenseDatabase.userDatabaseName(userId, bookId)
            )
            if (target.exists()) return@runCatching false

            target.parentFile?.mkdirs()
            val download = File(context.cacheDir, "ghar_budget_cloud_restore.db")
            val reference = FirebaseStorage.getInstance()
                .reference
                .child("user-backups")
                .child(userId)
                .child(bookId)
                .child("ghar_budget.db")

            try {
                Tasks.await(
                    reference.getFile(download),
                    30,
                    TimeUnit.SECONDS
                )
            } catch (_: Exception) {
                download.delete()
                return@runCatching false
            }

            download.copyTo(target, overwrite = true)
            download.delete()
            true
        }
    }
}
