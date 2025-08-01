package com.adrien.blissfulcake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrien.blissfulcake.data.model.Cake
import com.adrien.blissfulcake.data.repository.FavoritesRepository
import com.adrien.blissfulcake.data.repository.FavoriteItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    
    private val _favorites = MutableStateFlow<List<FavoriteItem>>(emptyList())
    val favorites: StateFlow<List<FavoriteItem>> = _favorites.asStateFlow()
    
    private val _favoriteCakeIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteCakeIds: StateFlow<Set<Int>> = _favoriteCakeIds.asStateFlow()
    
    fun loadFavorites(userId: String) {
        viewModelScope.launch {
            try {
                val favoritesWithCakes = favoritesRepository.getFavoritesWithCakes(userId)
                _favorites.value = favoritesWithCakes
                _favoriteCakeIds.value = favoritesWithCakes.map { it.cakeId }.toSet()
            } catch (e: Exception) {
                _favorites.value = emptyList()
                _favoriteCakeIds.value = emptySet()
            }
        }
    }
    
    fun toggleFavorite(userId: String, cakeId: Int) {
        viewModelScope.launch {
            try {
                println("DEBUG: FavoritesViewModel.toggleFavorite called - User ID: $userId, Cake ID: $cakeId")
                val isCurrentlyFavorite = _favoriteCakeIds.value.contains(cakeId)
                if (isCurrentlyFavorite) {
                    favoritesRepository.removeFromFavorites(userId, cakeId)
                    println("DEBUG: Removed from favorites")
                } else {
                    favoritesRepository.addToFavorites(userId, cakeId)
                    println("DEBUG: Added to favorites")
                }
                // Reload favorites to update UI
                loadFavorites(userId)
            } catch (e: Exception) {
                println("DEBUG: Error toggling favorite: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun addToFavorites(userId: String, cakeId: Int) {
        viewModelScope.launch {
            favoritesRepository.addToFavorites(userId, cakeId)
            loadFavorites(userId)
        }
    }
    
    fun removeFromFavorites(userId: String, cakeId: Int) {
        viewModelScope.launch {
            favoritesRepository.removeFromFavorites(userId, cakeId)
            loadFavorites(userId)
        }
    }
    
    fun clearFavorites(userId: String) {
        viewModelScope.launch {
            favoritesRepository.clearFavorites(userId)
            loadFavorites(userId)
        }
    }
    
    fun isFavorite(cakeId: Int): Boolean {
        return _favoriteCakeIds.value.contains(cakeId)
    }
} 