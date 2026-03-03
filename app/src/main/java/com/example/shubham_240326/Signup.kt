package com.example.shubham_240326

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shubham_240326.model.UserModel
import com.example.shubham_240326.repository.UserRepo
import com.example.shubham_240326.repository.UserRepoImpl
import com.example.shubham_240326.ui.theme.Shubham_240326Theme

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpSuccess: () -> Unit = {},
    onAlreadyHaveAccountClick: () -> Unit = {},
    userRepo: UserRepo = UserRepoImpl()
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create account",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Text(
                text = "Sign up to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("name")
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email")
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    TextButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Text(if (isPasswordVisible) "Hide" else "Show")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val trimmedName = name.trim()
                    val trimmedEmail = email.trim()
                    val pwd = password

                    if (trimmedName.isBlank() || trimmedEmail.isBlank() || pwd.length < 6) {
                        Toast.makeText(
                            context,
                            "Please fill all fields (password at least 6 characters)",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val user = UserModel(
                        name = trimmedName,
                        email = trimmedEmail
                    )

                    userRepo.signUp(user, pwd) { success, message ->
                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                        if (success) {
                            onSignUpSuccess()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup")
            ) {
                Text("Sign up")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Already have an account? Login",
                color = Color.White,
                modifier = Modifier.clickable { onAlreadyHaveAccountClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    Shubham_240326Theme {
        SignUpScreen()
    }
}
