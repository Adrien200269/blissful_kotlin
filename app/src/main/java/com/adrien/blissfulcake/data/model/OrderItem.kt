package com.adrien.blissfulcake.data.model

data class OrderItem(
    val id: Int = 0,
    val orderId: Int,
    val cakeId: Int,
    val quantity: Int,
    val price: Double
) 