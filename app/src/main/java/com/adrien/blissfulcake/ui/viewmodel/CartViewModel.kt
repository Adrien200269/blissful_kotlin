package com.adrien.blissfulcake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrien.blissfulcake.data.model.CartItem
import com.adrien.blissfulcake.data.repository.CartRepository
import com.adrien.blissfulcake.data.repository.CartItemWithCake
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {
    
    private val _cartItems = MutableStateFlow<List<CartItemWithCake>>(emptyList())
    val cartItems: StateFlow<List<CartItemWithCake>> = _cartItems.asStateFlow()
    
    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount: StateFlow<Double> = _totalAmount.asStateFlow()
    
    private val _itemCount = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _itemCount.asStateFlow()
    
    fun loadCartItems(userId: String) {
        viewModelScope.launch {
            try {
                println("DEBUG: CartViewModel.loadCartItems - User ID: $userId")
                val cartItemsWithCakes = cartRepository.getCartItemsWithCakes(userId)
                println("DEBUG: CartViewModel.loadCartItems - Loaded ${cartItemsWithCakes.size} cart items with cakes")
                _cartItems.value = cartItemsWithCakes
                _itemCount.value = cartItemsWithCakes.sumOf { it.cartItem.quantity }
                _totalAmount.value = cartItemsWithCakes.sumOf { it.cartItem.quantity * it.cake.price }
                println("DEBUG: CartViewModel.loadCartItems - Updated item count: ${_itemCount.value}, total amount: ${_totalAmount.value}")
            } catch (e: Exception) {
                println("DEBUG: CartViewModel.loadCartItems error: ${e.message}")
                e.printStackTrace()
                // Handle error
                _cartItems.value = emptyList()
                _itemCount.value = 0
                _totalAmount.value = 0.0
            }
        }
    }
    
    fun addToCart(userId: String, cakeId: Int, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                println("DEBUG: CartViewModel.addToCart called - User ID: $userId, Cake ID: $cakeId")
                cartRepository.addToCart(userId, cakeId, quantity)
                // Reload cart items to update UI
                loadCartItems(userId)
                println("DEBUG: Cart item added successfully")
            } catch (e: Exception) {
                println("DEBUG: Error adding to cart: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun updateQuantity(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                cartRepository.updateCartItemQuantity(cartItem)
                // Reload cart items to update UI
                loadCartItems(cartItem.userId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(cartItem)
                // Reload cart items to update UI
                loadCartItems(cartItem.userId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun clearCart(userId: String) {
        viewModelScope.launch {
            try {
                cartRepository.clearCart(userId)
                _cartItems.value = emptyList()
                _itemCount.value = 0
                _totalAmount.value = 0.0
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun diagnoseCart(userId: String) {
        viewModelScope.launch {
            try {
                println("DEBUG: CartViewModel.diagnoseCart called for user: $userId")
                cartRepository.diagnoseCartItems(userId)
            } catch (e: Exception) {
                println("DEBUG: Error in diagnoseCart: ${e.message}")
                e.printStackTrace()
            }
        }
    }
} 