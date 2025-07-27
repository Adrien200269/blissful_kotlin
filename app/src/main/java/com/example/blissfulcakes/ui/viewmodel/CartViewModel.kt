package com.example.blissfulcakes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blissfulcakes.data.model.CartItem
import com.example.blissfulcakes.data.repository.CartRepository
import com.example.blissfulcakes.data.repository.CartItemWithCake
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
    
    fun loadCartItems(userId: Int) {
        viewModelScope.launch {
            cartRepository.getCartItemsWithCakes(userId).collect { items ->
                _cartItems.value = items
                _itemCount.value = items.sumOf { it.cartItem.quantity }
                _totalAmount.value = items.sumOf { it.cartItem.quantity * it.cake.price }
            }
        }
    }
    
    fun addToCart(userId: Int, cakeId: Int, quantity: Int = 1) {
        viewModelScope.launch {
            cartRepository.addToCart(userId, cakeId, quantity)
        }
    }
    
    fun updateQuantity(cartItem: CartItem) {
        viewModelScope.launch {
            cartRepository.updateCartItemQuantity(cartItem)
        }
    }
    
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartRepository.removeFromCart(cartItem)
        }
    }
    
    fun clearCart(userId: Int) {
        viewModelScope.launch {
            cartRepository.clearCart(userId)
        }
    }
} 