package com.adrien.blissfulcake

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adrien.blissfulcake.ui.navigation.NavGraph
import com.adrien.blissfulcake.ui.theme.BlissfulCakesTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        setContent {
            BlissfulCakesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                    FirebaseConnectionTest()
                }
            }
        }
    }
}

@Composable
private fun FirebaseConnectionTest() {
    // Test Firebase connection
    try {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        
        Log.d("MainActivity", "Firebase Auth initialized: ${auth.app.name}")
        Log.d("MainActivity", "Firebase Firestore initialized: ${firestore.app.name}")
        
        // Test Firestore connection
        firestore.collection("test").document("connection")
            .get()
            .addOnSuccessListener {
                Log.d("MainActivity", "Firebase connection test successful")
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Firebase connection test failed: ${e.message}")
            }
    } catch (e: Exception) {
        Log.e("MainActivity", "Firebase initialization error: ${e.message}", e)
    }
}