package com.example.meeting_organizer.ui.register

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun RegisterScreen(
    onRegisterClick: (firstName: String, lastName: String, email: String, phoneNumber: String, password: String) -> Unit,
    activity: ComponentActivity,
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name", fontSize = 24.sp) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name", fontSize = 24.sp) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", fontSize = 24.sp) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number", fontSize = 24.sp) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", fontSize = 24.sp) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password", fontSize = 24.sp) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                IconButton(onClick = {
                    confirmPasswordVisible = !confirmPasswordVisible
                }) {
                    Icon(imageVector = image, contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password")
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            errorMessage = when {
                firstName.isBlank() -> "First name is required"
                lastName.isBlank() -> "Last name is required"
                email.isBlank() -> "Email is required"
                !email.contains("@") || !email.contains(".") -> "Invalid email address"
                phoneNumber.isBlank() -> "Phone Number is required"
                !phoneNumber.all { it.isDigit() } -> "Phone Number must be numeric"
                password.isBlank() || password.length < 8 -> "Password must be at least 8 characters"
                confirmPassword.isBlank() || confirmPassword.length < 8 -> "Confirm Password must be at least 8 characters"
                password != confirmPassword -> "Passwords do not match"
                else -> {
                    onRegisterClick(firstName, lastName, email, phoneNumber, password)
                    ""
                }
            }
        }) {
            Text("Register", fontSize = 24.sp)
        }
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 24.sp)
        }
    }
}