package com.adrien.blissfulcake.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrien.blissfulcake.data.model.User
import com.adrien.blissfulcake.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuthException
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
                    _authState.value = AuthState.Error("Login failed: User not found in database after authentication. If you registered before, please register again.")
                }
            } catch (e: com.google.firebase.auth.FirebaseAuthException) {
                val errorMessage = when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Please enter a valid email address"
                    "ERROR_WRONG_PASSWORD" -> "Incorrect password"
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email address"
                    "ERROR_USER_DISABLED" -> "This account has been disabled"
                    "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Try again later"
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Network error: Please check your internet connection and try again"
                    else -> "Login failed: ${e.message}"
                }
                _authState.value = AuthState.Error(errorMessage)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login failed: ${e.message}")
            }
        }
    }
    
    fun register(name: String, email: String, password: String, phone: String = "") {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Validate input
                if (name.isBlank()) {
                    _authState.value = AuthState.Error("Name is required")
                    return@launch
                }
                if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    _authState.value = AuthState.Error("Please enter a valid email address")
                    return@launch
                }
                if (password.length < 6) {
                    _authState.value = AuthState.Error("Password must be at least 6 characters")
                    return@launch
                }
                
                val userId = userRepository.register(name.trim(), email.trim().lowercase(), password)
                val newUser = User(
                    id = userId,
                    name = name.trim(),
                    email = email.trim().lowercase(),
                    phone = phone.trim()
                )
                _currentUser.value = newUser
                _authState.value = AuthState.Success(newUser)
            } catch (e: FirebaseAuthException) {
                val errorMessage = when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Please enter a valid email address"
                    "ERROR_WEAK_PASSWORD" -> "Password is too weak. Use at least 6 characters"
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists"
                    "ERROR_OPERATION_NOT_ALLOWED" -> "Email/password sign up is not enabled"
                    "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Try again later"
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Network error: Please check your internet connection and try again"
                    else -> "Registration failed: ${e.message}"
                }
                _authState.value = AuthState.Error(errorMessage)
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
        userRepository.logout()
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