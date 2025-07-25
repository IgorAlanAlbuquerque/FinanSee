package com.igor.finansee.data.repository

import com.igor.finansee.data.daos.UserDao
import com.igor.finansee.data.models.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userDao: UserDao) {

    fun getUser(userId: Int): Flow<User?> = userDao.getUserById(userId)

    suspend fun findByEmail(email: String): User? = userDao.findByEmail(email)

    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    suspend fun updateUser(user: User) {
        userDao.update(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.delete(user)
    }
}