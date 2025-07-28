package com.example.blissfulcakes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.blissfulcakes.ui.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
        }
        
        // Update: Accept cakeId as an argument (nullable)
        composable("checkout?cakeId={cakeId}") { backStackEntry ->
            val cakeId = backStackEntry.arguments?.getString("cakeId")?.toIntOrNull()
            CheckoutScreen(navController = navController, cakeId = cakeId)
        }
        
        composable(Screen.Orders.route) {
            OrdersScreen(navController = navController)
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Cart : Screen("cart")
    object Checkout : Screen("checkout?cakeId={cakeId}")
    object Orders : Screen("orders")
    object Profile : Screen("profile")
} 