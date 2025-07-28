package com.adrien.blissfulcake.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.adrien.blissfulcake.data.model.Cake
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

class CakeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val cakesCollection = db.collection("cakes")

    fun getAllCakes(): Flow<List<Cake>> = callbackFlow {
        val listener = cakesCollection.whereEqualTo("isAvailable", true)
            .addSnapshotListener { snapshot, _ ->
                val cakes = snapshot?.documents?.mapNotNull { it.toObject<Cake>() } ?: emptyList()
                trySend(cakes)
            }
        awaitClose { listener.remove() }
    }

    fun getCakesByCategory(category: String): Flow<List<Cake>> = callbackFlow {
        val listener = cakesCollection
            .whereEqualTo("category", category)
            .whereEqualTo("isAvailable", true)
            .addSnapshotListener { snapshot, _ ->
                val cakes = snapshot?.documents?.mapNotNull { it.toObject<Cake>() } ?: emptyList()
                trySend(cakes)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getCakeById(cakeId: Int): Cake? {
        val snapshot = cakesCollection.whereEqualTo("id", cakeId).get().await()
        return snapshot.documents.firstOrNull()?.toObject(Cake::class.java)
    }

    suspend fun insertCake(cake: Cake) {
        cakesCollection.document(cake.id.toString()).set(cake).await()
    }

    suspend fun updateCake(cake: Cake) {
        cakesCollection.document(cake.id.toString()).set(cake).await()
    }

    suspend fun deleteCake(cake: Cake) {
        cakesCollection.document(cake.id.toString()).delete().await()
    }
} 