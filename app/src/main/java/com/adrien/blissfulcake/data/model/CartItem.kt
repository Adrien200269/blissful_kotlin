package com.adrien.blissfulcake.data.model

data class CartItem(
    val id: String = "",
    val cakeId: Int,
    val quantity: Int,
    val userId: String
) 