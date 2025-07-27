package com.example.blissfulcakes.data.repository

import com.example.blissfulcakes.data.dao.OrderDao
import com.example.blissfulcakes.data.dao.OrderItemDao
import com.example.blissfulcakes.data.model.Order
import com.example.blissfulcakes.data.model.OrderItem
import kotlinx.coroutines.flow.Flow

class OrderRepository(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) {
    fun getOrdersByUserId(userId: Int): Flow<List<Order>> {
        return orderDao.getOrdersByUserId(userId)
    }
    
    fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders()
    }
    
    fun getOrderItemsByOrderId(orderId: Int): Flow<List<OrderItem>> {
        return orderItemDao.getOrderItemsByOrderId(orderId)
    }
    
    suspend fun createOrder(order: Order, orderItems: List<OrderItem>): Long {
        val orderId = orderDao.insertOrder(order)
        val orderItemsWithOrderId = orderItems.map { orderItem ->
            orderItem.copy(orderId = orderId.toInt())
        }
        orderItemDao.insertOrderItems(orderItemsWithOrderId)
        return orderId
    }
    
    suspend fun updateOrder(order: Order) {
        orderDao.updateOrder(order)
    }
    
    suspend fun deleteOrder(order: Order) {
        orderDao.deleteOrder(order)
    }
} 