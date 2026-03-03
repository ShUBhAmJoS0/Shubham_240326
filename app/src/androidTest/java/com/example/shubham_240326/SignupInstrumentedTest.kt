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
class SignupInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    // Mock repository to simulate signup logic
    class MockUserRepo : UserRepo {
        override fun signUp(user: UserModel, password: String, callback: (Boolean, String) -> Unit) {
            if (user.name.isNotEmpty() && user.email.isNotEmpty() && password.length >= 6) {
                callback(true, "Sign up successful")
            } else {
                callback(false, "Sign up failed")
            }
        }
        override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {}
        override fun sendPasswordReset(email: String, callback: (Boolean, String) -> Unit) {}
        override fun getCurrentUser(): FirebaseUser? = null
        override fun getUserData(uid: String, callback: (UserModel?) -> Unit) {}
        override fun updateUserName(uid: String, newName: String, callback: (Boolean) -> Unit) {}
        override fun addToFavorites(userId: String, item: DashboardItem, callback: (Boolean) -> Unit) {}
        override fun removeFromFavorites(userId: String, itemName: String, callback: (Boolean) -> Unit) {}
        override fun getFavorites(userId: String, callback: (List<DashboardItem>) -> Unit) {}
        override fun addToCart(userId: String, item: DashboardItem, callback: (Boolean) -> Unit) {}
        override fun removeFromCart(userId: String, itemName: String, callback: (Boolean) -> Unit) {}
        override fun getCart(userId: String, callback: (List<DashboardItem>) -> Unit) {}
    }

    @Test
    fun testSuccessfulSignup_triggersCallback() {
        var signupSuccessTriggered = false

        composeRule.setContent {
            Shubham_240326Theme {
                SignUpScreen(
                    onSignUpSuccess = { signupSuccessTriggered = true },
                    userRepo = MockUserRepo()
                )
            }
        }

        // Fill in the name
        composeRule.onNodeWithTag("name")
            .performTextInput("John Doe")

        // Fill in the email
        composeRule.onNodeWithTag("email")
            .performTextInput("john@example.com")

        // Fill in the password
        composeRule.onNodeWithTag("password")
            .performTextInput("password123")

        // Click the sign up button
        composeRule.onNodeWithTag("signup")
            .performClick()

        // Assert that the success callback was triggered
        assert(signupSuccessTriggered)
    }
}
