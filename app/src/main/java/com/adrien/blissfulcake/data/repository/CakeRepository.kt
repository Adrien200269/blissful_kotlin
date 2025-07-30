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
    }

    fun getAllCakes(): Flow<List<Cake>> = callbackFlow {
        Log.d(TAG, "Starting to fetch all cakes from Firestore")
        val listener = cakesCollection
            .addSnapshotListener { snapshot, error ->
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
                        val cake = doc.toObject<Cake>()
                        Log.d(TAG, "Fetched cake: ${cake?.name} (ID: ${cake?.id})")
                        cake
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing cake document: ${e.message}")
                        null
                    }
                } ?: emptyList()
                
                Log.d(TAG, "Total cakes fetched: ${cakes.size}")
                trySend(cakes)
            }
        awaitClose { 
            Log.d(TAG, "Closing getAllCakes listener")
            listener.remove() 
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
    
    // Test function to manually add cakes
    suspend fun addTestCakes() {
        try {
            Log.d(TAG, "Adding test cakes to Firestore")
            
            val testCakes = listOf(
                Cake(
                    id = 1,
                    name = "Chocolate Cake",
                    description = "Rich and moist chocolate cake with chocolate frosting",
                    price = 1200.0,
                    imageUrl = "chocolate_cake",
                    category = "Chocolate",
                    isAvailable = true
                ),
                Cake(
                    id = 2,
                    name = "Vanilla Cake",
                    description = "Classic vanilla cake with buttercream frosting",
                    price = 1000.0,
                    imageUrl = "vanilla_cake",
                    category = "Vanilla",
                    isAvailable = true
                ),
                Cake(
                    id = 3,
                    name = "Red Velvet Cake",
                    description = "Delicious red velvet cake with cream cheese frosting",
                    price = 1500.0,
                    imageUrl = "red_velvet_cake",
                    category = "Specialty",
                    isAvailable = true
                )
            )
            
            for (cake in testCakes) {
                try {
                    Log.d(TAG, "Adding test cake: ${cake.name}")
                    // Use auto-generated document ID instead of cake.id
                    cakesCollection.add(cake).await()
                    Log.d(TAG, "Test cake added successfully: ${cake.name}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to add test cake ${cake.name}: ${e.message}")
                }
            }
            
            Log.d(TAG, "Test cakes addition completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding test cakes: ${e.message}", e)
            throw e
        }
    }
    
    suspend fun addSampleCakesToFirebase() {
        try {
            Log.d(TAG, "Starting to add sample cakes to Firebase")
            
            val sampleCakes = listOf(
                Cake(
                    id = 1,
                    name = "Chocolate Cake",
                    description = "Rich and moist chocolate cake with chocolate frosting",
                    price = 1200.0,
                    imageUrl = "chocolate_cake",
                    category = "Chocolate",
                    isAvailable = true
                ),
                Cake(
                    id = 2,
                    name = "Vanilla Cake",
                    description = "Classic vanilla cake with buttercream frosting",
                    price = 1000.0,
                    imageUrl = "vanilla_cake",
                    category = "Vanilla",
                    isAvailable = true
                ),
                Cake(
                    id = 3,
                    name = "Red Velvet Cake",
                    description = "Delicious red velvet cake with cream cheese frosting",
                    price = 1500.0,
                    imageUrl = "red_velvet_cake",
                    category = "Specialty",
                    isAvailable = true
                ),
                Cake(
                    id = 4,
                    name = "Strawberry Cake",
                    description = "Fresh strawberry cake with strawberry frosting",
                    price = 1300.0,
                    imageUrl = "strawberry_cake",
                    category = "Fruit",
                    isAvailable = true
                ),
                Cake(
                    id = 5,
                    name = "Black Forest Cake",
                    description = "German chocolate cake with cherries and whipped cream",
                    price = 1800.0,
                    imageUrl = "black_forest_cake",
                    category = "Specialty",
                    isAvailable = true
                ),
                Cake(
                    id = 6,
                    name = "Carrot Cake",
                    description = "Moist carrot cake with cream cheese frosting and walnuts",
                    price = 1400.0,
                    imageUrl = "carrot_cake",
                    category = "Specialty",
                    isAvailable = true
                ),
                Cake(
                    id = 7,
                    name = "Lemon Cake",
                    description = "Tangy lemon cake with lemon glaze",
                    price = 1100.0,
                    imageUrl = "lemon_cake",
                    category = "Citrus",
                    isAvailable = true
                ),
                Cake(
                    id = 8,
                    name = "Coffee Cake",
                    description = "Delicious coffee-flavored cake with coffee frosting",
                    price = 1250.0,
                    imageUrl = "coffee_cake",
                    category = "Coffee",
                    isAvailable = true
                )
            )
            
            var successCount = 0
            var errorCount = 0
            
            for (cake in sampleCakes) {
                try {
                    // Use auto-generated document ID instead of cake.id
                    cakesCollection.add(cake).await()
                    successCount++
                    Log.d(TAG, "Sample cake added successfully: ${cake.name}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to add sample cake ${cake.name}: ${e.message}")
                    errorCount++
                }
            }
            
            Log.d(TAG, "Sample cakes addition completed. Success: $successCount, Errors: $errorCount")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding sample cakes to Firebase: ${e.message}", e)
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
    
    // Test function to verify Firestore connection
    suspend fun testFirestoreConnection() {
        try {
            Log.d(TAG, "Testing Firestore connection...")
            
            // Try to write a test document
            val testDoc = cakesCollection.document("test_connection")
            testDoc.set(mapOf(
                "test" to "connection",
                "timestamp" to System.currentTimeMillis()
            )).await()
            Log.d(TAG, "Firestore write test successful")
            
            // Try to read the test document
            val snapshot = testDoc.get().await()
            Log.d(TAG, "Firestore read test successful: ${snapshot.data}")
            
            // Clean up test document
            testDoc.delete().await()
            Log.d(TAG, "Firestore connection test completed successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Firestore connection test failed: ${e.message}", e)
            throw e
        }
    }
    
    // Debug function to list all documents in cakes collection
    suspend fun listAllCakesInFirestore() {
        try {
            Log.d(TAG, "Listing all documents in cakes collection...")
            val snapshot = cakesCollection.get().await()
            Log.d(TAG, "Total documents in cakes collection: ${snapshot.documents.size}")
            
            snapshot.documents.forEach { doc ->
                Log.d(TAG, "Document ID: ${doc.id}")
                Log.d(TAG, "Document data: ${doc.data}")
                try {
                    val cake = doc.toObject<Cake>()
                    Log.d(TAG, "Parsed cake: ${cake?.name} (ID: ${cake?.id})")
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing document ${doc.id}: ${e.message}")
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error listing cakes in Firestore: ${e.message}", e)
        }
    }
} 