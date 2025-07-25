package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.ExpenseDao
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.ExpenseRepository

class ExpenseScreenViewModelFactory(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseScreenViewModel::class.java)) {
            val firestore = Firebase.firestore
            val expenseRepository = ExpenseRepository(expenseDao, firestore, "someUserId")
            val categoryRepository = CategoryRepository(categoryDao, firestore)

            @Suppress("UNCHECKED_CAST")
            return ExpenseScreenViewModel(expenseRepository, categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}