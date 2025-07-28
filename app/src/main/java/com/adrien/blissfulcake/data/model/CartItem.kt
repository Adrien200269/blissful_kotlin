package com.adrien.blissfulcake.data.model

data class CartItem(
    val id: Int = 0,
    val cakeId: Int,
    val quantity: Int,
    val userId: Int
) 