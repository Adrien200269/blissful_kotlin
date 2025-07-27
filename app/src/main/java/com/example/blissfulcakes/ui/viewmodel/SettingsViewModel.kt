package com.example.blissfulcakes.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.blissfulcakes.data.util.PreferencesManager
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    val isDarkTheme: StateFlow<Boolean> = preferencesManager.isDarkTheme
    val notificationsEnabled: StateFlow<Boolean> = preferencesManager.notificationsEnabled
    val autoSyncEnabled: StateFlow<Boolean> = preferencesManager.autoSyncEnabled
    
    fun toggleDarkTheme() {
        preferencesManager.setDarkTheme(!isDarkTheme.value)
    }
    
    fun toggleNotifications() {
        preferencesManager.setNotificationsEnabled(!notificationsEnabled.value)
    }
    
    fun toggleAutoSync() {
        preferencesManager.setAutoSyncEnabled(!autoSyncEnabled.value)
    }
}