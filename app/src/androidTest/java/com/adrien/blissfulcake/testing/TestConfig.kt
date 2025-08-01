package com.adrien.blissfulcake.testing

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class TestConfig {

    @Before
    fun setup() {
        // Common setup for all tests
        setupTestEnvironment()
    }

    private fun setupTestEnvironment() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Initialize test-specific configurations
        setupTestPreferences(context)
        setupTestDatabase(context)
        setupTestNetwork()
    }

    private fun setupTestPreferences(context: android.content.Context) {
        // Clear any existing preferences
        val sharedPreferences = context.getSharedPreferences("test_prefs", android.content.Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    private fun setupTestDatabase(context: android.content.Context) {
        // Setup test database if needed
        // This would typically involve setting up an in-memory database
        // or clearing existing test data
    }

    private fun setupTestNetwork() {
        // Setup test network configurations
        // This could involve setting up mock network responses
        // or configuring test API endpoints
    }

    // Helper method to get test context
    protected fun getTestContext(): android.content.Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    // Helper method to get application context
    protected fun getApplicationContext(): android.content.Context {
        return InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
    }

    // Helper method to run on main thread
    protected fun runOnMainThread(runnable: Runnable) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(runnable)
    }

    // Helper method to wait for idle
    protected fun waitForIdle() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }
} 