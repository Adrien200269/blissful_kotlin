package com.example.blissfulcakes.data.repository

import com.example.blissfulcakes.data.dao.CartDao
import com.example.blissfulcakes.data.dao.CakeDao
import com.example.blissfulcakes.data.model.CartItem
import com.example.blissfulcakes.data.model.Cake
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class CartRepository(
    private val cartDao: CartDao,
    private val cakeDao: CakeDao
) {
    fun getCartItemsWithCakes(userId: Int): Flow<List<CartItemWithCake>> {
        return cartDao.getCartItemsByUserId(userId).combine(cakeDao.getAllCakes()) { cartItems, cakes ->
            cartItems.mapNotNull { cartItem ->
                val cake = cakes.find { it.id == cartItem.cakeId }
                if (cake != null) {
                    CartItemWithCake(cartItem, cake)
                } else null
            }
        }
    }
    
    suspend fun addToCart(userId: Int, cakeId: Int, quantity: Int = 1) {
        val existingItem = cartDao.getCartItemByUserAndCake(userId, cakeId)
        if (existingItem != null) {
            cartDao.updateCartItem(existingItem.copy(quantity = existingItem.quantity + quantity))
        } else {
            cartDao.insertCartItem(CartItem(cakeId = cakeId, quantity = quantity, userId = userId))
        }
    }
    
    suspend fun updateCartItemQuantity(cartItem: CartItem) {
        cartDao.updateCartItem(cartItem)
    }
    
    suspend fun removeFromCart(cartItem: CartItem) {
        cartDao.deleteCartItem(cartItem)
    }
    
    suspend fun clearCart(userId: Int) {
        cartDao.clearCart(userId)
    }
}

data class CartItemWithCake(
    val cartItem: CartItem,
    val cake: Cake
) 