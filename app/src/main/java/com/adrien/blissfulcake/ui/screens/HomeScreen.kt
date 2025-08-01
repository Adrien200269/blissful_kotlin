package com.adrien.blissfulcake.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.adrien.blissfulcake.data.model.Cake
import com.adrien.blissfulcake.di.DependencyProvider
import com.adrien.blissfulcake.ui.viewmodel.AuthViewModel
import com.adrien.blissfulcake.ui.viewmodel.CakeViewModel
import com.adrien.blissfulcake.ui.viewmodel.CartViewModel
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.adrien.blissfulcake.R
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import coil.compose.AsyncImage
import kotlin.random.Random
import androidx.compose.foundation.isSystemInDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    if (hasError) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Something went wrong",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        hasError = false
                        errorMessage = ""
                    }
                ) {
                    Text("Try Again")
                }
            }
        }
        return
    }
    val context = LocalContext.current
    val cakeViewModel = remember { DependencyProvider.provideCakeViewModel(context) }
    val cartViewModel = remember { DependencyProvider.provideCartViewModel(context) }
    val favoritesViewModel = remember { DependencyProvider.provideFavoritesViewModel(context) }
    val authViewModel = remember { DependencyProvider.provideAuthViewModel(context) }
    
    val cakes by cakeViewModel.cakes.collectAsState()
    val selectedCategory by cakeViewModel.selectedCategory.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val cartItemCount by cartViewModel.itemCount.collectAsState()
    val isLoading by cakeViewModel.isLoading.collectAsState()
    val favoriteCakeIds by favoritesViewModel.favoriteCakeIds.collectAsState()
    
    // Load current user on app start
    LaunchedEffect(Unit) {
        try {
            authViewModel.loadCurrentUser()
        } catch (e: Exception) {
            println("DEBUG: Error loading current user: ${e.message}")
            hasError = true
            errorMessage = "Failed to load user data: ${e.message}"
        }
    }
    
    // Debug: Print current user state
    LaunchedEffect(currentUser) {
        println("DEBUG: Current user state changed: $currentUser")
    }
    
    // Debug logging for cakes
    LaunchedEffect(cakes) {
        try {
            println("HomeScreen: Received ${cakes.size} cakes")
            cakes.forEach { cake ->
                Log.d("checkpoint", "HomeScreen: Cake - ${cake.name} (ID: ${cake.id}, Category: ${cake.category})")
                println("HomeScreen: Cake - ${cake.name} (ID: ${cake.id})")
            }
        } catch (e: Exception) {
            println("DEBUG: Error in cakes LaunchedEffect: ${e.message}")
            hasError = true
            errorMessage = "Failed to load cakes: ${e.message}"
            e.printStackTrace()
        }
    }
    
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(currentUser) {
        try {
            currentUser?.let { user ->
                println("DEBUG: Current user loaded - ID: ${user.id}, Name: ${user.name}")
                cartViewModel.loadCartItems(user.id)
                favoritesViewModel.loadFavorites(user.id)
            } ?: run {
                println("DEBUG: No current user found in LaunchedEffect")
            }
        } catch (e: Exception) {
            println("DEBUG: Error in currentUser LaunchedEffect: ${e.message}")
            e.printStackTrace()
        }
    }
    
    // Debug: Log cart and favorites state changes
    LaunchedEffect(cartItemCount) {
        println("DEBUG: Cart item count changed: $cartItemCount")
    }
    
    LaunchedEffect(favoriteCakeIds) {
        println("DEBUG: Favorite cake IDs changed: ${favoriteCakeIds.size} items")
    }
    
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(
                message = snackbarMessage,
                duration = SnackbarDuration.Short
            )
            showSnackbar = false
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    val logoAnim = rememberInfiniteTransition().animateFloat(
                        initialValue = 0.95f,
                        targetValue = 1.05f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1800, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.blissful_logo),
                        contentDescription = stringResource(id = R.string.blissful_logo_desc),
                        modifier = Modifier
                            .height(44.dp)
                            .scale(logoAnim.value)
                            .shadow(8.dp, RoundedCornerShape(12.dp))
                    )
                },
                actions = {
                    Box {
                        IconButton(
                            onClick = { navController.navigate("cart") },
                            modifier = Modifier
                                .padding(8.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp))
                                .background(
                                    Brush.radialGradient(
                                                                        colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Text(
                                    text = cartItemCount.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                                                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.shadow(8.dp)
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { 
                        Icon(
                            Icons.Default.Home, 
                            contentDescription = "Home",
                            modifier = Modifier.scale(1.2f)
                        ) 
                    },
                    label = { Text("Home", fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("favorites") },
                    icon = { 
                        Icon(
                            Icons.Default.Favorite, 
                            contentDescription = "Favorites",
                            modifier = Modifier.scale(1.2f)
                        ) 
                    },
                    label = { Text("Favorites", fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("orders") },
                    icon = { 
                        Icon(
                            Icons.Default.List, 
                            contentDescription = "Orders",
                            modifier = Modifier.scale(1.2f)
                        ) 
                    },
                    label = { Text("Orders", fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("profile") },
                    icon = { 
                        Icon(
                            Icons.Default.Person, 
                            contentDescription = "Profile",
                            modifier = Modifier.scale(1.2f)
                        ) 
                    },
                    label = { Text("Profile", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
        ) {
            // Animated background particles
            FloatingParticles()
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Welcome Text with Animation
                    val welcomeAnim = rememberInfiniteTransition().animateFloat(
                        initialValue = 0.8f,
                        targetValue = 1.1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(20.dp))
                            .scale(welcomeAnim.value),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                                Text(
                        text = "Welcome to Blissful Cakes",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Discover our delicious cakes",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                item {
                    // Categories with enhanced styling
                    Text(
                        text = "Categories",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val categories = listOf("All", "Chocolate", "Vanilla", "Fruit", "Specialty", "Coffee")
                        
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            val scaleAnim = rememberInfiniteTransition().animateFloat(
                                initialValue = if (isSelected) 1.05f else 1f,
                                targetValue = if (isSelected) 1.1f else 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )
                            
                            FilterChip(
                                selected = isSelected,
                                onClick = { cakeViewModel.selectCategory(category) },
                                label = { 
                                    Text(
                                        category, 
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ) 
                                },
                                modifier = Modifier.scale(scaleAnim.value),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                item {
                    Text(
                        text = "Our Cakes",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else if (cakes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.blissful_logo),
                                    contentDescription = stringResource(id = R.string.blissful_logo_desc),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No cakes available",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Check back later for delicious cakes!",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(cakes.size) { index ->

                        CakeCard(
                            cake = cakes[index],
                            onAddToCart = {
                                currentUser?.let { user ->
                                    println("DEBUG: Adding to cart - User ID: ${user.id}, Cake ID: ${cakes[index].id}")
                                    cartViewModel.addToCart(user.id, cakes[index].id)
                                    snackbarMessage = "Added to cart!"
                                    showSnackbar = true
                                } ?: run {
                                    println("DEBUG: No current user found")
                                    snackbarMessage = "Please log in to add items to cart"
                                    showSnackbar = true
                                }
                            },
                            onToggleFavorite = {
                                currentUser?.let { user ->
                                    val cakeId = cakes[index].id
                                    val isCurrentlyFavorite = favoriteCakeIds.contains(cakeId)
                                    println("DEBUG: Toggling favorite - User ID: ${user.id}, Cake ID: $cakeId, Currently Favorite: $isCurrentlyFavorite")
                                    favoritesViewModel.toggleFavorite(user.id, cakeId)
                                    snackbarMessage = if (isCurrentlyFavorite) {
                                        "Removed from favorites!"
                                    } else {
                                        "Added to favorites!"
                                    }
                                    showSnackbar = true
                                } ?: run {
                                    println("DEBUG: No current user found for favorites")
                                    snackbarMessage = "Please log in to add favorites"
                                    showSnackbar = true
                                }
                            },
                            isFavorite = favoriteCakeIds.contains(cakes[index].id)
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
private fun FloatingParticles() {
    val particles = remember { List(15) { Random.nextFloat() } }
    val animation = rememberInfiniteTransition()
    val colorScheme = MaterialTheme.colorScheme
    
    val alpha by animation.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val rotation by animation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEachIndexed { index, _ ->
            val x = size.width * Random.nextFloat()
            val y = size.height * Random.nextFloat()
            val radius = 3f + Random.nextFloat() * 4f
            
            rotate(degrees = rotation) {
                drawCircle(
                    color = colorScheme.primary.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CakeCard(
    cake: Cake,
    onAddToCart: () -> Unit,
    onToggleFavorite: () -> Unit,
    isFavorite: Boolean
) {
    val scaleAnim = rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .shadow(16.dp, RoundedCornerShape(20.dp))
            .scale(scaleAnim.value),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cake Image with gradient background

            AsyncImage(
                modifier = Modifier.fillMaxHeight().width(160.dp),
                model = if (cake.imageUrl != null) cake.imageUrl else "https://imgs.search.brave.com/XykIvwYigz7bg8UY0hCn9alZ74Lz9TIE8QR1miq7PZE/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90NC5m/dGNkbi5uZXQvanBn/LzA2LzU3LzM3LzAx/LzM2MF9GXzY1NzM3/MDE1MF9wZE5lRzVw/akk5NzZaYXNWYktO/OVZxSDFyZm95a2RZ/VS5qcGc",
                contentDescription = null)
//            Box(
//                modifier = Modifier
//                    .width(160.dp)
//                    .fillMaxHeight()
//                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
//                    .background(
//                        Brush.radialGradient(
//                                                            colors = listOf(
//                                    MaterialTheme.colorScheme.primaryContainer,
//                                    MaterialTheme.colorScheme.secondaryContainer
//                                )
//                        )
//                    )
//            ) {
//                // Animated cake icon
//                val iconAnim = rememberInfiniteTransition().animateFloat(
//                    initialValue = 0.8f,
//                    targetValue = 1.2f,
//                    animationSpec = infiniteRepeatable(
//                        animation = tween(1500, easing = FastOutSlowInEasing),
//                        repeatMode = RepeatMode.Reverse
//                    )
//                )
//
//                Icon(
//                    Icons.Default.Cake,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(70.dp)
//                        .align(Alignment.Center)
//                        .scale(iconAnim.value),
//                                                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
            
            // Cake Details
            Column(
                modifier = Modifier.weight(1f).padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = cake.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = cake.description,
                        fontSize = 14.sp,
                        color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "NPR ${cake.price}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Add to Cart Button with animation
                val buttonAnim = rememberInfiniteTransition().animateFloat(
                    initialValue = 1f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(buttonAnim.value),
                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Add to Cart",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
