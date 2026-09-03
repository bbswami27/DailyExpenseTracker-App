package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSecurityScreen(
    userId: String,
    userEmail: String,
    onBackupRestore: () -> Unit,
    onBack: () -> Unit
) {

    val context = androidx.compose.ui.platform.LocalContext.current

    var appLockEnabled by remember(userId) {
        mutableStateOf(
            PinManager.isAppLockEnabled(
                context = context,
                userId = userId
            ) && PinManager.hasPin(
                context = context,
                userId = userId
            )
        )
    }

    var biometricEnabled by remember(userId) {
        mutableStateOf(
            isBiometricUnlockEnabled(
                context = context,
                userId = userId
            )
        )
    }

    var currentPin by remember {
        mutableStateOf("")
    }

    var newPin by remember {
        mutableStateOf("")
    }

    var confirmPin by remember {
        mutableStateOf("")
    }

    var message by remember {
        mutableStateOf("")
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
                        text = "Account & Security",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background
                )
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Card(
                onClick = onBackupRestore,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(Icons.Default.Backup, contentDescription = "Backup and Restore")
                    Column {
                        AppText("Backup & Restore", fontWeight = FontWeight.Bold)
                        AppText("Local backup बनाएँ या पुरानी backup restore करें")
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    AppText(
                        text = "Logged-in Account",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    AppText(text = userEmail)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Password,
                            contentDescription = null
                        )

                        AppText(
                            text = "  Password Reset",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = {

                            FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(userEmail)
                                .addOnCompleteListener { task ->

                                    message =
                                        if (task.isSuccessful) {
                                            "Password reset email भेज दी गई है।"
                                        } else {
                                            task.exception?.localizedMessage
                                                ?: "Reset email नहीं भेजी गई।"
                                        }
                                }
                        }
                    ) {
                        AppText("Reset email भेजें")
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )

                        AppText(
                            text = if (appLockEnabled) {
                                "  App Lock चालू है"
                            } else {
                                "  App Lock बंद है"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (appLockEnabled) {

                        PinInputField(
                            value = currentPin,
                            onValueChange = {
                                currentPin = it
                                message = ""
                            },
                            label = "वर्तमान PIN"
                        )
                    }

                    PinInputField(
                        value = newPin,
                        onValueChange = {
                            newPin = it
                            message = ""
                        },
                        label = if (appLockEnabled) {
                            "नया PIN"
                        } else {
                            "नया App Lock PIN"
                        }
                    )

                    PinInputField(
                        value = confirmPin,
                        onValueChange = {
                            confirmPin = it
                            message = ""
                        },
                        label = "नया PIN दोबारा डालें"
                    )

                    Button(
                        onClick = {

                            when {

                                appLockEnabled &&
                                        !PinManager.verifyPin(
                                            context = context,
                                            userId = userId,
                                            enteredPin = currentPin
                                        ) -> {

                                    message = "वर्तमान PIN गलत है।"
                                }

                                !newPin.matches(
                                    Regex("\\d{4,6}")
                                ) -> {

                                    message =
                                        "नया PIN 4 से 6 अंकों का रखें।"
                                }

                                newPin != confirmPin -> {

                                    message =
                                        "दोनों नए PIN समान नहीं हैं।"
                                }

                                else -> {

                                    PinManager.savePin(
                                        context = context,
                                        userId = userId,
                                        pin = newPin
                                    )

                                    appLockEnabled = true
                                    currentPin = ""
                                    newPin = ""
                                    confirmPin = ""

                                    message =
                                        if (appLockEnabled) {
                                            "App Lock PIN save हो गया।"
                                        } else {
                                            "PIN बदल गया।"
                                        }
                                }
                            }
                        },

                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppText(
                            if (appLockEnabled) {
                                "PIN बदलें"
                            } else {
                                "App Lock चालू करें"
                            }
                        )
                    }

                    if (appLockEnabled) {

                        TextButton(
                            onClick = {

                                if (
                                    PinManager.verifyPin(
                                        context = context,
                                        userId = userId,
                                        enteredPin = currentPin
                                    )
                                ) {

                                    PinManager.disableAppLock(
                                        context = context,
                                        userId = userId
                                    )

                                    saveBiometricUnlockEnabled(
                                        context = context,
                                        userId = userId,
                                        enabled = false
                                    )

                                    appLockEnabled = false
                                    biometricEnabled = false
                                    currentPin = ""
                                    newPin = ""
                                    confirmPin = ""

                                    message = "App Lock बंद हो गया।"

                                } else {
                                    message =
                                        "App Lock बंद करने के लिए सही वर्तमान PIN डालें।"
                                }
                            },

                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppText("App Lock बंद करें")
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        AppText(
                            text = "Fingerprint Unlock",
                            fontWeight = FontWeight.Bold
                        )

                        AppText(
                            text = "PIN backup के रूप में रहेगा"
                        )
                    }

                    Switch(
                        checked = biometricEnabled,

                        enabled = appLockEnabled,

                        onCheckedChange = { enabled ->

                            if (!enabled) {

                                biometricEnabled = false

                                saveBiometricUnlockEnabled(
                                    context = context,
                                    userId = userId,
                                    enabled = false
                                )

                                message =
                                    "Fingerprint Unlock बंद हो गया।"

                            } else {

                                val biometricManager =
                                    BiometricManager.from(context)

                                val result =
                                    biometricManager.canAuthenticate(
                                        BiometricManager.Authenticators
                                            .BIOMETRIC_STRONG
                                    )

                                if (
                                    result ==
                                    BiometricManager.BIOMETRIC_SUCCESS
                                ) {

                                    biometricEnabled = true

                                    saveBiometricUnlockEnabled(
                                        context = context,
                                        userId = userId,
                                        enabled = true
                                    )

                                    message =
                                        "Fingerprint Unlock चालू हो गया।"

                                } else {

                                    biometricEnabled = false

                                    message =
                                        "पहले mobile Settings में Fingerprint register करें।"
                                }
                            }
                        }
                    )
                }
            }

            if (message.isNotBlank()) {

                AppText(
                    text = message,
                    color = if (
                        message.contains("गलत") ||
                        message.contains("नहीं")
                    ) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }
    }
}

@Composable
private fun PinInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {

    OutlinedTextField(
        value = value,

        onValueChange = { enteredValue ->

            if (
                enteredValue.length <= 6 &&
                enteredValue.all { it.isDigit() }
            ) {
                onValueChange(enteredValue)
            }
        },

        modifier = Modifier.fillMaxWidth(),

        label = {
            AppText(label)
        },

        visualTransformation =
            PasswordVisualTransformation(),

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ),

        singleLine = true
    )
}

private const val BIOMETRIC_PREFERENCES =
    "ghar_budget_biometric_settings"

fun isBiometricUnlockEnabled(
    context: Context,
    userId: String
): Boolean {

    return context.getSharedPreferences(
        BIOMETRIC_PREFERENCES,
        Context.MODE_PRIVATE
    )
        .getBoolean(
            "biometric_enabled_$userId",
            false
        )
}

fun saveBiometricUnlockEnabled(
    context: Context,
    userId: String,
    enabled: Boolean
) {

    context.getSharedPreferences(
        BIOMETRIC_PREFERENCES,
        Context.MODE_PRIVATE
    )
        .edit()
        .putBoolean(
            "biometric_enabled_$userId",
            enabled
        )
        .apply()
}
