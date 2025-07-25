package com.igor.finansee.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.igor.finansee.data.daos.UserDao
import com.igor.finansee.data.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore,
    private val userId: String
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun getUser(): Flow<User?> = userDao.getUserById(userId.toIntOrNull() ?: 0)

    suspend fun findByEmail(email: String): User? = userDao.findByEmail(email)

    fun startListeningForUserChanges() {
        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("UserRepository", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val remoteUser = snapshot.toObject(User::class.java)
                    if (remoteUser != null) {
                        repositoryScope.launch {
                            userDao.insert(remoteUser)
                        }
                    }
                } else {
                    Log.d("UserRepository", "Current data: null")
                }
            }
    }
    suspend fun insertUser(user: User) {
        try {
            firestore.collection("users").document(user.id.toString()).set(user).await()
            userDao.insert(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error inserting user", e)
        }
    }

    suspend fun updateUser(user: User) {
        try {
            firestore.collection("users").document(user.id.toString()).set(user).await()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user", e)
        }
    }

    suspend fun deleteUser(user: User) {
        userDao.delete(user)
    }
}