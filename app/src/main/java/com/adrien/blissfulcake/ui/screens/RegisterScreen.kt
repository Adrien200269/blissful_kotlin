package com.adrien.blissfulcake.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.adrien.blissfulcake.di.DependencyProvider
import com.adrien.blissfulcake.ui.viewmodel.AuthState
import com.adrien.blissfulcake.ui.viewmodel.AuthViewModel
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.adrien.blissfulcake.R
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel = remember { DependencyProvider.provideAuthViewModel(context) }
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    val authState by viewModel.authState.collectAsState()
    
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Log.d("RegisterScreen", "Registration successful, navigating to home")
                navController.navigate("home") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                Log.e("RegisterScreen", "Registration error: ${(authState as AuthState.Error).message}")
            }
            is AuthState.Loading -> {
                Log.d("RegisterScreen", "Registration in progress...")
            }
            else -> {}
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF8F8),
                        Color(0xFFFFE8E8),
                        Color(0xFFFFF0F0)
                    )
                )
            )
    ) {
        // Animated background particles
        FloatingParticles()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Logo/Title
            val scaleAnim = rememberInfiniteTransition().animateFloat(
                initialValue = 0.8f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            
            val alphaAnim = rememberInfiniteTransition().animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            
            Image(
                painter = painterResource(id = R.drawable.blissful_logo),
                contentDescription = stringResource(id = R.string.blissful_logo_desc),
                modifier = Modifier
                    .height(140.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Create your account",
                fontSize = 18.sp,
                color = Color(0xFF9C27B0),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Register Form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE91E63),
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Join us for the sweetest experience",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Name Field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Person, 
                                contentDescription = null,
                                tint = Color(0xFFE91E63)
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE91E63),
                            focusedLabelColor = Color(0xFFE91E63),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Email, 
                                contentDescription = null,
                                tint = Color(0xFFE91E63)
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE91E63),
                            focusedLabelColor = Color(0xFFE91E63),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Phone Field
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number (Optional)") },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Phone, 
                                contentDescription = null,
                                tint = Color(0xFFE91E63)
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE91E63),
                            focusedLabelColor = Color(0xFFE91E63),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Lock, 
                                contentDescription = null,
                                tint = Color(0xFFE91E63)
                            ) 
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFFE91E63)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE91E63),
                            focusedLabelColor = Color(0xFFE91E63),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    // Confirm Password Field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = { 
                            Icon(
                                Icons.Default.Lock, 
                                contentDescription = null,
                                tint = Color(0xFFE91E63)
                            ) 
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFFE91E63)
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE91E63),
                            focusedLabelColor = Color(0xFFE91E63),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Test Firebase Connection Button
                    OutlinedButton(
                        onClick = { 
                            Log.d("RegisterScreen", "Testing Firebase connection...")
                            // Test Firebase connection
                            try {
                                val auth = FirebaseAuth.getInstance()
                                val firestore = FirebaseFirestore.getInstance()
                                Log.d("RegisterScreen", "Firebase Auth: ${auth.app.name}")
                                Log.d("RegisterScreen", "Firebase Firestore: ${firestore.app.name}")
                                
                                // Test Firestore write permissions
                                firestore.collection("test").document("register_test")
                                    .set(mapOf(
                                        "test" to "connection", 
                                        "timestamp" to System.currentTimeMillis(),
                                        "email" to email,
                                        "name" to name
                                    ))
                                    .addOnSuccessListener {
                                        Log.d("RegisterScreen", "Firebase connection and write test successful")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("RegisterScreen", "Firebase connection or write test failed: ${e.message}")
                                    }
                            } catch (e: Exception) {
                                Log.e("RegisterScreen", "Firebase test error: ${e.message}", e)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE91E63)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Test Firebase Connection & Write", fontSize = 14.sp)
                    }
                    
                    // Manual Test User Creation Button
                    OutlinedButton(
                        onClick = { 
                            Log.d("RegisterScreen", "Creating test user document manually...")
                            try {
                                val firestore = FirebaseFirestore.getInstance()
                                val testUser = mapOf(
                                    "id" to 12345,
                                    "email" to "test@example.com",
                                    "password" to "",
                                    "name" to "Test User",
                                    "phone" to ""
                                )
                                
                                firestore.collection("users").document("test_user_manual")
                                    .set(testUser)
                                    .addOnSuccessListener {
                                        Log.d("RegisterScreen", "Manual test user document created successfully")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("RegisterScreen", "Manual test user creation failed: ${e.message}")
                                    }
                            } catch (e: Exception) {
                                Log.e("RegisterScreen", "Manual test user creation error: ${e.message}", e)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF9C27B0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create Test User Document", fontSize = 14.sp)
                    }
                    
                    // Register Button
                    Button(
                        onClick = { 
                            Log.d("RegisterScreen", "Register button clicked")
                            Log.d("RegisterScreen", "Name: $name, Email: $email, Password length: ${password.length}")
                            if (password == confirmPassword) {
                                Log.d("RegisterScreen", "Passwords match, calling register")
                                viewModel.register(name, email, password, phone)
                            } else {
                                Log.e("RegisterScreen", "Passwords do not match")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                        enabled = name.isNotEmpty() && email.isNotEmpty() && 
                                password.isNotEmpty() && confirmPassword.isNotEmpty() && 
                                password == confirmPassword && authState !is AuthState.Loading,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "Create Account", 
                                fontSize = 18.sp, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Error Message
                    if (authState is AuthState.Error) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = (authState as AuthState.Error).message,
                                color = Color(0xFFD32F2F),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    
                    if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Passwords do not match",
                                color = Color(0xFFD32F2F),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Login Link
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    TextButton(
                        onClick = { navController.navigate("login") },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE91E63))
                    ) {
                        Text(
                            text = "Sign In",
                            color = Color(0xFFE91E63),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FloatingParticles() {
    val particles = remember { List(20) { Random.nextFloat() } }
    val animation = rememberInfiniteTransition()
    
    val alpha by animation.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val rotation by animation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEachIndexed { index, _ ->
            val x = size.width * Random.nextFloat()
            val y = size.height * Random.nextFloat()
            val radius = 2f + Random.nextFloat() * 3f
            
            rotate(degrees = rotation) {
                drawCircle(
                    color = Color(0xFFE91E63).copy(alpha = alpha),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }
    }
} 