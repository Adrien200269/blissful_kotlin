package com.adrien.blissfulcake.data.model

data class Cake(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0, // Price in Nepali Rupees (NPR)
    val imageUrl: String = "",
    val category: String = "",
    val available: Boolean = true, // Firestore field name
    val isFavorite: Boolean = false
) 