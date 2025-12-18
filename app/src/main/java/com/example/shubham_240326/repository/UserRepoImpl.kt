package com.example.shubham_240326.repository

import com.example.shubham_240326.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserRepoImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
) : UserRepo {

    override fun signUp(
        user: UserModel,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (user.email.isBlank() || password.length < 6) {
            callback(false, "Please enter a valid email and password (min 6 characters)")
            return
        }

        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val data = mapOf(
                            "name" to user.name,
                            "email" to user.email
                        )
                        usersRef.child(uid).setValue(data)
                    }
                    callback(true, "Sign up successful")
                } else {
                    callback(false, task.exception?.localizedMessage ?: "Sign up failed")
                }
            }
    }

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            callback(false, "Please enter email and password")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Login successful")
                } else {
                    callback(false, task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    override fun sendPasswordReset(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (email.isBlank()) {
            callback(false, "Please enter email")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Reset link sent to $email")
                } else {
                    callback(false, task.exception?.localizedMessage ?: "Failed to send reset email")
                }
            }
    }

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser
}


