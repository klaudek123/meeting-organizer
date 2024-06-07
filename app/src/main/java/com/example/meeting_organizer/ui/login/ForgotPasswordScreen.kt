package com.example.meeting_organizer.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForgotPasswordScreen(
    onPasswordResetClick: (email: String, password: String) -> Unit,
    resetPasswordError: Boolean,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError =
                    it.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
            },
            label = { Text("Email", fontSize = 24.sp) },
            isError = emailError,
            modifier = Modifier.fillMaxWidth()
        )

        if (emailError) {
            Text(
                "Enter a valid email address",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = it.isEmpty() || it.length < 8
            },
            label = { Text("New password", fontSize = 24.sp) },
            isError = passwordError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (passwordError) {
            Text(
                "Password must be at least 8 characters",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = it != password
            },
            label = { Text("Confirm new password", fontSize = 24.sp) },
            isError = confirmPasswordError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (confirmPasswordError) {
            Text(
                "Passwords do not match",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            if (!emailError && !passwordError && !confirmPasswordError) {
                onPasswordResetClick(email, password)
                successMessage = "If the email is valid, your password has been reset."
                errorMessage = ""
            } else {
                errorMessage = "Please fix the errors before proceeding."
                successMessage = ""
            }
        }) {
            Text("Reset Password", fontSize = 24.sp)
        }

        if (successMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(successMessage, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }
        if (resetPasswordError) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Incorrect email address",
                color = Color.Red,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}