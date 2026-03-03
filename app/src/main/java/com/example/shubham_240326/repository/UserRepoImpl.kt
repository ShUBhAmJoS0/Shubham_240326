package com.example.shubham_240326.repository

import com.example.shubham_240326.model.UserModel
import com.example.shubham_240326.DashboardItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserRepoImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
) : UserRepo {

    private val usersRef = rootRef.child("users")

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
                            "uid" to uid,
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

    override fun getUserData(uid: String, callback: (UserModel?) -> Unit) {
        usersRef.child(uid).get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(UserModel::class.java)
            callback(user)
        }.addOnFailureListener {
            callback(null)
        }
    }

    override fun updateUserName(uid: String, newName: String, callback: (Boolean) -> Unit) {
        usersRef.child(uid).child("name").setValue(newName)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    override fun addToFavorites(userId: String, item: DashboardItem, callback: (Boolean) -> Unit) {
        usersRef.child(userId).child("favorites").child(item.name).setValue(item)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    override fun removeFromFavorites(userId: String, itemName: String, callback: (Boolean) -> Unit) {
        usersRef.child(userId).child("favorites").child(itemName).removeValue()
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    override fun getFavorites(userId: String, callback: (List<DashboardItem>) -> Unit) {
        usersRef.child(userId).child("favorites").get().addOnSuccessListener { snapshot ->
            val favorites = mutableListOf<DashboardItem>()
            snapshot.children.forEach { child ->
                try {
                    val map = child.value as? Map<*, *>
                    if (map != null) {
                        val item = DashboardItem(
                            name = map["name"] as? String ?: "",
                            description = map["description"] as? String ?: "",
                            price = map["price"] as? String ?: "",
                            imageRes = (map["imageRes"] as? Long)?.toInt() ?: 0
                        )
                        favorites.add(item)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            callback(favorites)
        }.addOnFailureListener {
            callback(emptyList())
        }
    }

    override fun addToCart(userId: String, item: DashboardItem, callback: (Boolean) -> Unit) {
        usersRef.child(userId).child("cart").child(item.name).setValue(item)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    override fun removeFromCart(userId: String, itemName: String, callback: (Boolean) -> Unit) {
        usersRef.child(userId).child("cart").child(itemName).removeValue()
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    override fun getCart(userId: String, callback: (List<DashboardItem>) -> Unit) {
        usersRef.child(userId).child("cart").get().addOnSuccessListener { snapshot ->
            val cart = mutableListOf<DashboardItem>()
            snapshot.children.forEach { child ->
                try {
                    val map = child.value as? Map<*, *>
                    if (map != null) {
                        val item = DashboardItem(
                            name = map["name"] as? String ?: "",
                            description = map["description"] as? String ?: "",
                            price = map["price"] as? String ?: "",
                            imageRes = (map["imageRes"] as? Long)?.toInt() ?: 0
                        )
                        cart.add(item)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            callback(cart)
        }.addOnFailureListener {
            callback(emptyList())
        }
    }
}
