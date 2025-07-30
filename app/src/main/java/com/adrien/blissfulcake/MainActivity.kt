package com.adrien.blissfulcake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.adrien.blissfulcake.ui.navigation.NavGraph
import com.adrien.blissfulcake.ui.theme.BlissfulCakesTheme
import com.adrien.blissfulcake.ui.theme.LocalThemeManager
import com.adrien.blissfulcake.ui.theme.ThemeManager
import com.google.firebase.FirebaseApp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        setContent {
            val themeManager = remember { ThemeManager() }
            
            CompositionLocalProvider(LocalThemeManager provides themeManager) {
                BlissfulCakesTheme(darkTheme = themeManager.isDarkTheme()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph()
                    }
                }
            }
        }
    }
}