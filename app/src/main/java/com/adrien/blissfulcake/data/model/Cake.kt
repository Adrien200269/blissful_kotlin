package com.adrien.blissfulcake.data.model

data class Cake(
    val id: Int = 0,
    val name: String,
    val description: String,
    val price: Double, // Price in Nepali Rupees (NPR)
    val imageUrl: String,
    val category: String,
    val isAvailable: Boolean = true
) 