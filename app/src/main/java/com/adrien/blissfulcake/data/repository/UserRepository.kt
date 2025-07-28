package com.adrien.blissfulcake.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.adrien.blissfulcake.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun login(email: String, password: String): User? {
        val snapshot = usersCollection
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get().await()
        return snapshot.documents.firstOrNull()?.toObject(User::class.java)
    }

    suspend fun getUserByEmail(email: String): User? {
        val snapshot = usersCollection.whereEqualTo("email", email).get().await()
        return snapshot.documents.firstOrNull()?.toObject(User::class.java)
    }

    suspend fun insertUser(user: User) {
        usersCollection.document(user.id.toString()).set(user).await()
    }

    suspend fun updateUser(user: User) {
        usersCollection.document(user.id.toString()).set(user).await()
    }

    suspend fun register(user: User): Long {
        // Generate a new document with auto ID
        val docRef = usersCollection.document()
        val userWithId = user.copy(id = docRef.id.hashCode())
        docRef.set(userWithId).await()
        return userWithId.id.toLong()
    }

    fun getUserById(userId: Int): Flow<User?> = callbackFlow {
        val listener = usersCollection.whereEqualTo("id", userId)
            .addSnapshotListener { snapshot, _ ->
                val user = snapshot?.documents?.firstOrNull()?.toObject(User::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }
} 