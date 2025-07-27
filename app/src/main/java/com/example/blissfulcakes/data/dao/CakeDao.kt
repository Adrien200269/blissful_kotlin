package com.example.blissfulcakes.data.dao

import androidx.room.*
import com.example.blissfulcakes.data.model.Cake
import kotlinx.coroutines.flow.Flow

@Dao
interface CakeDao {
    @Query("SELECT * FROM cakes WHERE isAvailable = 1")
    fun getAllCakes(): Flow<List<Cake>>
    
    @Query("SELECT * FROM cakes WHERE category = :category AND isAvailable = 1")
    fun getCakesByCategory(category: String): Flow<List<Cake>>
    
    @Query("SELECT * FROM cakes WHERE id = :cakeId")
    suspend fun getCakeById(cakeId: Int): Cake?
    
    @Insert
    suspend fun insertCake(cake: Cake): Long
    
    @Update
    suspend fun updateCake(cake: Cake)
    
    @Delete
    suspend fun deleteCake(cake: Cake)
} 