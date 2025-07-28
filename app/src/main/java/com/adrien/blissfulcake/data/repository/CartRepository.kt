package com.adrien.blissfulcake.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.adrien.blissfulcake.data.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import com.adrien.blissfulcake.data.repository.CartItemWithCake

data class CartItemWithCake(
    val cartItem: com.adrien.blissfulcake.data.model.CartItem,
    val cake: com.adrien.blissfulcake.data.model.Cake
)

class CartRepository {
    private val db = FirebaseFirestore.getInstance()
    private val cartItemsCollection = db.collection("cart_items")

    fun getCartItemsByUserId(userId: Int): Flow<List<CartItem>> = callbackFlow {
        val listener = cartItemsCollection.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val items = snapshot?.documents?.mapNotNull { it.toObject(CartItem::class.java) } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun insertCartItem(cartItem: CartItem) {
        cartItemsCollection.document(cartItem.id.toString()).set(cartItem).await()
    }

    suspend fun updateCartItem(cartItem: CartItem) {
        cartItemsCollection.document(cartItem.id.toString()).set(cartItem).await()
    }

    suspend fun deleteCartItem(cartItem: CartItem) {
        cartItemsCollection.document(cartItem.id.toString()).delete().await()
    }

    suspend fun clearCart(userId: Int) {
        val snapshot = cartItemsCollection.whereEqualTo("userId", userId).get().await()
        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }

    suspend fun getCartItemByUserAndCake(userId: Int, cakeId: Int): CartItem? {
        val snapshot = cartItemsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("cakeId", cakeId)
            .get().await()
        return snapshot.documents.firstOrNull()?.toObject(CartItem::class.java)
    }
} 