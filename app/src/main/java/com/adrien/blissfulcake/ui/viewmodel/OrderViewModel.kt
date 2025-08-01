package com.adrien.blissfulcake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrien.blissfulcake.data.model.Order
import com.adrien.blissfulcake.data.model.OrderItem
import com.adrien.blissfulcake.data.repository.OrderRepository
import com.adrien.blissfulcake.data.repository.CartItemWithCake
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {
    
    init {
        println("DEBUG: OrderViewModel - Initialized")
    }
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _orderState = MutableStateFlow<OrderState>(OrderState.Initial)
    val orderState: StateFlow<OrderState> = _orderState.asStateFlow()
    
    fun loadOrders(userId: String) {
        println("DEBUG: OrderViewModel.loadOrders called with userId: $userId")
        viewModelScope.launch {
            try {
                println("DEBUG: OrderViewModel.loadOrders - User ID: $userId")
                orderRepository.getOrdersByUserId(userId).collect { orders ->
                    println("DEBUG: OrderViewModel - Received ${orders.size} orders from repository")
                    _orders.value = orders
                    println("DEBUG: OrderViewModel - _orders.value updated to: ${_orders.value.size} items")
                }
            } catch (e: Exception) {
                println("DEBUG: OrderViewModel.loadOrders error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun createOrder(
        userId: String,
        customerName: String,
        customerAddress: String,
        customerPhone: String,
        customerNotes: String,
        cartItems: List<CartItemWithCake>
    ) {
        viewModelScope.launch {
            _orderState.value = OrderState.Loading
            try {
                val totalAmount = cartItems.sumOf { it.cartItem.quantity * it.cake.price }
                val order = Order(
                    userId = userId,
                    customerName = customerName,
                    customerAddress = customerAddress,
                    customerPhone = customerPhone,
                    customerNotes = customerNotes,
                    totalAmount = totalAmount
                )
                val orderItems = cartItems.map { cartItemWithCake ->
                    OrderItem(
                        orderId = 0, // Will be set by repository
                        cakeId = cartItemWithCake.cake.id,
                        quantity = cartItemWithCake.cartItem.quantity,
                        price = cartItemWithCake.cake.price
                    )
                }
                val orderId = orderRepository.createOrder(order, orderItems)
                _orderState.value = OrderState.Success(orderId)
            } catch (e: Exception) {
                _orderState.value = OrderState.Error(e.message ?: "Failed to create order")
            }
        }
    }
    
    fun clearOrderState() {
        _orderState.value = OrderState.Initial
    }
}

sealed class OrderState {
    object Initial : OrderState()
    object Loading : OrderState()
    data class Success(val orderId: Long) : OrderState()
    data class Error(val message: String) : OrderState()
} 