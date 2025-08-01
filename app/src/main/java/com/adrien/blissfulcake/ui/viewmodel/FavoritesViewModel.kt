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
    
    init {
        println("DEBUG: FavoritesViewModel - Initialized")
    }
    
    private val _favorites = MutableStateFlow<List<FavoriteItem>>(emptyList())
    val favorites: StateFlow<List<FavoriteItem>> = _favorites.asStateFlow()
    
    private val _favoriteCakeIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteCakeIds: StateFlow<Set<Int>> = _favoriteCakeIds.asStateFlow()
    
    fun loadFavorites(userId: String) {
        println("DEBUG: FavoritesViewModel.loadFavorites called with userId: $userId")
        viewModelScope.launch {
            try {
                println("DEBUG: FavoritesViewModel.loadFavorites - User ID: $userId")
                val favoritesWithCakes = favoritesRepository.getFavoritesWithCakes(userId)
                println("DEBUG: FavoritesViewModel - Loaded ${favoritesWithCakes.size} favorites with cakes")
                _favorites.value = favoritesWithCakes
                val cakeIds = favoritesWithCakes.map { it.cakeId }.toSet()
                _favoriteCakeIds.value = cakeIds
                println("DEBUG: FavoritesViewModel - Updated favorite cake IDs: $cakeIds")
                println("DEBUG: FavoritesViewModel - _favorites.value updated to: ${_favorites.value.size} items")
            } catch (e: Exception) {
                println("DEBUG: FavoritesViewModel.loadFavorites error: ${e.message}")
                e.printStackTrace()
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
                
                // Update state immediately for better UX
                if (isCurrentlyFavorite) {
                    // Remove from favorites
                    favoritesRepository.removeFromFavorites(userId, cakeId)
                    println("DEBUG: Removed from favorites")
                    // Update local state immediately
                    _favoriteCakeIds.value = _favoriteCakeIds.value - cakeId
                } else {
                    // Add to favorites
                    favoritesRepository.addToFavorites(userId, cakeId)
                    println("DEBUG: Added to favorites")
                    // Update local state immediately
                    _favoriteCakeIds.value = _favoriteCakeIds.value + cakeId
                }
                
                // Reload favorites to ensure consistency
                loadFavorites(userId)
            } catch (e: Exception) {
                println("DEBUG: Error toggling favorite: ${e.message}")
                e.printStackTrace()
                // Revert state on error
                loadFavorites(userId)
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
    
    fun diagnoseFavorites(userId: String) {
        viewModelScope.launch {
            try {
                println("DEBUG: FavoritesViewModel.diagnoseFavorites called for user: $userId")
                favoritesRepository.diagnoseFavorites(userId)
            } catch (e: Exception) {
                println("DEBUG: Error in diagnoseFavorites: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun cleanupDuplicateFavorites(userId: String) {
        viewModelScope.launch {
            try {
                println("DEBUG: FavoritesViewModel.cleanupDuplicateFavorites called for user: $userId")
                favoritesRepository.cleanupDuplicateFavorites(userId)
                // Reload favorites after cleanup
                loadFavorites(userId)
            } catch (e: Exception) {
                println("DEBUG: Error in cleanupDuplicateFavorites: ${e.message}")
                e.printStackTrace()
            }
        }
    }
} 