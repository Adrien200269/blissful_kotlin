package com.adrien.blissfulcake.data.model

import java.util.Date

data class Order(
    val id: Int = 0,
    val userId: Int,
    val customerName: String,
    val customerAddress: String,
    val customerPhone: String,
    val customerNotes: String = "",
    val totalAmount: Double,
    val orderDate: Date = Date(),
    val status: OrderStatus = OrderStatus.PENDING
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    DELIVERED,
    CANCELLED
} 