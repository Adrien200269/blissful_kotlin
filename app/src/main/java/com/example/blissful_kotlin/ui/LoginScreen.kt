package com.blissfulcakes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.blissfulcakes.ui.theme.PinkAccent
import com.blissfulcakes.ui.theme.White
import com.blissfulcakes.ui.theme.BlackShadow
import com.blissfulcakes.ui.theme.TextPrimary

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PinkAccent),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .shadow(8.dp, RoundedCornerShape(16.dp), ambientColor = BlackShadow)
                .background(White, shape = RoundedCornerShape(16.dp))
                .padding(0.dp)
                .widthIn(max = 400.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.h4,
                    color = PinkAccent
                )
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        showError = email.isBlank() || password.isBlank()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = PinkAccent)
                ) {
                    Text("Login", color = TextPrimary)
                }
                if (showError) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Please enter both email and password.", color = MaterialTheme.colors.error)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO: Navigate to Sign Up */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = PinkAccent),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Up", color = White)
                }
            }
        }
    }
}
