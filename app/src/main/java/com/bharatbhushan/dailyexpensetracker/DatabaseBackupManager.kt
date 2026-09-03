package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object DatabaseBackupManager {

    suspend fun createAndShareBackup(
        context: Context,
        userId: String,
        bookId: String,
        database: ExpenseDatabase
    ) {

        withContext(Dispatchers.IO) {

            database.openHelper
                .writableDatabase
                .query("PRAGMA wal_checkpoint(FULL)")
                .close()

            val databaseFile =
                context.getDatabasePath(
                    ExpenseDatabase.userDatabaseName(userId, bookId)
                )

            val backupFile = File(
                context.cacheDir,
                "GharKharch_Backup_${System.currentTimeMillis()}.ghk"
            )

            databaseFile.copyTo(
                target = backupFile,
                overwrite = true
            )

            withContext(Dispatchers.Main) {

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    backupFile
                )

                val intent = Intent(
                    Intent.ACTION_SEND
                ).apply {

                    type = "application/octet-stream"

                    putExtra(
                        Intent.EXTRA_STREAM,
                        uri
                    )

                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                context.startActivity(
                    Intent.createChooser(
                        intent,
                        "Daily Expense Tracker Backup सुरक्षित करें"
                    )
                )
            }
        }
    }

    suspend fun restoreBackup(
        context: Context,
        userId: String,
        bookId: String,
        backupUri: Uri
    ) {

        withContext(Dispatchers.IO) {
            val databaseFile =
                context.getDatabasePath(
                    ExpenseDatabase.userDatabaseName(userId, bookId)
                )

            val temporaryFile = File(
                context.cacheDir,
                "restore_check_${System.currentTimeMillis()}.db"
            )

            try {
                context.contentResolver.openInputStream(backupUri)?.use { input ->
                    temporaryFile.outputStream().use { output -> input.copyTo(output) }
                } ?: error("Backup file नहीं खुली")

                require(temporaryFile.length() > 100L) {
                    "चुनी हुई file खाली या invalid है"
                }

                val checkDatabase = SQLiteDatabase.openDatabase(
                    temporaryFile.path,
                    null,
                    SQLiteDatabase.OPEN_READONLY
                )
                val version = checkDatabase.rawQuery("PRAGMA user_version", null).use { cursor ->
                    if (cursor.moveToFirst()) cursor.getInt(0) else 0
                }
                checkDatabase.close()

                require(version in 1..10) {
                    "यह Daily Expense Tracker backup file नहीं है"
                }

                ExpenseDatabase.closeDatabase()
                databaseFile.parentFile?.mkdirs()
                temporaryFile.copyTo(databaseFile, overwrite = true)
            } finally {
                temporaryFile.delete()
            }

            File(
                databaseFile.path + "-wal"
            ).delete()

            File(
                databaseFile.path + "-shm"
            ).delete()
        }
    }
}
