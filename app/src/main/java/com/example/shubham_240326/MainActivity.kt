package com.example.shubham_240326

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
                    when (currentScreen) {
                        "login" -> {
                            LoginScreen(
                                modifier = Modifier.padding(innerPadding),
                                onLoginSuccess = { currentScreen = "dashboard" },
                                onCreateAccountClick = { currentScreen = "signup" },
                                onForgotPasswordClick = { currentScreen = "forgot" }
                            )
                        }

                        "signup" -> {
                            SignUpScreen(
                                modifier = Modifier.padding(innerPadding),
                                onSignUpSuccess = { currentScreen = "login" },
                                onAlreadyHaveAccountClick = { currentScreen = "login" }
                            )
                        }

                        "forgot" -> {
                            ForgotPasswordScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackToLogin = { currentScreen = "login" }
                            )
                        }

                        "dashboard" -> {
                            DashboardScreen(
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}