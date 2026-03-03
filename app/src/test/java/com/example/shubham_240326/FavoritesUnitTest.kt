package com.example.shubham_240326

import com.example.shubham_240326.repository.UserRepo
import com.example.shubham_240326.model.UserModel
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoritesUnitTest {

    // A simplified mock of UserRepo to test business logic related to Favorites
    class MockUserRepo : UserRepo {
        private val favoriteItems = mutableListOf<DashboardItem>()

        override fun addToFavorites(userId: String, item: DashboardItem, callback: (Boolean) -> Unit) {
            favoriteItems.add(item)
            callback(true)
        }

        override fun removeFromFavorites(userId: String, itemName: String, callback: (Boolean) -> Unit) {
            val removed = favoriteItems.removeIf { it.name == itemName }
            callback(removed)
        }

        override fun getFavorites(userId: String, callback: (List<DashboardItem>) -> Unit) {
            callback(favoriteItems.toList())
        }

        // Other methods not needed for this specific test
        override fun signUp(user: UserModel, password: String, callback: (Boolean, String) -> Unit) {}
        override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {}
        override fun sendPasswordReset(email: String, callback: (Boolean, String) -> Unit) {}
        override fun getCurrentUser(): FirebaseUser? = null
        override fun getUserData(uid: String, callback: (UserModel?) -> Unit) {}
        override fun updateUserName(uid: String, newName: String, callback: (Boolean) -> Unit) {}
        override fun addToCart(userId: String, item: DashboardItem, callback: (Boolean) -> Unit) {}
        override fun removeFromCart(userId: String, itemName: String, callback: (Boolean) -> Unit) {}
        override fun getCart(userId: String, callback: (List<DashboardItem>) -> Unit) {}
    }

    @Test
    fun testAddAndRetrieveFavorites() {
        val repo = MockUserRepo()
        val item = DashboardItem("Coffee", "Rich & Aromatic", "$4.50", 0)
        
        var retrievedItems = listOf<DashboardItem>()

        // Add to favorites
        repo.addToFavorites("user123", item) { success ->
            assertTrue(success)
        }

        // Retrieve favorites and verify
        repo.getFavorites("user123") { items ->
            retrievedItems = items
        }

        assertEquals(1, retrievedItems.size)
        assertEquals("Coffee", retrievedItems[0].name)
    }

    @Test
    fun testRemoveFromFavorites() {
        val repo = MockUserRepo()
        val item1 = DashboardItem("Coffee", "Rich & Aromatic", "$4.50", 0)
        val item2 = DashboardItem("Cake", "Sweet & Fluffy", "$5.20", 0)

        repo.addToFavorites("user123", item1) {}
        repo.addToFavorites("user123", item2) {}

        // Remove one item
        repo.removeFromFavorites("user123", "Coffee") { success ->
            assertTrue(success)
        }

        // Verify only one item remains
        var remainingItems = listOf<DashboardItem>()
        repo.getFavorites("user123") { items ->
            remainingItems = items
        }

        assertEquals(1, remainingItems.size)
        assertEquals("Cake", remainingItems[0].name)
    }
}
