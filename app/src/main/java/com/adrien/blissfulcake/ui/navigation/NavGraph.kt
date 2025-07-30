package com.adrien.blissfulcake.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adrien.blissfulcake.ui.screens.*
import com.adrien.blissfulcake.ui.theme.BlissfulCakesTheme
import com.adrien.blissfulcake.ui.theme.LocalThemeManager

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val themeManager = LocalThemeManager.current
    
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                SplashScreen(navController)
            }
        }
        
        composable("login") {
            BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                LoginScreen(navController)
            }
        }
        
        composable("register") {
            BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                RegisterScreen(navController)
            }
        }
        
        composable("forgot_password") {
            BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                ForgotPasswordScreen(navController)
            }
        }
        
        composable("home") {
            BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                HomeScreen(navController)
            }
        }
        
        composable("cart") {
            BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                CartScreen(navController)
            }
        }
        
        composable("checkout") {
            BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                CheckoutScreen(navController)
            }
        }
        
        composable("orders") {
            BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                OrdersScreen(navController)
            }
        }
        
        composable("profile") {
            BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                ProfileScreen(navController)
            }
        }
        
        composable("settings") {
            BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                SettingsScreen(navController)
            }
        }
        
        composable("about") {
            BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                AboutScreen(navController)
            }
        }
    }
} 