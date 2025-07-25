package com.blissfulcakes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blissfulcakes.ui.theme.PinkAccent
import com.blissfulcakes.ui.theme.White
import com.blissfulcakes.ui.theme.TextPrimary

@Composable
fun LandingPageScreen(onLoginClick: () -> Unit, onSignUpClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PinkAccent),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .background(White, shape = RoundedCornerShape(24.dp))
                .padding(32.dp)
                .widthIn(max = 400.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo), // You need to add logo to res/drawable as logo.png
                contentDescription = "Blissful Cakes Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )
            // Welcome message
            Text(
                text = "Welcome to Blissful Cakes!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PinkAccent
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Delicious cakes and cupcakes delivered fresh to your door.",
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            // Navigation buttons
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = PinkAccent)
            ) {
                Text("Login", color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSignUpClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = PinkAccent)
            ) {
                Text("Sign Up", color = TextPrimary)
            }
        }
    }
}

