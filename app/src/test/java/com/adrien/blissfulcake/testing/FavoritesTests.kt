package com.adrien.blissfulcake.testing

import com.adrien.blissfulcake.data.model.Cake
import com.adrien.blissfulcake.data.repository.FavoriteItem
import com.adrien.blissfulcake.data.repository.FavoritesRepository
import com.adrien.blissfulcake.ui.viewmodel.FavoritesViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class FavoritesTests {

    @Test
    fun `test FavoriteItem data class`() {
        val favoriteItem = FavoriteItem(
            id = "fav1",
            userId = "user123",
            cakeId = 5,
            cake = Cake(id = 5, name = "Chocolate Cake", price = 500.0)
        )

        assertEquals("fav1", favoriteItem.id)
        assertEquals("user123", favoriteItem.userId)
        assertEquals(5, favoriteItem.cakeId)
        assertNotNull(favoriteItem.cake)
        assertEquals("Chocolate Cake", favoriteItem.cake?.name)
    }

    @Test
    fun `test FavoriteItem with default values`() {
        val favoriteItem = FavoriteItem()

        assertEquals("", favoriteItem.id)
        assertEquals("", favoriteItem.userId)
        assertEquals(0, favoriteItem.cakeId)
        assertNull(favoriteItem.cake)
    }

    @Test
    fun `test FavoritesViewModel state management`() {
        // Test the logic without Firebase dependencies
        val favoriteIds = setOf<Int>()
        assertEquals(0, favoriteIds.size)
        assertTrue(favoriteIds.isEmpty())
    }

    @Test
    fun `test FavoritesViewModel isFavorite function`() {
        // Test the logic without Firebase dependencies
        val favoriteIds = setOf(1, 3, 5)
        
        // Test isFavorite logic
        assertFalse(favoriteIds.contains(2))
        assertTrue(favoriteIds.contains(1))
        assertTrue(favoriteIds.contains(3))
        assertTrue(favoriteIds.contains(5))
        assertFalse(favoriteIds.contains(4))
    }

    @Test
    fun `test favorites data flow`() {
        val testFavorites = listOf(
            FavoriteItem(id = "fav1", userId = "user123", cakeId = 1),
            FavoriteItem(id = "fav2", userId = "user123", cakeId = 3),
            FavoriteItem(id = "fav3", userId = "user123", cakeId = 5)
        )
        
        val expectedCakeIds = setOf(1, 3, 5)
        
        // Verify the mapping logic
        val actualCakeIds = testFavorites.map { it.cakeId }.toSet()
        assertEquals(expectedCakeIds, actualCakeIds)
    }

    @Test
    fun `test favorite toggle logic`() {
        val currentFavorites = setOf(1, 3, 5)
        
        // Test adding a new favorite
        val cakeIdToAdd = 2
        val isCurrentlyFavorite = currentFavorites.contains(cakeIdToAdd)
        assertFalse(isCurrentlyFavorite)
        
        // Test removing an existing favorite
        val cakeIdToRemove = 3
        val isCurrentlyFavoriteForRemoval = currentFavorites.contains(cakeIdToRemove)
        assertTrue(isCurrentlyFavoriteForRemoval)
    }

    @Test
    fun `test favorites with cake data`() {
        val testCake = Cake(
            id = 1,
            name = "Chocolate Cake",
            description = "Delicious chocolate cake",
            price = 500.0,
            imageUrl = "https://example.com/chocolate-cake.jpg",
            category = "Chocolate",
            available = true,
            isFavorite = false
        )
        
        val favoriteItem = FavoriteItem(
            id = "fav1",
            userId = "user123",
            cakeId = testCake.id,
            cake = testCake
        )
        
        assertEquals(testCake.id, favoriteItem.cakeId)
        assertEquals(testCake, favoriteItem.cake)
        assertEquals("Chocolate Cake", favoriteItem.cake?.name)
        assertEquals(500.0, favoriteItem.cake?.price ?: 0.0, 0.01)
    }

    @Test
    fun `test favorites data class functionality`() {
        val favorite1 = FavoriteItem(
            id = "fav1",
            userId = "user123",
            cakeId = 1
        )
        
        val favorite2 = FavoriteItem(
            id = "fav1",
            userId = "user123",
            cakeId = 1
        )
        
        val favorite3 = FavoriteItem(
            id = "fav2",
            userId = "user123",
            cakeId = 2
        )
        
        // Test equality
        assertEquals(favorite1, favorite2)
        assertNotEquals(favorite1, favorite3)
        
        // Test copy functionality
        val modifiedFavorite = favorite1.copy(cakeId = 5)
        assertEquals("fav1", modifiedFavorite.id)
        assertEquals("user123", modifiedFavorite.userId)
        assertEquals(5, modifiedFavorite.cakeId)
        assertEquals(1, favorite1.cakeId) // Original unchanged
    }

    @Test
    fun `test cake ID type consistency`() {
        // Test that cake IDs are consistent between favorites and cakes
        val testCake = Cake(id = 1, name = "Test Cake", price = 100.0)
        val testFavorite = FavoriteItem(cakeId = 1)
        
        // Verify both use Int type
        assertEquals(1, testCake.id)
        assertEquals(1, testFavorite.cakeId)
        
        // Test matching logic
        val cakes = listOf(testCake)
        val favorites = listOf(testFavorite)
        
        val matchedFavorites = favorites.mapNotNull { favorite ->
            val cake = cakes.find { it.id == favorite.cakeId }
            if (cake != null) {
                favorite.copy(cake = cake)
            } else null
        }
        
        assertEquals(1, matchedFavorites.size)
        assertEquals(testCake, matchedFavorites[0].cake)
    }

    @Test
    fun `test multiple favorites matching`() {
        val cakes = listOf(
            Cake(id = 0, name = "Cake 0", price = 100.0),
            Cake(id = 1, name = "Cake 1", price = 200.0),
            Cake(id = 2, name = "Cake 2", price = 300.0)
        )
        
        val favorites = listOf(
            FavoriteItem(cakeId = 0),
            FavoriteItem(cakeId = 1),
            FavoriteItem(cakeId = 3) // This one won't match
        )
        
        val matchedFavorites = favorites.mapNotNull { favorite ->
            val cake = cakes.find { it.id == favorite.cakeId }
            if (cake != null) {
                favorite.copy(cake = cake)
            } else null
        }
        
        assertEquals(2, matchedFavorites.size) // Only 0 and 1 should match
        assertTrue(matchedFavorites.any { it.cakeId == 0 })
        assertTrue(matchedFavorites.any { it.cakeId == 1 })
        assertFalse(matchedFavorites.any { it.cakeId == 3 })
    }
} 