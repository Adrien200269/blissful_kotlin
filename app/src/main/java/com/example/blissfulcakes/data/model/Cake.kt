package com.example.blissfulcakes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cakes")
data class Cake(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val price: Double, // Price in Nepali Rupees (NPR)
    val imageUrl: String,
    val category: String,
    val isAvailable: Boolean = true
) 