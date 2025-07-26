package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.ExpenseDao
import com.igor.finansee.data.models.User
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.ExpenseRepository

class ExpenseScreenViewModelFactory(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val user: User?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseScreenViewModel::class.java)) {
            val firestore = Firebase.firestore
            user?.id ?: throw IllegalStateException("O utilizador deve ter sessão iniciada")

            val expenseRepository = ExpenseRepository(expenseDao, firestore, user.id)
            val categoryRepository = CategoryRepository(categoryDao, firestore)

            @Suppress("UNCHECKED_CAST")
            return ExpenseScreenViewModel(expenseRepository, categoryRepository, user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}