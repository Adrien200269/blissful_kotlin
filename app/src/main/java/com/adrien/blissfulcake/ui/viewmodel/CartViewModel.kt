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
            // TODO: Implement getCartItemsWithCakes and CartItemWithCake if needed, or remove/comment out their usages.
            _cartItems.value = emptyList() // Placeholder
            _itemCount.value = 0 // Placeholder
            _totalAmount.value = 0.0 // Placeholder
        }
    }
    
    // TODO: Implement addToCart if needed.
    fun addToCart(userId: String, cakeId: Int, quantity: Int = 1) {
        viewModelScope.launch {
            // cartRepository.addToCart(userId, cakeId, quantity)
        }
    }
    
    // TODO: Implement updateCartItemQuantity if needed.
    fun updateQuantity(cartItem: CartItem) {
        viewModelScope.launch {
            // cartRepository.updateCartItemQuantity(cartItem)
        }
    }
    
    // TODO: Implement removeFromCart if needed.
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            // cartRepository.removeFromCart(cartItem)
        }
    }
    
    // TODO: Implement clearCart if needed.
    fun clearCart(userId: String) {
        viewModelScope.launch {
            // cartRepository.clearCart(userId)
        }
    }
} 