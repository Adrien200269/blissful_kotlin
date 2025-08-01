package com.adrien.blissfulcake.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import io.mockk.mockk

class BlissfulCakesTestRunner : AndroidJUnitRunner() {
    
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize test-specific configurations here
    }
} 