package com.bharatbhushan.dailyexpensetracker

import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

@Composable
fun PinLockScreen(
    userId: String,
    onPinAccepted: () -> Unit,
    onLogout: () -> Unit
) {

    val context = LocalContext.current

    val activity = context as? FragmentActivity

    val isCreatingPin = remember(userId) {
        !PinManager.hasPin(
            context = context,
            userId = userId
        )
    }

    val biometricEnabled = remember(userId) {
        isBiometricUnlockEnabled(
            context = context,
            userId = userId
        )
    }

    var pin by remember {
        mutableStateOf("")
    }

    var confirmPin by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    fun openFingerprint() {

        if (activity == null) {
            errorMessage =
                "Fingerprint इस device पर उपलब्ध नहीं है।"
            return
        }

        showFingerprintPrompt(
            activity = activity,

            onSuccess = {
                errorMessage = ""
                onPinAccepted()
            },

            onError = { error ->
                errorMessage = error
            }
        )
    }

    LaunchedEffect(
        userId,
        biometricEnabled,
        isCreatingPin
    ) {

        if (
            biometricEnabled &&
            !isCreatingPin &&
            activity != null
        ) {
            openFingerprint()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(24.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = if (biometricEnabled) {
                Icons.Default.Fingerprint
            } else {
                Icons.Default.Lock
            },
            contentDescription = "App Lock",
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppText(
            text = if (isCreatingPin) {
                "App PIN बनाएँ"
            } else {
                "App Unlock करें"
            },
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        AppText(
            text = if (isCreatingPin) {
                "4 से 6 अंकों का सुरक्षित PIN बनाएँ"
            } else if (biometricEnabled) {
                "Fingerprint या PIN से Daily Expense Tracker खोलें"
            } else {
                "Daily Expense Tracker खोलने के लिए PIN डालें"
            }
        )

        if (
            biometricEnabled &&
            !isCreatingPin
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    openFingerprint()
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null
                )

                AppText("  Fingerprint से Unlock करें")
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = pin,

            onValueChange = { value ->

                if (
                    value.length <= 6 &&
                    value.all { character ->
                        character.isDigit()
                    }
                ) {
                    pin = value
                    errorMessage = ""
                }
            },

            modifier = Modifier.fillMaxWidth(),

            label = {
                AppText(
                    if (isCreatingPin) {
                        "नया PIN"
                    } else {
                        "PIN"
                    }
                )
            },

            visualTransformation =
                PasswordVisualTransformation(),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),

            singleLine = true
        )

        if (isCreatingPin) {

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPin,

                onValueChange = { value ->

                    if (
                        value.length <= 6 &&
                        value.all { character ->
                            character.isDigit()
                        }
                    ) {
                        confirmPin = value
                        errorMessage = ""
                    }
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    AppText("PIN दोबारा डालें")
                },

                visualTransformation =
                    PasswordVisualTransformation(),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),

                singleLine = true
            )
        }

        if (errorMessage.isNotBlank()) {

            Spacer(modifier = Modifier.height(12.dp))

            AppText(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                if (isCreatingPin) {

                    when {

                        !pin.matches(
                            Regex("\\d{4,6}")
                        ) -> {

                            errorMessage =
                                "PIN 4 से 6 अंकों का होना चाहिए।"
                        }

                        pin != confirmPin -> {

                            errorMessage =
                                "दोनों PIN समान नहीं हैं।"
                        }

                        else -> {

                            val saved = PinManager.savePin(
                                context = context,
                                userId = userId,
                                pin = pin
                            )

                            if (saved) {
                                onPinAccepted()
                            } else {
                                errorMessage =
                                    "PIN save नहीं हुआ।"
                            }
                        }
                    }

                } else {

                    val isCorrect = PinManager.verifyPin(
                        context = context,
                        userId = userId,
                        enteredPin = pin
                    )

                    if (isCorrect) {
                        onPinAccepted()
                    } else {
                        errorMessage = "गलत PIN।"
                        pin = ""
                    }
                }
            },

            modifier = Modifier.fillMaxWidth()
        ) {

            AppText(
                text = if (isCreatingPin) {
                    "PIN सेव करें"
                } else {
                    "PIN से Unlock करें"
                }
            )
        }

        if (!isCreatingPin) {

            TextButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppText("दूसरे Account से Login करें")
            }
        }
    }
}

private fun showFingerprintPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {

    val executor =
        ContextCompat.getMainExecutor(activity)

    val biometricPrompt = BiometricPrompt(
        activity,
        executor,

        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()

                onError(
                    "Fingerprint match नहीं हुआ। दोबारा प्रयास करें।"
                )
            }

            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(
                    errorCode,
                    errString
                )

                if (
                    errorCode !=
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                    errorCode !=
                    BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode !=
                    BiometricPrompt.ERROR_CANCELED
                ) {
                    onError(errString.toString())
                }
            }
        }
    )

    val promptInfo =
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Daily Expense Tracker Unlock")
            .setSubtitle(
                "Fingerprint लगाकर app खोलें"
            )
            .setNegativeButtonText("PIN का उपयोग करें")
            .build()

    biometricPrompt.authenticate(promptInfo)
}
