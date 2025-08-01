package com.adrien.blissfulcake.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.adrien.blissfulcake.R
import com.adrien.blissfulcake.di.DependencyProvider
import com.adrien.blissfulcake.ui.viewmodel.AuthState
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel = remember { DependencyProvider.provideAuthViewModel(context) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    // Enhanced error states for individual fields
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()

    // SharedPreferences for Remember Me functionality
    val sharedPreferences = context.getSharedPreferences("BlissfulCakeUser", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // Load saved credentials if available
    LaunchedEffect(Unit) {
        val savedEmail = sharedPreferences.getString("saved_email", "") ?: ""
        val savedPassword = sharedPreferences.getString("saved_password", "") ?: ""
        val wasRemembered = sharedPreferences.getBoolean("remember_me", false)

        if (savedEmail.isNotEmpty() && wasRemembered) {
            email = savedEmail
            password = savedPassword
            rememberMe = true
        }
    }

    // Enhanced email validation
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validation function
    fun validateInputs(): Boolean {
        var isValid = true

        if (email.trim().isEmpty()) {
            emailError = "Email is required"
            isValid = false
        } else if (!isValidEmail(email.trim())) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = null
        }

        if (password.isEmpty()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordError = null
        }

        return isValid
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                // Save credentials if remember me is checked
                if (rememberMe) {
                    editor.putString("saved_email", email.trim())
                    editor.putString("saved_password", password)
                    editor.putBoolean("remember_me", true)
                    editor.apply()
                } else {
                    // Clear saved credentials if not remembering
                    editor.clear()
                    editor.apply()
                }

                Log.d("LoginScreen", "Login successful, navigating to home")
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                // Show error in snackbar
                coroutineScope.launch {
                    snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                }
                Log.e("LoginScreen", "Login error: ${(authState as AuthState.Error).message}")
            }
            is AuthState.Loading -> {
                Log.d("LoginScreen", "Login in progress...")
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFFE91E63),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn {
            item {
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
                        .padding(paddingValues)
                ) {
                    // Animated background particles
                    FloatingParticles()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Logo animation
                        val scaleAnim = rememberInfiniteTransition().animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1.1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ), label = "scaleAnim"
                        )

                        val alphaAnim = rememberInfiniteTransition().animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1800, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ), label = "alphaAnim"
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
                            text = "Sweet moments, delivered to you",
                            fontSize = 18.sp,
                            color = Color(0xFF9C27B0),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        // Enhanced Login Form
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
                                    text = "Welcome Back",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE91E63),
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Sign in to continue your sweet journey",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Enhanced Email Field with validation
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        emailError = null // Clear error when user types
                                    },
                                    label = { Text("Email") },
                                    placeholder = { Text("abc@gmail.com") },
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
                                        unfocusedBorderColor = Color(0xFFE0E0E0),
                                        errorBorderColor = Color(0xFFDC3545),
                                        errorLabelColor = Color(0xFFDC3545),
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    isError = emailError != null,
                                    supportingText = emailError?.let {
                                        { Text(it, color = Color(0xFFDC3545)) }
                                    },
                                    singleLine = true
                                )

                                // Enhanced Password Field with validation
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = {
                                        password = it
                                        passwordError = null // Clear error when user types
                                    },
                                    label = { Text("Password") },
                                    placeholder = { Text("*") },
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
                                        imeAction = ImeAction.Done
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFE91E63),
                                        focusedLabelColor = Color(0xFFE91E63),
                                        unfocusedBorderColor = Color(0xFFE0E0E0),
                                        errorBorderColor = Color(0xFFDC3545),
                                        errorLabelColor = Color(0xFFDC3545),
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    isError = passwordError != null,
                                    supportingText = passwordError?.let {
                                        { Text(it, color = Color(0xFFDC3545)) }
                                    },
                                    singleLine = true
                                )

                                // Remember Me and Forgot Password Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = rememberMe,
                                            onCheckedChange = { rememberMe = it },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Color(0xFFE91E63),
                                                checkmarkColor = Color.White,
                                                uncheckedColor = Color(0xFFE0E0E0)
                                            )
                                        )
                                        Text(
                                            "Remember me",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                    }

                                    TextButton(
                                        onClick = { navController.navigate("forgot_password") }
                                    ) {
                                        Text(
                                            text = "Forgot Password?",
                                            color = Color(0xFFE91E63),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Enhanced Login Button with validation
                                Button(
                                    onClick = {
                                        if (validateInputs()) {
                                            // Save credentials if remember me is checked
                                            if (rememberMe) {
                                                editor.putString("saved_email", email.trim())
                                                editor.putString("saved_password", password)
                                                editor.putBoolean("remember_me", true)
                                                editor.apply()
                                            } else {
                                                // Clear saved credentials if not remembering
                                                editor.clear()
                                                editor.apply()
                                            }

                                            Log.d("LoginScreen", "Login button clicked")
                                            Log.d("LoginScreen", "Email: $email, Password length: ${password.length}")
                                            viewModel.login(email.trim(), password)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                                    enabled = authState !is AuthState.Loading,
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 4.dp,
                                        pressedElevation = 8.dp
                                    )
                                ) {
                                    if (authState is AuthState.Loading) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            "Signing In...",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    } else {
                                        Text(
                                            "Sign In",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Register Link
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
                                    text = "Don't have an account? ",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                                TextButton(
                                    onClick = { navController.navigate("register") },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE91E63))
                                ) {
                                    Text(
                                        text = "Sign Up",
                                        color = Color(0xFFE91E63),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingParticles() {
    val particles = remember { List(20) { Random.nextFloat() } }
    val animation = rememberInfiniteTransition(label = "particleAnimation")

    val alpha by animation.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alphaAnimation"
    )

    val rotation by animation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        ), label = "rotationAnimation"
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
