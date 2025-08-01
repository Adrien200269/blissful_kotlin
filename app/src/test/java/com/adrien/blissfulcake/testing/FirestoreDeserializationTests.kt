package com.adrien.blissfulcake.testing

import com.adrien.blissfulcake.data.model.Cake
import org.junit.Test
import org.junit.Assert.*

class FirestoreDeserializationTests {

    @Test
    fun `test Cake model with Firestore field mapping`() {
        // Simulate Firestore document data
        val firestoreData = mapOf(
            "id" to 1,
            "name" to "Chocolate Cake",
            "description" to "Rich and moist chocolate cake with chocolate frosting",
            "price" to 1200.0,
            "imageUrl" to "https://i.ibb.co/6RS6LD3Z/Whats-App-Image-2025-07-23-at-21-22-44-cb5d92d1.jpg",
            "category" to "Chocolate",
            "available" to true
        )

        // Create Cake object with the same field names as Firestore
        val cake = Cake(
            id = firestoreData["id"] as Int,
            name = firestoreData["name"] as String,
            description = firestoreData["description"] as String,
            price = firestoreData["price"] as Double,
            imageUrl = firestoreData["imageUrl"] as String,
            category = firestoreData["category"] as String,
            available = firestoreData["available"] as Boolean
        )

        // Verify all fields are correctly mapped
        assertEquals(1, cake.id)
        assertEquals("Chocolate Cake", cake.name)
        assertEquals("Rich and moist chocolate cake with chocolate frosting", cake.description)
        assertEquals(1200.0, cake.price, 0.01)
        assertEquals("https://i.ibb.co/6RS6LD3Z/Whats-App-Image-2025-07-23-at-21-22-44-cb5d92d1.jpg", cake.imageUrl)
        assertEquals("Chocolate", cake.category)
        assertTrue(cake.available)
        assertFalse(cake.isFavorite) // Default value
    }

    @Test
    fun `test Cake model with default values for Firestore deserialization`() {
        // Test that Cake can be created with no arguments (required for Firestore)
        val cake = Cake()

        assertEquals(0, cake.id)
        assertEquals("", cake.name)
        assertEquals("", cake.description)
        assertEquals(0.0, cake.price, 0.01)
        assertEquals("", cake.imageUrl)
        assertEquals("", cake.category)
        assertTrue(cake.available) // Default value
        assertFalse(cake.isFavorite) // Default value
    }

    @Test
    fun `test Cake model with partial Firestore data`() {
        // Simulate Firestore document with only some fields
        val partialData = mapOf(
            "id" to 2,
            "name" to "Vanilla Cake",
            "price" to 1000.0,
            "available" to true
        )

        // Create Cake with partial data, other fields should use defaults
        val cake = Cake(
            id = partialData["id"] as Int,
            name = partialData["name"] as String,
            price = partialData["price"] as Double,
            available = partialData["available"] as Boolean
        )

        assertEquals(2, cake.id)
        assertEquals("Vanilla Cake", cake.name)
        assertEquals("", cake.description) // Default value
        assertEquals(1000.0, cake.price, 0.01)
        assertEquals("", cake.imageUrl) // Default value
        assertEquals("", cake.category) // Default value
        assertTrue(cake.available)
        assertFalse(cake.isFavorite) // Default value
    }

    @Test
    fun `test Cake model data class functionality`() {
        val cake1 = Cake(
            id = 1,
            name = "Chocolate Cake",
            description = "Delicious chocolate cake",
            price = 500.0,
            imageUrl = "https://example.com/chocolate-cake.jpg",
            category = "Chocolate",
            available = true,
            isFavorite = false
        )

        val cake2 = Cake(
            id = 1,
            name = "Chocolate Cake",
            description = "Delicious chocolate cake",
            price = 500.0,
            imageUrl = "https://example.com/chocolate-cake.jpg",
            category = "Chocolate",
            available = true,
            isFavorite = false
        )

        val cake3 = Cake(
            id = 2,
            name = "Vanilla Cake",
            description = "Delicious vanilla cake",
            price = 400.0,
            imageUrl = "https://example.com/vanilla-cake.jpg",
            category = "Vanilla",
            available = true,
            isFavorite = true
        )

        // Test equality
        assertEquals(cake1, cake2)
        assertNotEquals(cake1, cake3)

        // Test copy functionality
        val modifiedCake = cake1.copy(
            price = 600.0,
            isFavorite = true
        )

        assertEquals(1, modifiedCake.id)
        assertEquals("Chocolate Cake", modifiedCake.name)
        assertEquals(600.0, modifiedCake.price, 0.01)
        assertTrue(modifiedCake.isFavorite)
        assertTrue(modifiedCake.available) // Unchanged
        assertEquals(500.0, cake1.price, 0.01) // Original unchanged
    }
} 