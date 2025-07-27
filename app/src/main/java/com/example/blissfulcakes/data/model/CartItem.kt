package com.example.blissfulcakes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cakeId: Int,
    val quantity: Int,
    val userId: Int
) 