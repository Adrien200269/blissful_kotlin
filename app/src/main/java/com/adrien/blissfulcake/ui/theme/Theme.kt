package com.adrien.blissfulcake.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Consistent Light Theme Colors - Pink/Purple Theme
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE91E63), // Pink
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFCE4EC),
    onPrimaryContainer = Color(0xFFE91E63),
    secondary = Color(0xFF9C27B0), // Purple
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E5F5),
    onSecondaryContainer = Color(0xFF9C27B0),
    tertiary = Color(0xFFE91E63), // Same as primary for consistency
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFCE4EC),
    onTertiaryContainer = Color(0xFFE91E63),
    background = Color(0xFFFFF8F8),
    onBackground = Color(0xFF424242),
    surface = Color.White,
    onSurface = Color(0xFF424242),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF757575),
    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFCCCCCC)
)

// Consistent Dark Theme Colors - Pink/Purple Theme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF4081), // Light Pink
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF2D1B2E),
    onPrimaryContainer = Color(0xFFFF4081),
    secondary = Color(0xFFCE93D8), // Light Purple
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2D1B2E),
    onSecondaryContainer = Color(0xFFCE93D8),
    tertiary = Color(0xFFFF4081), // Same as primary for consistency
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF2D1B2E),
    onTertiaryContainer = Color(0xFFFF4081),
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF424242),
    outlineVariant = Color(0xFF616161)
)

@Composable
fun BlissfulCakesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to maintain consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}