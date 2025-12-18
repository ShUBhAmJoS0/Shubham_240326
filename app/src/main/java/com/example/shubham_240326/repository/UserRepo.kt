package com.example.shubham_240326.repository

import com.example.shubham_240326.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepo {
    fun signUp(
        user: UserModel,
        password: String,
        callback: (Boolean, String) -> Unit
    )

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )

    fun sendPasswordReset(
        email: String,
        callback: (Boolean, String) -> Unit
    )

    fun getCurrentUser(): FirebaseUser?
}



