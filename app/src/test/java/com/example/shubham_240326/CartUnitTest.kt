package com.example.shubham_240326

import com.example.shubham_240326.repository.UserRepo
import com.example.shubham_240326.model.UserModel
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CartUnitTest {

    // A simplified mock of UserRepo to test business logic related to Cart
    class MockUserRepo : UserRepo {
        private val cartItems = mutableListOf<DashboardItem>()

        override fun addToCart(userId: String, item: DashboardItem, callback: (Boolean) -> Unit) {
            cartItems.add(item)
            callback(true)
        }

        override fun removeFromCart(userId: String, itemName: String, callback: (Boolean) -> Unit) {
            val removed = cartItems.removeIf { it.name == itemName }
            callback(removed)
        }

        override fun getCart(userId: String, callback: (List<DashboardItem>) -> Unit) {
            callback(cartItems.toList())
        }

        // Other methods not needed for this specific test
        override fun signUp(user: UserModel, password: String, callback: (Boolean, String) -> Unit) {}
        override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {}
        override fun sendPasswordReset(email: String, callback: (Boolean, String) -> Unit) {}
        override fun getCurrentUser(): FirebaseUser? = null
        override fun getUserData(uid: String, callback: (UserModel?) -> Unit) {}
        override fun updateUserName(uid: String, newName: String, callback: (Boolean) -> Unit) {}
        override fun addToFavorites(userId: String, item: DashboardItem, callback: (Boolean) -> Unit) {}
        override fun removeFromFavorites(userId: String, itemName: String, callback: (Boolean) -> Unit) {}
        override fun getFavorites(userId: String, callback: (List<DashboardItem>) -> Unit) {}
    }

    @Test
    fun testAddAndRetrieveCartItems() {
        val repo = MockUserRepo()
        val item = DashboardItem("Donut", "Glazed & Tasty", "$3.00", 0)
        
        var retrievedItems = listOf<DashboardItem>()

        // Add to cart
        repo.addToCart("user123", item) { success ->
            assertTrue(success)
        }

        // Retrieve cart and verify
        repo.getCart("user123") { items ->
            retrievedItems = items
        }

        assertEquals(1, retrievedItems.size)
        assertEquals("Donut", retrievedItems[0].name)
    }

    @Test
    fun testRemoveFromCart() {
        val repo = MockUserRepo()
        val item1 = DashboardItem("Donut", "Glazed & Tasty", "$3.00", 0)
        val item2 = DashboardItem("Tea", "Fresh & Calming", "$3.50", 0)

        repo.addToCart("user123", item1) {}
        repo.addToCart("user123", item2) {}

        // Remove one item
        repo.removeFromCart("user123", "Donut") { success ->
            assertTrue(success)
        }

        // Verify only one item remains
        var remainingItems = listOf<DashboardItem>()
        repo.getCart("user123") { items ->
            remainingItems = items
        }

        assertEquals(1, remainingItems.size)
        assertEquals("Tea", remainingItems[0].name)
    }
}
