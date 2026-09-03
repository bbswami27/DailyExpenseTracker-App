package com.bharatbhushan.dailyexpensetracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background

@Composable
fun FirebaseAuthScreen(
    onAuthSuccess: () -> Unit,
    onContinueAsGuest: () -> Unit
) {
    val firebaseAuth = remember {
        FirebaseAuth.getInstance()
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var isRegisterMode by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),

        verticalArrangement = Arrangement.Center
    ) {
        AppText(
            text = "घर बजट",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        AppText(
            text = "Daily Expense Tracker",
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        AppText(
            text = if (isRegisterMode) {
                "नया अकाउंट बनाएँ"
            } else {
                "अपने अकाउंट में Login करें"
            },
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it.trim()
                message = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                AppText("Email")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                message = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                AppText("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true
        )

        if (message.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))

            AppText(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val cleanEmail = email.trim()

                when {
                    cleanEmail.isBlank() -> {
                        message = "Email दर्ज करें।"
                    }

                    password.length < 6 -> {
                        message =
                            "Password कम से कम 6 characters का रखें।"
                    }

                    else -> {
                        isLoading = true
                        message = ""

                        if (isRegisterMode) {
                            firebaseAuth
                                .createUserWithEmailAndPassword(
                                    cleanEmail,
                                    password
                                )
                                .addOnCompleteListener { task ->
                                    isLoading = false

                                    if (task.isSuccessful) {
                                        onAuthSuccess()
                                    } else {
                                        message =
                                            task.exception?.localizedMessage
                                                ?: "Registration नहीं हुआ।"
                                    }
                                }
                        } else {
                            firebaseAuth
                                .signInWithEmailAndPassword(
                                    cleanEmail,
                                    password
                                )
                                .addOnCompleteListener { task ->
                                    isLoading = false

                                    if (task.isSuccessful) {
                                        onAuthSuccess()
                                    } else {
                                        message =
                                            task.exception?.localizedMessage
                                                ?: "Login नहीं हुआ।"
                                    }
                                }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            AppText(
                text = when {
                    isLoading -> "कृपया प्रतीक्षा करें..."
                    isRegisterMode -> "Account बनाएँ"
                    else -> "Login"
                }
            )
        }

        TextButton(
            onClick = {
                isRegisterMode = !isRegisterMode
                message = ""
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            AppText(
                if (isRegisterMode) {
                    "पहले से Account है? Login करें"
                } else {
                    "नया Account बनाएँ / Register"
                }
            )
        }

        if (!isRegisterMode) {
            TextButton(
                onClick = {
                    val cleanEmail = email.trim()

                    if (cleanEmail.isBlank()) {
                        message =
                            "पहले Email दर्ज करें।"
                    } else {
                        isLoading = true

                        firebaseAuth
                            .sendPasswordResetEmail(cleanEmail)
                            .addOnCompleteListener { task ->
                                isLoading = false

                                message =
                                    if (task.isSuccessful) {
                                        "Password reset email भेज दी गई है।"
                                    } else {
                                        task.exception?.localizedMessage
                                            ?: "Reset email नहीं भेजी गई।"
                                    }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                AppText("Password भूल गए?")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            OutlinedButton(
                onClick = onContinueAsGuest,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                AppText("Continue without Login")
            }

            AppText(
                "Guest mode में data केवल इसी device पर सुरक्षित रहेगा; cloud sync उपलब्ध नहीं होगा।",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
