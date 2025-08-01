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
        val listener = favoritesCollection.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FavoriteItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getFavoritesWithCakes(userId: String): List<FavoriteItem> {
        try {
            val favorites = getFavoritesByUserId(userId).first()
            val cakeRepository = CakeRepository()
            val cakes = cakeRepository.getAllCakes().first()
            
            return favorites.mapNotNull { favorite ->
                val cake = cakes.find { it.id == favorite.cakeId }
                if (cake != null) {
                    favorite.copy(cake = cake)
                } else null
            }
        } catch (e: Exception) {
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
} 