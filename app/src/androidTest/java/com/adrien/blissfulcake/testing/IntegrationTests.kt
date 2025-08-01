package com.adrien.blissfulcake.testing

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.adrien.blissfulcake.data.model.*
import com.adrien.blissfulcake.data.repository.*
import com.adrien.blissfulcake.ui.viewmodel.*
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntegrationTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockCakeRepository: CakeRepository
    private lateinit var mockCartRepository: CartRepository
    private lateinit var mockUserRepository: UserRepository
    private lateinit var mockOrderRepository: OrderRepository
    private lateinit var mockFavoritesRepository: FavoritesRepository

    private lateinit var cakeViewModel: CakeViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var favoritesViewModel: FavoritesViewModel

    @Before
    fun setup() {
        mockCakeRepository = mockk()
        mockCartRepository = mockk()
        mockUserRepository = mockk()
        mockOrderRepository = mockk()
        mockFavoritesRepository = mockk()

        cakeViewModel = CakeViewModel(mockCakeRepository)
        cartViewModel = CartViewModel(mockCartRepository)
        authViewModel = AuthViewModel(mockUserRepository)
        orderViewModel = OrderViewModel(mockOrderRepository)
        favoritesViewModel = FavoritesViewModel(mockFavoritesRepository)
    }

    @Test
    fun testUserLoginToHomeFlow() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val user = User(id = "user123", name = "Test User", email = email)
        
        coEvery { mockUserRepository.login(email, password) } returns Result.success(user)
        
        // When
        authViewModel.login(email, password)
        
        // Then
        assertEquals(AuthState.Success, authViewModel.authState.value)
        assertEquals(user, authViewModel.currentUser.value)
    }

    @Test
    fun testAddToCartFlow() = runTest {
        // Given
        val userId = "user123"
        val cakeId = 1
        val quantity = 2
        val cake = Cake(id = cakeId, name = "Chocolate Cake", price = 500.0)
        
        coEvery { mockCartRepository.addToCart(userId, cakeId, quantity) } just Runs
        coEvery { mockCartRepository.getCartItemsWithCakes(userId) } returns listOf(
            CartItemWithCake(
                cartItem = CartItem(id = 1, userId = userId, cakeId = cakeId, quantity = quantity),
                cake = cake
            )
        )
        
        // When
        cartViewModel.addToCart(userId, cakeId, quantity)
        cartViewModel.loadCartItems(userId)
        
        // Then
        coVerify { mockCartRepository.addToCart(userId, cakeId, quantity) }
        assertEquals(1, cartViewModel.cartItems.value.size)
        assertEquals(quantity, cartViewModel.itemCount.value)
        assertEquals(cake.price * quantity, cartViewModel.totalAmount.value, 0.01)
    }

    @Test
    fun testCakeFilteringFlow() = runTest {
        // Given
        val allCakes = listOf(
            Cake(id = 1, name = "Chocolate Cake", category = "Chocolate", price = 500.0),
            Cake(id = 2, name = "Vanilla Cake", category = "Vanilla", price = 400.0),
            Cake(id = 3, name = "Strawberry Cake", category = "Fruit", price = 600.0)
        )
        
        coEvery { mockCakeRepository.getAllCakes() } returns MutableStateFlow(allCakes)
        
        // When
        cakeViewModel.loadCakes()
        cakeViewModel.filterByCategory("Chocolate")
        
        // Then
        assertEquals("Chocolate", cakeViewModel.selectedCategory.value)
        val filteredCakes = cakeViewModel.cakes.value.filter { it.category == "Chocolate" }
        assertEquals(1, filteredCakes.size)
        assertEquals("Chocolate Cake", filteredCakes[0].name)
    }

    @Test
    fun testOrderCreationFlow() = runTest {
        // Given
        val userId = "user123"
        val customerName = "John Doe"
        val customerAddress = "Kathmandu, Nepal"
        val customerPhone = "+977-1234567890"
        val customerNotes = "Please deliver in the morning"
        val cartItems = listOf(
            CartItemWithCake(
                cartItem = CartItem(id = 1, userId = userId, cakeId = 1, quantity = 2),
                cake = Cake(id = 1, name = "Chocolate Cake", price = 500.0)
            )
        )
        val expectedOrderId = "order123"
        
        coEvery { 
            mockOrderRepository.createOrder(any(), any(), any(), any(), any(), any(), any(), any()) 
        } returns expectedOrderId
        
        // When
        orderViewModel.createOrder(userId, customerName, customerAddress, customerPhone, customerNotes, cartItems)
        
        // Then
        assertEquals(OrderState.Success(expectedOrderId), orderViewModel.orderState.value)
    }

    @Test
    fun testFavoritesFlow() = runTest {
        // Given
        val userId = "user123"
        val cakeId = 1
        val favorites = listOf(
            Cake(id = cakeId, name = "Chocolate Cake", price = 500.0, isFavorite = true)
        )
        
        coEvery { mockFavoritesRepository.getFavorites(userId) } returns MutableStateFlow(favorites)
        coEvery { mockFavoritesRepository.addToFavorites(userId, cakeId) } just Runs
        coEvery { mockFavoritesRepository.isFavorite(userId, cakeId) } returns true
        
        // When
        favoritesViewModel.loadFavorites(userId)
        favoritesViewModel.addToFavorites(userId, cakeId)
        
        // Then
        assertEquals(favorites, favoritesViewModel.favorites.value)
        coVerify { mockFavoritesRepository.addToFavorites(userId, cakeId) }
    }

    @Test
    fun testCartTotalCalculation() = runTest {
        // Given
        val userId = "user123"
        val cartItemsWithCakes = listOf(
            CartItemWithCake(
                cartItem = CartItem(id = 1, userId = userId, cakeId = 1, quantity = 2),
                cake = Cake(id = 1, name = "Chocolate Cake", price = 500.0)
            ),
            CartItemWithCake(
                cartItem = CartItem(id = 2, userId = userId, cakeId = 2, quantity = 1),
                cake = Cake(id = 2, name = "Vanilla Cake", price = 400.0)
            ),
            CartItemWithCake(
                cartItem = CartItem(id = 3, userId = userId, cakeId = 3, quantity = 3),
                cake = Cake(id = 3, name = "Strawberry Cake", price = 600.0)
            )
        )
        
        coEvery { mockCartRepository.getCartItemsWithCakes(userId) } returns cartItemsWithCakes
        
        // When
        cartViewModel.loadCartItems(userId)
        
        // Then
        val expectedTotal = (2 * 500.0) + (1 * 400.0) + (3 * 600.0) // 1000 + 400 + 1800 = 3200
        val expectedItemCount = 2 + 1 + 3 // 6 items
        
        assertEquals(expectedTotal, cartViewModel.totalAmount.value, 0.01)
        assertEquals(expectedItemCount, cartViewModel.itemCount.value)
        assertEquals(3, cartViewModel.cartItems.value.size)
    }

    @Test
    fun testUserRegistrationFlow() = runTest {
        // Given
        val name = "Test User"
        val email = "test@example.com"
        val password = "password123"
        val user = User(id = "user123", name = name, email = email)
        
        coEvery { mockUserRepository.register(name, email, password) } returns Result.success(user)
        
        // When
        authViewModel.register(name, email, password)
        
        // Then
        assertEquals(AuthState.Success, authViewModel.authState.value)
        assertEquals(user, authViewModel.currentUser.value)
    }

    @Test
    fun testCakeSearchAndFilterFlow() = runTest {
        // Given
        val allCakes = listOf(
            Cake(id = 1, name = "Chocolate Cake", category = "Chocolate", price = 500.0),
            Cake(id = 2, name = "Chocolate Truffle", category = "Chocolate", price = 600.0),
            Cake(id = 3, name = "Vanilla Cake", category = "Vanilla", price = 400.0),
            Cake(id = 4, name = "Strawberry Cake", category = "Fruit", price = 600.0)
        )
        
        coEvery { mockCakeRepository.getAllCakes() } returns MutableStateFlow(allCakes)
        
        // When
        cakeViewModel.loadCakes()
        cakeViewModel.filterByCategory("Chocolate")
        
        // Then
        val chocolateCakes = cakeViewModel.cakes.value.filter { it.category == "Chocolate" }
        assertEquals(2, chocolateCakes.size)
        assertTrue(chocolateCakes.all { it.name.contains("Chocolate") })
    }

    @Test
    fun testOrderHistoryFlow() = runTest {
        // Given
        val userId = "user123"
        val orders = listOf(
            Order(id = "order1", userId = userId, customerName = "John Doe", totalAmount = 1500.0, status = "completed"),
            Order(id = "order2", userId = userId, customerName = "John Doe", totalAmount = 800.0, status = "pending"),
            Order(id = "order3", userId = userId, customerName = "John Doe", totalAmount = 1200.0, status = "delivered")
        )
        
        coEvery { mockOrderRepository.getOrdersByUserId(userId) } returns MutableStateFlow(orders)
        
        // When
        orderViewModel.loadOrders(userId)
        
        // Then
        assertEquals(3, orderViewModel.orders.value.size)
        assertEquals(orders, orderViewModel.orders.value)
    }

    @Test
    fun testCartItemUpdateFlow() = runTest {
        // Given
        val cartItemId = 1
        val newQuantity = 3
        
        coEvery { mockCartRepository.updateQuantity(cartItemId, newQuantity) } just Runs
        
        // When
        cartViewModel.updateQuantity(cartItemId, newQuantity)
        
        // Then
        coVerify { mockCartRepository.updateQuantity(cartItemId, newQuantity) }
    }

    @Test
    fun testRemoveFromCartFlow() = runTest {
        // Given
        val cartItemId = 1
        
        coEvery { mockCartRepository.removeFromCart(cartItemId) } just Runs
        
        // When
        cartViewModel.removeFromCart(cartItemId)
        
        // Then
        coVerify { mockCartRepository.removeFromCart(cartItemId) }
    }

    @Test
    fun testFavoritesToggleFlow() = runTest {
        // Given
        val userId = "user123"
        val cakeId = 1
        
        coEvery { mockFavoritesRepository.addToFavorites(userId, cakeId) } just Runs
        coEvery { mockFavoritesRepository.removeFromFavorites(userId, cakeId) } just Runs
        coEvery { mockFavoritesRepository.isFavorite(userId, cakeId) } returns true andThen false
        
        // When - Add to favorites
        favoritesViewModel.addToFavorites(userId, cakeId)
        
        // Then
        coVerify { mockFavoritesRepository.addToFavorites(userId, cakeId) }
        
        // When - Remove from favorites
        favoritesViewModel.removeFromFavorites(userId, cakeId)
        
        // Then
        coVerify { mockFavoritesRepository.removeFromFavorites(userId, cakeId) }
    }
} 