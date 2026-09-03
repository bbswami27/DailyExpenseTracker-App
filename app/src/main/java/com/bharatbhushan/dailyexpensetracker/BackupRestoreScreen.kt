package com.bharatbhushan.dailyexpensetracker

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    database: ExpenseDatabase,
    userId: String,
    bookId: String,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val restoreLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocument()
        ) { uri ->

            if (uri != null) {

                coroutineScope.launch {

                    val result = runCatching {

                        DatabaseBackupManager
                            .restoreBackup(
                                context = context,
                                userId = userId,
                                bookId = bookId,
                                backupUri = uri
                            )
                    }

                    if (result.isSuccess) {

                        Toast.makeText(
                            context,
                            "Backup Restore हो गया। App दोबारा खोलें।",
                            Toast.LENGTH_LONG
                        ).show()

                        delay(1200)

                        android.os.Process.killProcess(
                            android.os.Process.myPid()
                        )

                    } else {

                        Toast.makeText(
                            context,
                            "Restore failed: " +
                                    result.exceptionOrNull()
                                        ?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                title = {
                    AppText(
                        text = "Backup और Restore",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    AppText(
                        text = "Full Database Backup",
                        fontWeight = FontWeight.Bold
                    )

                    AppText(
                        text =
                            "Expenses, Income, Budget, " +
                                    "Shopping Lists, Items और " +
                                    "Categories सुरक्षित होंगे।"
                    )
                }
            }

            Button(
                onClick = {

                    coroutineScope.launch {

                        val result = runCatching {

                            DatabaseBackupManager
                                .createAndShareBackup(
                                    context = context,
                                    userId = userId,
                                    bookId = bookId,
                                    database = database
                                )
                        }

                        if (result.isFailure) {

                            Toast.makeText(
                                context,
                                "Backup failed: " +
                                        result.exceptionOrNull()
                                            ?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                AppText("Backup बनाएँ और Share करें")
            }

            OutlinedButton(
                onClick = {

                    restoreLauncher.launch(
                        arrayOf(
                            "application/octet-stream",
                            "application/x-sqlite3",
                            "*/*"
                        )
                    )
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                AppText("Backup File Restore करें")
            }

            AppText(
                text =
                    "Restore करने पर वर्तमान data backup " +
                            "file के data से replace होगा। Restore " +
                            "से पहले नया backup अवश्य बना लें।"
            )
        }
    }
}
