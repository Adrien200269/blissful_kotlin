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
    
    private val _isAddingCakes = MutableStateFlow(false)
    val isAddingCakes: StateFlow<Boolean> = _isAddingCakes.asStateFlow()
    
    companion object {
        private const val TAG = "CakeViewModel"
    }
    
    init {
        Log.d(TAG, "CakeViewModel initialized")
        loadCakes()
        checkAndAddSampleCakes()
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
                    }
                } else {
                    Log.d(TAG, "Loading cakes for category: $category")
                    cakeRepository.getCakesByCategory(category).collect { cakes ->
                        Log.d(TAG, "Received ${cakes.size} cakes for category: $category")
                        _cakes.value = cakes
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading cakes by category: ${e.message}", e)
                _cakes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addTestCakes() {
        Log.d(TAG, "Adding test cakes to Firebase")
        viewModelScope.launch {
            _isAddingCakes.value = true
            try {
                cakeRepository.addTestCakes()
                Log.d(TAG, "Test cakes added successfully")
                // Reload cakes after adding
                loadCakes()
            } catch (e: Exception) {
                Log.e(TAG, "Error adding test cakes: ${e.message}", e)
            } finally {
                _isAddingCakes.value = false
            }
        }
    }
    
    fun testFirestoreConnection() {
        Log.d(TAG, "Testing Firestore connection")
        viewModelScope.launch {
            try {
                cakeRepository.testFirestoreConnection()
                Log.d(TAG, "Firestore connection test successful")
            } catch (e: Exception) {
                Log.e(TAG, "Firestore connection test failed: ${e.message}", e)
            }
        }
    }
    
    fun listAllCakesInFirestore() {
        Log.d(TAG, "Listing all cakes in Firestore")
        viewModelScope.launch {
            try {
                cakeRepository.listAllCakesInFirestore()
            } catch (e: Exception) {
                Log.e(TAG, "Error listing cakes in Firestore: ${e.message}", e)
            }
        }
    }
    
    fun addSampleCakesToFirebase() {
        Log.d(TAG, "Adding sample cakes to Firebase")
        viewModelScope.launch {
            _isAddingCakes.value = true
            try {
                cakeRepository.addSampleCakesToFirebase()
                Log.d(TAG, "Sample cakes added successfully")
                // Reload cakes after adding
                loadCakes()
            } catch (e: Exception) {
                Log.e(TAG, "Error adding sample cakes: ${e.message}", e)
            } finally {
                _isAddingCakes.value = false
            }
        }
    }
    
    private fun checkAndAddSampleCakes() {
        Log.d(TAG, "Checking if sample cakes need to be added")
        viewModelScope.launch {
            try {
                val cakesExist = cakeRepository.checkIfCakesExist()
                Log.d(TAG, "Cakes exist check result: $cakesExist")
                if (!cakesExist) {
                    Log.d(TAG, "No cakes found in Firebase, adding sample cakes")
                    addSampleCakesToFirebase()
                } else {
                    Log.d(TAG, "Cakes already exist in Firebase")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking cakes: ${e.message}", e)
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