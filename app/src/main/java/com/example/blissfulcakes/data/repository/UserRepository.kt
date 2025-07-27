package com.example.blissfulcakes.data.repository

import com.example.blissfulcakes.data.dao.UserDao
import com.example.blissfulcakes.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }
    
    suspend fun register(user: User): Long {
        return userDao.insertUser(user)
    }
    
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
    
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
    
    fun getUserById(userId: Int): Flow<User?> {
        return userDao.getUserById(userId)
    }
} 