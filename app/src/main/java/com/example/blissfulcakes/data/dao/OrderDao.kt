package com.example.blissfulcakes.data.dao

import androidx.room.*
import com.example.blissfulcakes.data.model.Order
import com.example.blissfulcakes.data.model.OrderItem
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    fun getOrdersByUserId(userId: Int): Flow<List<Order>>
    
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<Order>>
    
    @Insert
    suspend fun insertOrder(order: Order): Long
    
    @Update
    suspend fun updateOrder(order: Order)
    
    @Delete
    suspend fun deleteOrder(order: Order)
}

@Dao
interface OrderItemDao {
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsByOrderId(orderId: Int): Flow<List<OrderItem>>
    
    @Insert
    suspend fun insertOrderItem(orderItem: OrderItem): Long
    
    @Insert
    suspend fun insertOrderItems(orderItems: List<OrderItem>)
    
    @Update
    suspend fun updateOrderItem(orderItem: OrderItem)
    
    @Delete
    suspend fun deleteOrderItem(orderItem: OrderItem)
} 