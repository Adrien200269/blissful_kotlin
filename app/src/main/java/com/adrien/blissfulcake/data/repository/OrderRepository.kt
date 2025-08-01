package com.adrien.blissfulcake.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.adrien.blissfulcake.data.model.Order
import com.adrien.blissfulcake.data.model.OrderItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

class OrderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = db.collection("orders")
    private val orderItemsCollection = db.collection("order_items")

    fun getOrdersByUserId(userId: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersCollection.whereEqualTo("userId", userId)
            .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val orders = snapshot?.documents?.mapNotNull { it.toObject(Order::class.java) } ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    fun getAllOrders(): Flow<List<Order>> = callbackFlow {
        val listener = ordersCollection
            .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val orders = snapshot?.documents?.mapNotNull { it.toObject(Order::class.java) } ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    suspend fun insertOrder(order: Order) {
        ordersCollection.document(order.id.toString()).set(order).await()
    }

    suspend fun updateOrder(order: Order) {
        ordersCollection.document(order.id.toString()).set(order).await()
    }

    suspend fun deleteOrder(order: Order) {
        ordersCollection.document(order.id.toString()).delete().await()
    }

    fun getOrderItemsByOrderId(orderId: Int): Flow<List<OrderItem>> = callbackFlow {
        val listener = orderItemsCollection.whereEqualTo("orderId", orderId)
            .addSnapshotListener { snapshot, _ ->
                val items = snapshot?.documents?.mapNotNull { it.toObject(OrderItem::class.java) } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun insertOrderItem(orderItem: OrderItem) {
        orderItemsCollection.document(orderItem.id.toString()).set(orderItem).await()
    }

    suspend fun insertOrderItems(orderItems: List<OrderItem>) {
        orderItems.forEach { insertOrderItem(it) }
    }

    suspend fun updateOrderItem(orderItem: OrderItem) {
        orderItemsCollection.document(orderItem.id.toString()).set(orderItem).await()
    }

    suspend fun deleteOrderItem(orderItem: OrderItem) {
        orderItemsCollection.document(orderItem.id.toString()).delete().await()
    }

    suspend fun createOrder(order: com.adrien.blissfulcake.data.model.Order, orderItems: List<com.adrien.blissfulcake.data.model.OrderItem>): Long {
        try {
            // Generate a unique order ID using timestamp
            val orderId = System.currentTimeMillis()
            val orderWithId = order.copy(id = orderId.toInt())
            
            // Save the order
            insertOrder(orderWithId)
            
            // Save order items with the correct order ID
            val orderItemsWithId = orderItems.map { item ->
                item.copy(orderId = orderId.toInt())
            }
            insertOrderItems(orderItemsWithId)
            
            return orderId
        } catch (e: Exception) {
            throw e
        }
    }
} 