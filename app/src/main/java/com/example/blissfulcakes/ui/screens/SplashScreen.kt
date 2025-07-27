package com.example.blissfulcakes.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.blissfulcakes.R
import com.example.blissfulcakes.di.DependencyProvider
import com.example.blissfulcakes.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val authViewModel = remember { DependencyProvider.provideAuthViewModel(context) }
    
    // Animation values
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animation")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_animation"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )
    
    // Handle navigation after splash delay
    LaunchedEffect(Unit) {
        delay(3000) // Show splash for 3 seconds
        
        authViewModel.checkLoginStatus { isLoggedIn ->
            if (isLoggedIn) {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFE5E5),
                        Color(0xFFFFF0F0),
                        Color.White
                    ),
                    radius = 1000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with animation
            Image(
                painter = painterResource(id = R.drawable.blissful_cakes_logo),
                contentDescription = "Blissful Cakes Logo",
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App Name Text
            Text(
                text = "Blissful",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = Color(0xFFE91E63).copy(alpha = alpha)
            )
            
            Text(
                text = "CAKES",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 4.sp,
                color = Color(0xFF4CAF50).copy(alpha = alpha)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Loading indicator
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = Color(0xFFE91E63),
                strokeWidth = 3.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Loading delicious experiences...",
                fontSize = 14.sp,
                color = Color.Gray.copy(alpha = alpha),
                fontStyle = FontStyle.Italic
            )
        }
        
        // Version info at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Made with ❤️ for cake lovers",
                fontSize = 12.sp,
                color = Color.Gray.copy(alpha = alpha)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Version 1.0.0",
                fontSize = 10.sp,
                color = Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}