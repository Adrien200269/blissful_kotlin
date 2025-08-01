package com.adrien.blissfulcake.testing

import com.adrien.blissfulcake.data.model.*
import org.junit.Test
import org.junit.Assert.*

class SimpleTests {

            @Test
        fun `test Cake model creation`() {
            val cake = Cake(
                id = 1,
                name = "Chocolate Cake",
                description = "Delicious chocolate cake",
                price = 500.0,
                imageUrl = "https://example.com/chocolate-cake.jpg",
                category = "Chocolate",
                available = true,
                isFavorite = false
            )

            assertEquals(1, cake.id)
            assertEquals("Chocolate Cake", cake.name)
            assertEquals("Delicious chocolate cake", cake.description)
            assertEquals(500.0, cake.price, 0.01)
            assertEquals("https://example.com/chocolate-cake.jpg", cake.imageUrl)
            assertEquals("Chocolate", cake.category)
            assertTrue(cake.available)
            assertFalse(cake.isFavorite)
        }

    @Test
    fun `test User model creation`() {
        val user = User(
            id = "user123",
            name = "John Doe",
            email = "john@example.com",
            phone = "+977-1234567890",
            photoUrl = "https://example.com/photo.jpg",
            createdAt = 1234567890L
        )

        assertEquals("user123", user.id)
        assertEquals("John Doe", user.name)
        assertEquals("john@example.com", user.email)
        assertEquals("+977-1234567890", user.phone)
        assertEquals("https://example.com/photo.jpg", user.photoUrl)
        assertEquals(1234567890L, user.createdAt)
    }

    @Test
    fun `test CartItem model creation`() {
        val cartItem = CartItem(
            id = "cart1",
            userId = "user123",
            cakeId = 5,
            quantity = 2
        )

        assertEquals("cart1", cartItem.id)
        assertEquals("user123", cartItem.userId)
        assertEquals(5, cartItem.cakeId)
        assertEquals(2, cartItem.quantity)
    }

    @Test
    fun `test CartItem model with default values`() {
        val cartItem = CartItem()

        assertEquals("", cartItem.id)
        assertEquals("", cartItem.userId)
        assertEquals(0, cartItem.cakeId)
        assertEquals(0, cartItem.quantity)
    }

    @Test
    fun `test Order model creation`() {
        val order = Order(
            id = 1,
            userId = "user123",
            customerName = "John Doe",
            customerAddress = "Kathmandu, Nepal",
            customerPhone = "+977-1234567890",
            customerNotes = "Please deliver in the morning",
            totalAmount = 1500.0,
            status = OrderStatus.PENDING
        )

        assertEquals(1, order.id)
        assertEquals("user123", order.userId)
        assertEquals("John Doe", order.customerName)
        assertEquals("Kathmandu, Nepal", order.customerAddress)
        assertEquals("+977-1234567890", order.customerPhone)
        assertEquals("Please deliver in the morning", order.customerNotes)
        assertEquals(1500.0, order.totalAmount, 0.01)
        assertEquals(OrderStatus.PENDING, order.status)
    }

    @Test
    fun `test Order model with default values`() {
        val order = Order()

        assertEquals(0, order.id)
        assertEquals("", order.userId)
        assertEquals("", order.customerName)
        assertEquals("", order.customerAddress)
        assertEquals("", order.customerPhone)
        assertEquals("", order.customerNotes)
        assertEquals(0.0, order.totalAmount, 0.01)
        assertEquals(OrderStatus.PENDING, order.status)
    }

    @Test
    fun `test OrderItem model creation`() {
        val orderItem = OrderItem(
            id = 1,
            orderId = 123,
            cakeId = 5,
            quantity = 2,
            price = 500.0
        )

        assertEquals(1, orderItem.id)
        assertEquals(123, orderItem.orderId)
        assertEquals(5, orderItem.cakeId)
        assertEquals(2, orderItem.quantity)
        assertEquals(500.0, orderItem.price, 0.01)
    }

    @Test
    fun `test OrderItem model with default values`() {
        val orderItem = OrderItem()

        assertEquals(0, orderItem.id)
        assertEquals(0, orderItem.orderId)
        assertEquals(0, orderItem.cakeId)
        assertEquals(0, orderItem.quantity)
        assertEquals(0.0, orderItem.price, 0.01)
    }

    @Test
    fun `test data class equality`() {
        val cake1 = Cake(id = 1, name = "Chocolate Cake", price = 500.0)
        val cake2 = Cake(id = 1, name = "Chocolate Cake", price = 500.0)
        val cake3 = Cake(id = 2, name = "Vanilla Cake", price = 400.0)

        assertEquals(cake1, cake2)
        assertNotEquals(cake1, cake3)
    }

    @Test
    fun `test data class copy`() {
        val originalCake = Cake(id = 1, name = "Chocolate Cake", price = 500.0)
        val modifiedCake = originalCake.copy(price = 600.0, isFavorite = true)

        assertEquals(1, modifiedCake.id)
        assertEquals("Chocolate Cake", modifiedCake.name)
        assertEquals(600.0, modifiedCake.price, 0.01)
        assertTrue(modifiedCake.isFavorite)
        assertEquals(500.0, originalCake.price, 0.01) // Original unchanged
    }

    @Test
    fun `test OrderStatus enum`() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"))
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.valueOf("CONFIRMED"))
        assertEquals(OrderStatus.PREPARING, OrderStatus.valueOf("PREPARING"))
        assertEquals(OrderStatus.READY, OrderStatus.valueOf("READY"))
        assertEquals(OrderStatus.DELIVERED, OrderStatus.valueOf("DELIVERED"))
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"))
    }
} 