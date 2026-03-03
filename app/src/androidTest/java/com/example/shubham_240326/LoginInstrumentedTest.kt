package com.example.shubham_240326

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shubham_240326.model.UserModel
import com.example.shubham_240326.repository.UserRepo
import com.example.shubham_240326.ui.theme.Shubham_240326Theme
import com.google.firebase.auth.FirebaseUser
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    // A simple mock repository for testing UI logic without Firebase
    class MockUserRepo : UserRepo {
        override fun signUp(user: UserModel, password: String, callback: (Boolean, String) -> Unit) {}
        override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
            if (email == "test@gmail.com" && password == "password") {
                callback(true, "Login successful")
            } else {
                callback(false, "Login failed")
            }
        }
        override fun sendPasswordReset(email: String, callback: (Boolean, String) -> Unit) {}
        override fun getCurrentUser(): FirebaseUser? = null
        override fun getUserData(uid: String, callback: (UserModel?) -> Unit) {
            callback(UserModel(name = "Test User"))
        }
        override fun updateUserName(uid: String, newName: String, callback: (Boolean) -> Unit) {}
        override fun addToFavorites(userId: String, item: DashboardItem, callback: (Boolean) -> Unit) {}
        override fun removeFromFavorites(userId: String, itemName: String, callback: (Boolean) -> Unit) {}
        override fun getFavorites(userId: String, callback: (List<DashboardItem>) -> Unit) { callback(emptyList()) }
        override fun addToCart(userId: String, item: DashboardItem, callback: (Boolean) -> Unit) {}
        override fun removeFromCart(userId: String, itemName: String, callback: (Boolean) -> Unit) {}
        override fun getCart(userId: String, callback: (List<DashboardItem>) -> Unit) { callback(emptyList()) }
    }

    @Test
    fun testSuccessfulLogin_triggersCallback() {
        var loginSuccessTriggered = false

        composeRule.setContent {
            Shubham_240326Theme {
                LoginScreen(
                    onLoginSuccess = { loginSuccessTriggered = true },
                    userRepo = MockUserRepo()
                )
            }
        }

        // Fill in the email
        composeRule.onNodeWithTag("email")
            .performTextInput("test@gmail.com")

        // Fill in the password
        composeRule.onNodeWithTag("password")
            .performTextInput("password")

        // Click the login button
        composeRule.onNodeWithTag("login")
            .performClick()

        // Assert that the success callback was triggered
        assert(loginSuccessTriggered)
    }
}
