package com.adrien.blissfulcake.data.model

import java.util.Date

data class Order(
    val id: Int = 0,
    val userId: String = "",
    val customerName: String = "",
    val customerAddress: String = "",
    val customerPhone: String = "",
    val customerNotes: String = "",
    val totalAmount: Double = 0.0,
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