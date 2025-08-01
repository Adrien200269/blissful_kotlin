package com.adrien.blissfulcake.data.repository

import android.util.Log
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
    
    companion object {
        private const val TAG = "CakeRepository"
        @Volatile
        private var INSTANCE: CakeRepository? = null
        
        fun getInstance(): CakeRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CakeRepository().also { INSTANCE = it }
            }
        }
    }

    fun getAllCakes(): Flow<List<Cake>> = callbackFlow {
        Log.d(TAG, "Starting to fetch all cakes from Firestore")
        try {
            val listener = cakesCollection
                .addSnapshotListener { snapshot, error ->
                    try {
                        if (error != null) {
                            Log.e(TAG, "Error fetching cakes: ${error.message}")
                            trySend(emptyList())
                            return@addSnapshotListener
                        }
                        
                        Log.d(TAG, "Snapshot received. Documents count: ${snapshot?.documents?.size ?: 0}")
                        
                        val cakes = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                Log.d(TAG, "Processing document: ${doc.id}")
                                Log.d(TAG, "Document data: ${doc.data}")
                                Log.d(TAG, "Document data types - id: ${doc.data?.get("id")?.javaClass}, name: ${doc.data?.get("name")?.javaClass}")
                                
                                val cake = doc.toObject<Cake>()
                                Log.d(TAG, "Fetched cake: ${cake?.name} (ID: ${cake?.id}, type: ${cake?.id?.javaClass})")
                                cake
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing cake document: ${e.message}")
                                null
                            }
                        } ?: emptyList()
                        
                        Log.d(TAG, "Total cakes fetched: ${cakes.size}")
                        trySend(cakes)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in snapshot listener: ${e.message}")
                        trySend(emptyList())
                    }
                }
            awaitClose { 
                Log.d(TAG, "Closing getAllCakes listener")
                listener.remove() 
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up getAllCakes listener: ${e.message}")
            trySend(emptyList())
            awaitClose { }
        }
    }
    
    // Add a simple function to get cakes without Flow for matching
    suspend fun getAllCakesDirect(): List<Cake> {
        return try {
            Log.d(TAG, "Getting cakes directly (no Flow)")
            val snapshot = cakesCollection.get().await()
            val cakes = snapshot.documents.mapNotNull { doc ->
                try {
                    Log.d(TAG, "Processing document directly: ${doc.id}")
                    val cake = doc.toObject<Cake>()
                    Log.d(TAG, "Direct fetched cake: ${cake?.name} (ID: ${cake?.id})")
                    cake
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing cake document directly: ${e.message}")
                    null
                }
            }
            Log.d(TAG, "Total cakes fetched directly: ${cakes.size}")
            cakes
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cakes directly: ${e.message}")
            emptyList()
        }
    }

    fun getCakesByCategory(category: String): Flow<List<Cake>> = callbackFlow {
        Log.d(TAG, "Starting to fetch cakes for category: $category")
        val listener = cakesCollection
            .whereEqualTo("category", category)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error fetching cakes for category $category: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                
                val cakes = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val cake = doc.toObject<Cake>()
                        Log.d(TAG, "Fetched cake for category $category: ${cake?.name}")
                        cake
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing cake document for category $category: ${e.message}")
                        null
                    }
                } ?: emptyList()
                
                Log.d(TAG, "Total cakes fetched for category $category: ${cakes.size}")
                trySend(cakes)
            }
        awaitClose { 
            Log.d(TAG, "Closing getCakesByCategory listener for category: $category")
            listener.remove() 
        }
    }

    suspend fun getCakeById(cakeId: Int): Cake? {
        return try {
            Log.d(TAG, "Fetching cake by ID: $cakeId")
            val snapshot = cakesCollection.whereEqualTo("id", cakeId).get().await()
            val cake = snapshot.documents.firstOrNull()?.toObject(Cake::class.java)
            Log.d(TAG, "Cake found by ID $cakeId: ${cake?.name}")
            cake
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching cake by ID $cakeId: ${e.message}")
            null
        }
    }

    suspend fun insertCake(cake: Cake) {
        try {
            Log.d(TAG, "Inserting cake: ${cake.name} with ID: ${cake.id}")
            cakesCollection.document(cake.id.toString()).set(cake).await()
            Log.d(TAG, "Cake inserted successfully: ${cake.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting cake ${cake.name}: ${e.message}", e)
            throw e
        }
    }

    suspend fun updateCake(cake: Cake) {
        try {
            Log.d(TAG, "Updating cake: ${cake.name}")
            cakesCollection.document(cake.id.toString()).set(cake).await()
            Log.d(TAG, "Cake updated successfully: ${cake.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating cake ${cake.name}: ${e.message}", e)
            throw e
        }
    }

    suspend fun deleteCake(cake: Cake) {
        try {
            Log.d(TAG, "Deleting cake: ${cake.name}")
            cakesCollection.document(cake.id.toString()).delete().await()
            Log.d(TAG, "Cake deleted successfully: ${cake.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting cake ${cake.name}: ${e.message}", e)
            throw e
        }
    }
    
    suspend fun checkIfCakesExist(): Boolean {
        return try {
            Log.d(TAG, "Checking if cakes exist in Firestore")
            val snapshot = cakesCollection.limit(1).get().await()
            val exists = snapshot.documents.isNotEmpty()
            Log.d(TAG, "Cakes exist check result: $exists")
            exists
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if cakes exist: ${e.message}")
            false
        }
    }
} 