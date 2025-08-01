package com.adrien.blissfulcake.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.adrien.blissfulcake.data.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import com.adrien.blissfulcake.data.repository.CartItemWithCake
import com.adrien.blissfulcake.data.repository.CakeRepository

data class CartItemWithCake(
    val cartItem: com.adrien.blissfulcake.data.model.CartItem,
    val cake: com.adrien.blissfulcake.data.model.Cake
)

class CartRepository {
    private val db = FirebaseFirestore.getInstance()
    private val cartItemsCollection = db.collection("cart_items")

    fun getCartItemsByUserId(userId: String): Flow<List<CartItem>> = callbackFlow {
        val listener = cartItemsCollection.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CartItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun insertCartItem(cartItem: CartItem) {
        cartItemsCollection.add(cartItem).await()
    }

    suspend fun updateCartItem(cartItem: CartItem) {
        cartItemsCollection.document(cartItem.id).set(cartItem).await()
    }

    suspend fun deleteCartItem(cartItem: CartItem) {
        cartItemsCollection.document(cartItem.id).delete().await()
    }

    suspend fun clearCart(userId: String) {
        val snapshot = cartItemsCollection.whereEqualTo("userId", userId).get().await()
        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }

    suspend fun getCartItemByUserAndCake(userId: String, cakeId: Int): CartItem? {
        val snapshot = cartItemsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("cakeId", cakeId)
            .get().await()
        return snapshot.documents.firstOrNull()?.let { doc ->
            doc.toObject(CartItem::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun addToCart(userId: String, cakeId: Int, quantity: Int = 1) {
        try {
            println("DEBUG: CartRepository.addToCart - User ID: $userId, Cake ID: $cakeId")
            val existingItem = getCartItemByUserAndCake(userId, cakeId)
            if (existingItem != null) {
                // Update quantity if item already exists
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                updateCartItem(updatedItem)
                println("DEBUG: Updated existing cart item")
            } else {
                // Add new item
                val newItem = CartItem(
                    cakeId = cakeId,
                    quantity = quantity,
                    userId = userId
                )
                insertCartItem(newItem)
                println("DEBUG: Added new cart item")
            }
        } catch (e: Exception) {
            println("DEBUG: Error in CartRepository.addToCart: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun updateCartItemQuantity(cartItem: CartItem) {
        updateCartItem(cartItem)
    }

    suspend fun removeFromCart(cartItem: CartItem) {
        deleteCartItem(cartItem)
    }

    suspend fun getCartItemsWithCakes(userId: String): List<CartItemWithCake> {
        try {
            val cartItems = getCartItemsByUserId(userId).first()
            val cakeRepository = CakeRepository()
            val cakes = cakeRepository.getAllCakes().first()
            
            return cartItems.mapNotNull { cartItem ->
                val cake = cakes.find { it.id == cartItem.cakeId }
                if (cake != null) {
                    CartItemWithCake(cartItem, cake)
                } else null
            }
        } catch (e: Exception) {
            return emptyList()
        }
    }
} 