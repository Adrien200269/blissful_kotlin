package com.example.blissfulcakes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
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