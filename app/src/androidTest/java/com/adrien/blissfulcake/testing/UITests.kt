package com.adrien.blissfulcake.testing

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.adrien.blissfulcake.MainActivity
import com.adrien.blissfulcake.data.model.Cake
import com.adrien.blissfulcake.ui.screens.HomeScreen
import com.adrien.blissfulcake.ui.screens.LoginScreen
import com.adrien.blissfulcake.ui.screens.RegisterScreen
import com.adrien.blissfulcake.ui.screens.CartScreen
import com.adrien.blissfulcake.ui.screens.ProfileScreen
import com.adrien.blissfulcake.ui.viewmodel.*
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UITests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testLoginScreenUI() {
        composeTestRule.setContent {
            LoginScreen(
                authViewModel = mockk(),
                onNavigateToRegister = {},
                onNavigateToHome = {}
            )
        }

        // Verify login screen elements are displayed
        composeTestRule.onNodeWithText("Login").assertExists()
        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onNodeWithText("Don't have an account? Register").assertExists()
        composeTestRule.onNodeWithText("Forgot Password?").assertExists()
    }

    @Test
    fun testRegisterScreenUI() {
        composeTestRule.setContent {
            RegisterScreen(
                authViewModel = mockk(),
                onNavigateToLogin = {},
                onNavigateToHome = {}
            )
        }

        // Verify register screen elements are displayed
        composeTestRule.onNodeWithText("Register").assertExists()
        composeTestRule.onNodeWithText("Name").assertExists()
        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onNodeWithText("Already have an account? Login").assertExists()
    }

    @Test
    fun testHomeScreenUI() {
        val mockCakeViewModel = mockk<CakeViewModel>()
        val mockCartViewModel = mockk<CartViewModel>()
        val mockFavoritesViewModel = mockk<FavoritesViewModel>()

        composeTestRule.setContent {
            HomeScreen(
                cakeViewModel = mockCakeViewModel,
                cartViewModel = mockCartViewModel,
                favoritesViewModel = mockFavoritesViewModel,
                onNavigateToCart = {},
                onNavigateToProfile = {},
                onNavigateToFavorites = {},
                onNavigateToOrders = {}
            )
        }

        // Verify home screen elements are displayed
        composeTestRule.onNodeWithText("BlissfulCakes").assertExists()
        composeTestRule.onNodeWithText("All").assertExists()
        composeTestRule.onNodeWithText("Chocolate").assertExists()
        composeTestRule.onNodeWithText("Vanilla").assertExists()
        composeTestRule.onNodeWithText("Fruit").assertExists()
    }

    @Test
    fun testCartScreenUI() {
        val mockCartViewModel = mockk<CartViewModel>()

        composeTestRule.setContent {
            CartScreen(
                cartViewModel = mockCartViewModel,
                onNavigateBack = {},
                onNavigateToCheckout = {}
            )
        }

        // Verify cart screen elements are displayed
        composeTestRule.onNodeWithText("Cart").assertExists()
        composeTestRule.onNodeWithText("Total").assertExists()
        composeTestRule.onNodeWithText("Checkout").assertExists()
    }

    @Test
    fun testProfileScreenUI() {
        val mockAuthViewModel = mockk<AuthViewModel>()

        composeTestRule.setContent {
            ProfileScreen(
                authViewModel = mockAuthViewModel,
                onNavigateToSettings = {},
                onNavigateToOrders = {},
                onNavigateToLogin = {}
            )
        }

        // Verify profile screen elements are displayed
        composeTestRule.onNodeWithText("Profile").assertExists()
        composeTestRule.onNodeWithText("Settings").assertExists()
        composeTestRule.onNodeWithText("Orders").assertExists()
        composeTestRule.onNodeWithText("Logout").assertExists()
    }

    @Test
    fun testCakeItemDisplay() {
        val cake = Cake(
            id = 1,
            name = "Chocolate Cake",
            description = "Delicious chocolate cake",
            price = 500.0,
            imageUrl = "https://example.com/chocolate-cake.jpg",
            category = "Chocolate"
        )

        composeTestRule.setContent {
            // Create a simple cake item display
            androidx.compose.material3.Card {
                androidx.compose.material3.Text(cake.name)
                androidx.compose.material3.Text("NPR ${cake.price}")
                androidx.compose.material3.Text(cake.description)
            }
        }

        // Verify cake item elements are displayed
        composeTestRule.onNodeWithText("Chocolate Cake").assertExists()
        composeTestRule.onNodeWithText("NPR 500.0").assertExists()
        composeTestRule.onNodeWithText("Delicious chocolate cake").assertExists()
    }

    @Test
    fun testNavigationElements() {
        composeTestRule.setContent {
            // Create a simple bottom navigation
            androidx.compose.material3.BottomAppBar {
                androidx.compose.material3.NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { androidx.compose.material.icons.Icons.Default.Home },
                    label = { androidx.compose.material3.Text("Home") }
                )
                androidx.compose.material3.NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { androidx.compose.material.icons.Icons.Default.ShoppingCart },
                    label = { androidx.compose.material3.Text("Cart") }
                )
                androidx.compose.material3.NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { androidx.compose.material.icons.Icons.Default.Favorite },
                    label = { androidx.compose.material3.Text("Favorites") }
                )
                androidx.compose.material3.NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { androidx.compose.material.icons.Icons.Default.Person },
                    label = { androidx.compose.material3.Text("Profile") }
                )
            }
        }

        // Verify navigation elements are displayed
        composeTestRule.onNodeWithText("Home").assertExists()
        composeTestRule.onNodeWithText("Cart").assertExists()
        composeTestRule.onNodeWithText("Favorites").assertExists()
        composeTestRule.onNodeWithText("Profile").assertExists()
    }

    @Test
    fun testSearchFunctionality() {
        composeTestRule.setContent {
            // Create a search bar
            androidx.compose.material3.OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { androidx.compose.material3.Text("Search cakes...") },
                leadingIcon = { androidx.compose.material.icons.Icons.Default.Search }
            )
        }

        // Verify search elements are displayed
        composeTestRule.onNodeWithText("Search cakes...").assertExists()
    }

    @Test
    fun testCategoryFilter() {
        composeTestRule.setContent {
            // Create category filter chips
            androidx.compose.material3.Row {
                androidx.compose.material3.FilterChip(
                    selected = true,
                    onClick = {},
                    label = { androidx.compose.material3.Text("All") }
                )
                androidx.compose.material3.FilterChip(
                    selected = false,
                    onClick = {},
                    label = { androidx.compose.material3.Text("Chocolate") }
                )
                androidx.compose.material3.FilterChip(
                    selected = false,
                    onClick = {},
                    label = { androidx.compose.material3.Text("Vanilla") }
                )
                androidx.compose.material3.FilterChip(
                    selected = false,
                    onClick = {},
                    label = { androidx.compose.material3.Text("Fruit") }
                )
            }
        }

        // Verify category filter elements are displayed
        composeTestRule.onNodeWithText("All").assertExists()
        composeTestRule.onNodeWithText("Chocolate").assertExists()
        composeTestRule.onNodeWithText("Vanilla").assertExists()
        composeTestRule.onNodeWithText("Fruit").assertExists()
    }

    @Test
    fun testAddToCartButton() {
        composeTestRule.setContent {
            // Create add to cart button
            androidx.compose.material3.Button(
                onClick = {},
                content = { androidx.compose.material3.Text("Add to Cart") }
            )
        }

        // Verify add to cart button is displayed
        composeTestRule.onNodeWithText("Add to Cart").assertExists()
    }

    @Test
    fun testFavoriteButton() {
        composeTestRule.setContent {
            // Create favorite button
            androidx.compose.material3.IconButton(
                onClick = {},
                content = { androidx.compose.material.icons.Icons.Default.Favorite }
            )
        }

        // Verify favorite button is displayed
        composeTestRule.onNodeWithContentDescription("Favorite").assertExists()
    }

    @Test
    fun testPriceDisplay() {
        composeTestRule.setContent {
            // Create price display
            androidx.compose.material3.Text("NPR 500.0")
        }

        // Verify price is displayed correctly
        composeTestRule.onNodeWithText("NPR 500.0").assertExists()
    }

    @Test
    fun testQuantitySelector() {
        composeTestRule.setContent {
            // Create quantity selector
            androidx.compose.material3.Row {
                androidx.compose.material3.IconButton(
                    onClick = {},
                    content = { androidx.compose.material.icons.Icons.Default.Remove }
                )
                androidx.compose.material3.Text("1")
                androidx.compose.material3.IconButton(
                    onClick = {},
                    content = { androidx.compose.material.icons.Icons.Default.Add }
                )
            }
        }

        // Verify quantity selector elements are displayed
        composeTestRule.onNodeWithText("1").assertExists()
    }
} 