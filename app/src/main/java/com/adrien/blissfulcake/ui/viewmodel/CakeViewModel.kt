package com.adrien.blissfulcake.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrien.blissfulcake.data.model.Cake
import com.adrien.blissfulcake.data.repository.CakeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CakeViewModel(
    private val cakeRepository: CakeRepository
) : ViewModel() {
    
    private val _cakes = MutableStateFlow<List<Cake>>(emptyList())
    val cakes: StateFlow<List<Cake>> = _cakes.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    companion object {
        private const val TAG = "CakeViewModel"
    }
    
    init {
        Log.d(TAG, "CakeViewModel initialized")
        loadCakes()
    }
    
    fun loadCakes() {
        Log.d(TAG, "Loading cakes from repository")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                cakeRepository.getAllCakes().collect { cakes ->
                    Log.d(TAG, "Received ${cakes.size} cakes from repository")
                    _cakes.value = cakes
                    cakes.forEach { cake ->
                        Log.d(TAG, "Cake: ${cake.name} (ID: ${cake.id}, Category: ${cake.category})")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading cakes: ${e.message}", e)
                _cakes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectCategory(category: String) {
        Log.d(TAG, "Selecting category: $category")
        _selectedCategory.value = category
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (category == "All") {
                    Log.d(TAG, "Loading all cakes")
                    cakeRepository.getAllCakes().collect { cakes ->
                        Log.d(TAG, "Received ${cakes.size} cakes for 'All' category")
                        _cakes.value = cakes
                        _isLoading.value = false
                    }
                } else {
                    Log.d(TAG, "Loading cakes for category: $category")
                    cakeRepository.getCakesByCategory(category).collect { cakes ->
                        Log.d(TAG, "Received ${cakes.size} cakes for category: $category")
                        _cakes.value = cakes
                        _isLoading.value = false
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading cakes by category: ${e.message}", e)
                _cakes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
} 