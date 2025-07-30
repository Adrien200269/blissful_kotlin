package com.adrien.blissfulcake.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

class ThemeManager {
    private var _themeMode by mutableStateOf(ThemeMode.SYSTEM)
    val themeMode: ThemeMode get() = _themeMode
    
    fun setThemeMode(mode: ThemeMode) {
        _themeMode = mode
    }
    
    @Composable
    fun isDarkTheme(): Boolean {
        return when (_themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
    }
}

val LocalThemeManager = staticCompositionLocalOf { ThemeManager() }

@Composable
fun rememberThemeManager(): ThemeManager {
    return LocalThemeManager.current
} 