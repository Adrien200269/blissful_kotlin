package com.example.blissfulcakes.data.repository

import com.example.blissfulcakes.data.dao.CakeDao
import com.example.blissfulcakes.data.model.Cake
import kotlinx.coroutines.flow.Flow

class CakeRepository(
    private val cakeDao: CakeDao
) {
    fun getAllCakes(): Flow<List<Cake>> {
        return cakeDao.getAllCakes()
    }
    
    fun getCakesByCategory(category: String): Flow<List<Cake>> {
        return cakeDao.getCakesByCategory(category)
    }
    
    suspend fun getCakeById(cakeId: Int): Cake? {
        return cakeDao.getCakeById(cakeId)
    }
    
    suspend fun insertCake(cake: Cake): Long {
        return cakeDao.insertCake(cake)
    }
    
    suspend fun updateCake(cake: Cake) {
        cakeDao.updateCake(cake)
    }
    
    suspend fun deleteCake(cake: Cake) {
        cakeDao.deleteCake(cake)
    }
} 