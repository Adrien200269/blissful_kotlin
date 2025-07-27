package com.example.blissfulcakes.data.dao

import androidx.room.*
import com.example.blissfulcakes.data.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItemsByUserId(userId: Int): Flow<List<CartItem>>
    
    @Insert
    suspend fun insertCartItem(cartItem: CartItem): Long
    
    @Update
    suspend fun updateCartItem(cartItem: CartItem)
    
    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)
    
    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Int)
    
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND cakeId = :cakeId LIMIT 1")
    suspend fun getCartItemByUserAndCake(userId: Int, cakeId: Int): CartItem?
} 