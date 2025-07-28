package com.example.blissfulcakes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blissfulcakes.data.model.User
import com.example.blissfulcakes.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = userRepository.login(email, password)
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error("Invalid email or password")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }
    
    fun register(name: String, email: String, password: String, phone: String = "") {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val existingUser = userRepository.getUserByEmail(email)
                if (existingUser != null) {
                    _authState.value = AuthState.Error("Email already registered")
                    return@launch
                }
                
                val user = User(
                    name = name,
                    email = email,
                    password = password,
                    phone = phone
                )
                
                val userId = userRepository.register(user)
                val newUser = user.copy(id = userId.toInt())
                _currentUser.value = newUser
                _authState.value = AuthState.Success(newUser)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }
    
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    // In a real app, you would send a password reset email
                    _authState.value = AuthState.Error("Password reset functionality not implemented")
                } else {
                    _authState.value = AuthState.Error("Email not found")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Password reset failed")
            }
        }
    }
    
    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Initial
    }
    
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Initial
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                val updatedUser = user.copy(name = name, email = email)
                userRepository.updateUser(updatedUser)
                _currentUser.value = updatedUser
                _authState.value = AuthState.Success(updatedUser)
            } else {
                _authState.value = AuthState.Error("No user logged in")
            }
        }
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
} 