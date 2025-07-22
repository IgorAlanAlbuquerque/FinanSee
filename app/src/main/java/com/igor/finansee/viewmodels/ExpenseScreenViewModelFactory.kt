package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.ExpenseDao

class ExpenseScreenViewModelFactory(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseScreenViewModel(expenseDao, categoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}