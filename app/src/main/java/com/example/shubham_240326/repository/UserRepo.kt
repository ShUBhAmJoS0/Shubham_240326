package com.example.shubham_240326.repository

import com.example.shubham_240326.model.UserModel
import com.example.shubham_240326.DashboardItem
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

    fun getUserData(uid: String, callback: (UserModel?) -> Unit)

    fun updateUserName(uid: String, newName: String, callback: (Boolean) -> Unit)

    fun addToFavorites(userId: String, item: DashboardItem, callback: (Boolean) -> Unit)

    fun removeFromFavorites(userId: String, itemName: String, callback: (Boolean) -> Unit)

    fun getFavorites(userId: String, callback: (List<DashboardItem>) -> Unit)

    fun addToCart(userId: String, item: DashboardItem, callback: (Boolean) -> Unit)

    fun removeFromCart(userId: String, itemName: String, callback: (Boolean) -> Unit)

    fun getCart(userId: String, callback: (List<DashboardItem>) -> Unit)
}
