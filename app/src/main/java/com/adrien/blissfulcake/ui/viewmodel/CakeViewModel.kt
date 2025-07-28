package com.adrien.blissfulcake.ui.viewmodel

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
    
    init {
        loadCakes()
        insertSampleCakes()
    }
    
    fun loadCakes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                cakeRepository.getAllCakes().collect { cakes ->
                    _cakes.value = cakes
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectCategory(category: String) {
        _selectedCategory.value = category
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (category == "All") {
                    cakeRepository.getAllCakes().collect { cakes ->
                        _cakes.value = cakes
                    }
                } else {
                    cakeRepository.getCakesByCategory(category).collect { cakes ->
                        _cakes.value = cakes
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun insertSampleCakes() {
        viewModelScope.launch {
            val sampleCakes = listOf(
                Cake(
                    name = "Chocolate Cake",
                    description = "Rich and moist chocolate cake with chocolate frosting",
                    price = 1200.0,
                    imageUrl = "chocolate_cake",
                    category = "Chocolate"
                ),
                Cake(
                    name = "Vanilla Cake",
                    description = "Classic vanilla cake with buttercream frosting",
                    price = 1000.0,
                    imageUrl = "vanilla_cake",
                    category = "Vanilla"
                ),
                Cake(
                    name = "Red Velvet Cake",
                    description = "Delicious red velvet cake with cream cheese frosting",
                    price = 1500.0,
                    imageUrl = "red_velvet_cake",
                    category = "Specialty"
                ),
                Cake(
                    name = "Strawberry Cake",
                    description = "Fresh strawberry cake with strawberry frosting",
                    price = 1300.0,
                    imageUrl = "strawberry_cake",
                    category = "Fruit"
                ),
                Cake(
                    name = "Black Forest Cake",
                    description = "German chocolate cake with cherries and whipped cream",
                    price = 1800.0,
                    imageUrl = "black_forest_cake",
                    category = "Specialty"
                ),
                Cake(
                    name = "Carrot Cake",
                    description = "Moist carrot cake with cream cheese frosting",
                    price = 1100.0,
                    imageUrl = "carrot_cake",
                    category = "Specialty"
                )
            )
            
            sampleCakes.forEach { cake ->
                try {
                    cakeRepository.insertCake(cake)
                } catch (e: Exception) {
                    // Cake might already exist
                }
            }
        }
    }
} 