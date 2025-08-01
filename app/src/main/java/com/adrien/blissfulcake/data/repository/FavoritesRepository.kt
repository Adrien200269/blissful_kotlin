package com.adrien.blissfulcake.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.adrien.blissfulcake.data.model.Cake
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import com.adrien.blissfulcake.data.repository.CakeRepository

data class FavoriteItem(
    val id: String = "",
    val userId: String = "",
    val cakeId: Int = 0,
    val cake: Cake? = null
)

class FavoritesRepository {
    private val db = FirebaseFirestore.getInstance()
    private val favoritesCollection = db.collection("favorites")

    fun getFavoritesByUserId(userId: String): Flow<List<FavoriteItem>> = callbackFlow {
        println("DEBUG: FavoritesRepository.getFavoritesByUserId - User ID: $userId")
        val listener = favoritesCollection.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("DEBUG: Error getting favorites: ${error.message}")
                } else {
                    val items = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(FavoriteItem::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
                    println("DEBUG: FavoritesRepository - Found ${items.size} favorites for user $userId")
                    items.forEach { item ->
                        println("DEBUG: Favorite item - ID: ${item.id}, Cake ID: ${item.cakeId}")
                    }
                    trySend(items)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun getFavoritesWithCakes(userId: String): List<FavoriteItem> {
        try {
            println("DEBUG: FavoritesRepository.getFavoritesWithCakes - User ID: $userId")
            val favorites = getFavoritesByUserId(userId).first()
            println("DEBUG: FavoritesRepository.getFavoritesWithCakes - Found ${favorites.size} favorites")
            
            // Debug: Print raw favorite data
            favorites.forEach { favorite ->
                println("DEBUG: Favorite item - ID: ${favorite.id}, Cake ID: ${favorite.cakeId} (type: ${favorite.cakeId.javaClass}), User ID: ${favorite.userId}")
            }
            
            val cakeRepository = CakeRepository.getInstance()
            // Use direct method instead of Flow to avoid conflicts
            val cakes = cakeRepository.getAllCakesDirect()
            println("DEBUG: FavoritesRepository.getFavoritesWithCakes - Found ${cakes.size} cakes")
            
            // Debug: Print cake data
            cakes.forEach { cake ->
                println("DEBUG: Cake - ID: ${cake.id} (type: ${cake.id.javaClass}), Name: ${cake.name}")
            }
            
            // Debug: Print cake IDs for comparison
            println("DEBUG: Available cake IDs: ${cakes.map { it.id }}")
            println("DEBUG: Favorite cake IDs: ${favorites.map { it.cakeId }}")
            
            val result = favorites.mapNotNull { favorite ->
                println("DEBUG: Processing favorite - Cake ID: ${favorite.cakeId} (type: ${favorite.cakeId.javaClass})")
                val cake = cakes.find { it.id == favorite.cakeId }
                if (cake != null) {
                    println("DEBUG: Matched favorite cake ID ${favorite.cakeId} with cake ${cake.name}")
                    favorite.copy(cake = cake)
                } else {
                    println("DEBUG: No cake found for favorite cake ID ${favorite.cakeId}")
                    println("DEBUG: Available cake IDs: ${cakes.map { it.id }}")
                    null
                }
            }
            
            println("DEBUG: FavoritesRepository.getFavoritesWithCakes - Returning ${result.size} favorites with cakes")
            return result
        } catch (e: Exception) {
            println("DEBUG: FavoritesRepository.getFavoritesWithCakes error: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun addToFavorites(userId: String, cakeId: Int) {
        try {
            println("DEBUG: FavoritesRepository.addToFavorites - User ID: $userId, Cake ID: $cakeId")
            val favoriteItem = FavoriteItem(
                userId = userId,
                cakeId = cakeId
            )
            favoritesCollection.add(favoriteItem).await()
            println("DEBUG: Added to favorites successfully")
        } catch (e: Exception) {
            println("DEBUG: Error in FavoritesRepository.addToFavorites: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun removeFromFavorites(userId: String, cakeId: Int) {
        val snapshot = favoritesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("cakeId", cakeId)
            .get().await()
        
        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }

    suspend fun isFavorite(userId: String, cakeId: Int): Boolean {
        val snapshot = favoritesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("cakeId", cakeId)
            .get().await()
        return !snapshot.isEmpty
    }

    suspend fun clearFavorites(userId: String) {
        val snapshot = favoritesCollection.whereEqualTo("userId", userId).get().await()
        for (doc in snapshot.documents) {
            doc.reference.delete().await()
        }
    }
    
    suspend fun cleanupDuplicateFavorites(userId: String) {
        try {
            println("DEBUG: Cleaning up duplicate favorites for user: $userId")
            val snapshot = favoritesCollection.whereEqualTo("userId", userId).get().await()
            val favorites = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FavoriteItem::class.java)?.copy(id = doc.id)
            }
            
            // Group by cakeId and keep only one entry per cake
            val uniqueFavorites = favorites.groupBy { it.cakeId }.mapValues { (_, items) ->
                items.first() // Keep the first entry for each cake
            }.values.toList()
            
            println("DEBUG: Found ${favorites.size} total favorites, keeping ${uniqueFavorites.size} unique favorites")
            
            // Delete all existing favorites for this user
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
            
            // Re-add only unique favorites
            for (favorite in uniqueFavorites) {
                favoritesCollection.add(favorite).await()
            }
            
            println("DEBUG: Cleanup completed successfully")
        } catch (e: Exception) {
            println("DEBUG: Error cleaning up duplicates: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun diagnoseFavorites(userId: String) {
        try {
            println("=== FAVORITES DIAGNOSTICS ===")
            println("DEBUG: Diagnosing favorites for user: $userId")
            
            // Get raw favorites
            val snapshot = favoritesCollection.whereEqualTo("userId", userId).get().await()
            println("DEBUG: Found ${snapshot.documents.size} favorites in Firestore")
            
            snapshot.documents.forEach { doc ->
                println("DEBUG: Document ID: ${doc.id}")
                println("DEBUG: Raw data: ${doc.data}")
                doc.data?.forEach { (key, value) ->
                    println("DEBUG: Field '$key' = $value (type: ${value?.javaClass})")
                }
                
                // Try to parse as FavoriteItem
                val favoriteItem = doc.toObject(FavoriteItem::class.java)?.copy(id = doc.id)
                println("DEBUG: Parsed FavoriteItem: $favoriteItem")
                if (favoriteItem != null) {
                    println("DEBUG: FavoriteItem.cakeId = ${favoriteItem.cakeId} (type: ${favoriteItem.cakeId.javaClass})")
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
            
            println("=== END FAVORITES DIAGNOSTICS ===")
        } catch (e: Exception) {
            println("DEBUG: Error in diagnoseFavorites: ${e.message}")
            e.printStackTrace()
        }
    }
} 