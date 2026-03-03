package com.example.shubham_240326

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.shubham_240326.ui.theme.Shubham_240326Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Shubham_240326Theme {
                var currentScreen by rememberSaveable { mutableStateOf("signup") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "login" -> {
                                LoginScreen(
                                    onLoginSuccess = { currentScreen = "dashboard" },
                                    onCreateAccountClick = { currentScreen = "signup" },
                                    onForgotPasswordClick = { currentScreen = "forgot" }
                                )
                            }

                            "signup" -> {
                                SignUpScreen(
                                    onSignUpSuccess = { currentScreen = "login" },
                                    onAlreadyHaveAccountClick = { currentScreen = "login" }
                                )
                            }

                            "forgot" -> {
                                ForgotPasswordScreen(
                                    onBackToLogin = { currentScreen = "login" }
                                )
                            }

                            "dashboard" -> {
                                DashboardScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
