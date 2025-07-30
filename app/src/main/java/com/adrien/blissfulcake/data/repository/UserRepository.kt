package com.adrien.blissfulcake.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.adrien.blissfulcake.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = db.collection("users")
    
    companion object {
        private const val TAG = "UserRepository"
    }

    // Test Firebase connectivity
    suspend fun testFirebaseConnection(): Boolean {
        return try {
            Log.d(TAG, "Testing Firebase connection...")
            
            // Test Firestore connection
            val testDoc = usersCollection.document("connection_test")
            testDoc.set(mapOf(
                "test" to "connection",
                "timestamp" to System.currentTimeMillis()
            )).await()
            Log.d(TAG, "Firestore write test successful")
            
            // Test Firestore read
            val snapshot = testDoc.get().await()
            Log.d(TAG, "Firestore read test successful: ${snapshot.data}")
            
            // Clean up
            testDoc.delete().await()
            Log.d(TAG, "Firebase connection test completed successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Firebase connection test failed: ${e.message}", e)
            false
        }
    }

    suspend fun login(email: String, password: String): User? {
        return try {
            val cleanEmail = email.trim().lowercase()
            Log.d(TAG, "Attempting login for email: $cleanEmail")
            
            // Test connection first
            if (!testFirebaseConnection()) {
                Log.e(TAG, "Firebase connection test failed during login")
                throw Exception("Network error: Unable to connect to Firebase")
            }
            
            val result = auth.signInWithEmailAndPassword(cleanEmail, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                Log.d(TAG, "Login successful for user: ${firebaseUser.uid}")
                
                // Try to get user data from Firestore using Firebase UID first
                var userDoc = usersCollection.document(firebaseUser.uid).get().await()
                var user = userDoc.toObject(User::class.java)
                
                // If not found by UID, try to find by email (for old users)
                if (user == null) {
                    Log.d(TAG, "User not found by UID, trying to find by email for migration: $cleanEmail")
                    val emailQuery = usersCollection.get().await()
                    val matchingDocs = emailQuery.documents.filter {
                        val docEmail = (it.getString("email") ?: "").trim().lowercase()
                        Log.d(TAG, "Checking Firestore doc id=${it.id} email=$docEmail against $cleanEmail")
                        docEmail == cleanEmail
                    }
                    Log.d(TAG, "Found ${matchingDocs.size} matching docs for email $cleanEmail")
                    val oldDoc = matchingDocs.firstOrNull()
                    user = oldDoc?.toObject(User::class.java)
                    
                    // If found by email, migrate the document to use Firebase UID
                    if (user != null && oldDoc != null) {
                        Log.d(TAG, "Found user by email, migrating to use Firebase UID. Old doc id: ${oldDoc.id}")
                        val migratedUser = user.copy(
                            id = firebaseUser.uid,
                            email = firebaseUser.email ?: cleanEmail
                        )
                        usersCollection.document(firebaseUser.uid).set(migratedUser).await()
                        // Optionally, delete the old document
                        usersCollection.document(oldDoc.id).delete().await()
                        user = migratedUser
                        Log.d(TAG, "Migration complete for user $cleanEmail")
                    } else {
                        Log.d(TAG, "No matching Firestore user found for migration for $cleanEmail")
                        
                        // Create a basic user document for Firebase Auth users without Firestore data
                        Log.d(TAG, "Creating basic user document for Firebase Auth user: ${firebaseUser.uid}")
                        val basicUser = User(
                            id = firebaseUser.uid,
                            email = firebaseUser.email ?: cleanEmail,
                            name = firebaseUser.displayName ?: "User",
                            phone = ""
                        )
                        try {
                            usersCollection.document(firebaseUser.uid).set(basicUser).await()
                            Log.d(TAG, "Basic user document created successfully")
                            user = basicUser
                        } catch (createError: Exception) {
                            Log.e(TAG, "Failed to create basic user document: ${createError.message}")
                            // Return a basic user object anyway so login can succeed
                            user = basicUser
                        }
                    }
                } else {
                    // Update email if it's different
                    user = user.copy(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: cleanEmail
                    )
                }
                
                Log.d(TAG, "Retrieved user data from Firestore: $user")
                user
            } else {
                Log.e(TAG, "Login failed: Firebase user is null")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error: ${e.message}", e)
            null
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return try {
            Log.d(TAG, "Getting user by email: $email")
            val snapshot = usersCollection.whereEqualTo("email", email).get().await()
            val user = snapshot.documents.firstOrNull()?.toObject(User::class.java)
            Log.d(TAG, "Found user: $user")
            user
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user by email: ${e.message}", e)
            null
        }
    }

    suspend fun insertUser(user: User) {
        try {
            Log.d(TAG, "Inserting user: $user")
            usersCollection.document(user.id).set(user).await()
            Log.d(TAG, "User inserted successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting user: ${e.message}", e)
            throw e
        }
    }

    suspend fun updateUser(user: User) {
        try {
            Log.d(TAG, "Updating user: $user")
            usersCollection.document(user.id).set(user).await()
            Log.d(TAG, "User updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user: ${e.message}", e)
            throw e
        }
    }

    suspend fun register(name: String, email: String, password: String): String {
        return try {
            Log.d(TAG, "Starting registration for email: $email")
            
            // Test Firebase connection first
            if (!testFirebaseConnection()) {
                Log.e(TAG, "Firebase connection test failed during registration")
                throw Exception("Network error: Unable to connect to Firebase. Please check your internet connection and try again.")
            }
            
            // First, create Firebase Auth user
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                Log.d(TAG, "Firebase Auth user created successfully: ${firebaseUser.uid}")
                
                // Create user document in Firestore with Firebase UID
                val userWithFirebaseId = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email
                )
                
                Log.d(TAG, "Preparing to store user data in Firestore: $userWithFirebaseId")
                Log.d(TAG, "Firestore document ID will be: ${firebaseUser.uid}")
                
                try {
                    usersCollection.document(firebaseUser.uid).set(userWithFirebaseId).await()
                    Log.d(TAG, "User data stored in Firestore successfully")
                    
                    // Verify the document was created
                    val verificationDoc = usersCollection.document(firebaseUser.uid).get().await()
                    if (verificationDoc.exists()) {
                        Log.d(TAG, "Firestore document verification successful")
                    } else {
                        Log.e(TAG, "Firestore document verification failed - document does not exist")
                    }
                } catch (firestoreError: Exception) {
                    Log.e(TAG, "Error storing user data in Firestore: ${firestoreError.message}", firestoreError)
                    // Try to delete the Firebase Auth user since Firestore failed
                    try {
                        firebaseUser.delete().await()
                        Log.d(TAG, "Deleted Firebase Auth user due to Firestore failure")
                    } catch (deleteError: Exception) {
                        Log.e(TAG, "Error deleting Firebase Auth user: ${deleteError.message}")
                    }
                    throw Exception("Network error: Failed to save user data. Please try again.")
                }
                
                firebaseUser.uid
            } else {
                Log.e(TAG, "Failed to create Firebase user")
                throw Exception("Failed to create user account. Please try again.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration error: ${e.message}", e)
            throw e
        }
    }

    fun getUserById(userId: String): Flow<User?> = callbackFlow {
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error getting user by ID: ${error.message}")
                    trySend(null)
                } else {
                    val user = snapshot?.toObject(User::class.java)
                    Log.d(TAG, "User by ID result: $user")
                    trySend(user)
                }
            }
        awaitClose { listener.remove() }
    }

    fun getCurrentUser(): FirebaseUser? {
        val user = auth.currentUser
        Log.d(TAG, "Current Firebase user: ${user?.uid}")
        return user
    }

    fun isUserLoggedIn(): Boolean {
        val isLoggedIn = auth.currentUser != null
        Log.d(TAG, "User logged in: $isLoggedIn")
        return isLoggedIn
    }

    fun logout() {
        Log.d(TAG, "Logging out user")
        auth.signOut()
    }
} 