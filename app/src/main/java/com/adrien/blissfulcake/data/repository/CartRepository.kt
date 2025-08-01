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
        println("DEBUG: CartRepository.getCartItemsByUserId - User ID: $userId")
        val listener = cartItemsCollection.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("DEBUG: Error getting cart items: ${error.message}")
                } else {
                    val items = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(CartItem::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
                    println("DEBUG: CartRepository.getCartItemsByUserId - Found ${items.size} cart items")
                    items.forEach { item ->
                        println("DEBUG: Cart item - ID: ${item.id}, Cake ID: ${item.cakeId}, Quantity: ${item.quantity}")
                    }
                    trySend(items)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun insertCartItem(cartItem: CartItem) {
        try {
            println("DEBUG: CartRepository.insertCartItem - Cake ID: ${cartItem.cakeId}, Quantity: ${cartItem.quantity}, User ID: ${cartItem.userId}")
            val docRef = cartItemsCollection.add(cartItem).await()
            println("DEBUG: CartRepository.insertCartItem - Document added with ID: ${docRef.id}")
        } catch (e: Exception) {
            println("DEBUG: Error inserting cart item: ${e.message}")
            e.printStackTrace()
            throw e
        }
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

    suspend fun getAllCartItemsForUser(userId: String): List<CartItem> {
        try {
            println("DEBUG: CartRepository.getAllCartItemsForUser - User ID: $userId")
            val snapshot = cartItemsCollection.whereEqualTo("userId", userId).get().await()
            val items = snapshot.documents.mapNotNull { doc ->
                println("DEBUG: Raw cart item from Firestore - ID: ${doc.id}, Data: ${doc.data}")
                println("DEBUG: Document data types - userId: ${doc.data?.get("userId")?.javaClass}, cakeId: ${doc.data?.get("cakeId")?.javaClass}, quantity: ${doc.data?.get("quantity")?.javaClass}")
                
                val cartItem = doc.toObject(CartItem::class.java)?.copy(id = doc.id)
                println("DEBUG: Parsed cart item - ID: ${cartItem?.id}, Cake ID: ${cartItem?.cakeId} (type: ${cartItem?.cakeId?.javaClass}), Quantity: ${cartItem?.quantity}, User ID: ${cartItem?.userId}")
                cartItem
            }
            println("DEBUG: CartRepository.getAllCartItemsForUser - Found ${items.size} items")
            items.forEach { item ->
                println("DEBUG: Cart item - ID: ${item.id}, Cake ID: ${item.cakeId}, Quantity: ${item.quantity}, User ID: ${item.userId}")
            }
            return items
        } catch (e: Exception) {
            println("DEBUG: Error in getAllCartItemsForUser: ${e.message}")
            e.printStackTrace()
            return emptyList()
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
                println("DEBUG: Updated existing cart item - ID: ${existingItem.id}, New quantity: ${updatedItem.quantity}")
            } else {
                // Add new item
                val newItem = CartItem(
                    cakeId = cakeId,
                    quantity = quantity,
                    userId = userId
                )
                insertCartItem(newItem)
                println("DEBUG: Added new cart item - Cake ID: $cakeId, Quantity: $quantity")
            }
            println("DEBUG: CartRepository.addToCart completed successfully")
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
            println("DEBUG: CartRepository.getCartItemsWithCakes - User ID: $userId")
            
            // Use the direct query function for better debugging
            val cartItems = getAllCartItemsForUser(userId)
            println("DEBUG: CartRepository.getCartItemsWithCakes - Found ${cartItems.size} cart items")
            
            val cakeRepository = CakeRepository.getInstance()
            // Use direct method instead of Flow to avoid conflicts
            val cakes = cakeRepository.getAllCakesDirect()
            println("DEBUG: CartRepository.getCartItemsWithCakes - Found ${cakes.size} cakes")
            
            // Debug: Print cart item IDs and cake IDs for comparison
            println("DEBUG: Available cake IDs: ${cakes.map { it.id }}")
            println("DEBUG: Cart item cake IDs: ${cartItems.map { it.cakeId }}")
            
            val result = cartItems.mapNotNull { cartItem ->
                println("DEBUG: Processing cart item - ID: ${cartItem.id}, Cake ID: ${cartItem.cakeId}, User ID: ${cartItem.userId}")
                val cake = cakes.find { it.id == cartItem.cakeId }
                if (cake != null) {
                    println("DEBUG: Matched cart item cake ID ${cartItem.cakeId} with cake ${cake.name}")
                    CartItemWithCake(cartItem, cake)
                } else {
                    println("DEBUG: No cake found for cart item cake ID ${cartItem.cakeId}")
                    println("DEBUG: Available cake IDs: ${cakes.map { it.id }}")
                    null
                }
            }
            
            println("DEBUG: CartRepository.getCartItemsWithCakes - Returning ${result.size} cart items with cakes")
            return result
        } catch (e: Exception) {
            println("DEBUG: CartRepository.getCartItemsWithCakes error: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun diagnoseCartItems(userId: String) {
        try {
            println("=== CART DIAGNOSTICS ===")
            println("DEBUG: Diagnosing cart items for user: $userId")
            
            // Get raw cart items
            val snapshot = cartItemsCollection.whereEqualTo("userId", userId).get().await()
            println("DEBUG: Found ${snapshot.documents.size} cart items in Firestore")
            
            snapshot.documents.forEach { doc ->
                println("DEBUG: Document ID: ${doc.id}")
                println("DEBUG: Raw data: ${doc.data}")
                doc.data?.forEach { (key, value) ->
                    println("DEBUG: Field '$key' = $value (type: ${value?.javaClass})")
                }
                
                // Try to parse as CartItem
                val cartItem = doc.toObject(CartItem::class.java)?.copy(id = doc.id)
                println("DEBUG: Parsed CartItem: $cartItem")
                if (cartItem != null) {
                    println("DEBUG: CartItem.cakeId = ${cartItem.cakeId} (type: ${cartItem.cakeId.javaClass})")
                }
                println("---")
            }
            
            // Get cakes for comparison
            val cakeRepository = CakeRepository.getInstance()
            val cakes = cakeRepository.getAllCakes().first()
            println("DEBUG: Found ${cakes.size} cakes")
            cakes.forEach { cake ->
                println("DEBUG: Cake ID: ${cake.id} (type: ${cake.id.javaClass}), Name: ${cake.name}")
            }
            
            println("=== END CART DIAGNOSTICS ===")
        } catch (e: Exception) {
            println("DEBUG: Error in diagnoseCartItems: ${e.message}")
            e.printStackTrace()
        }
    }
} 