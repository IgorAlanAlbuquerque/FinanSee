package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.MonthPlanningDao
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.models.User
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.MonthPlanningRepository
import com.igor.finansee.data.repository.TransactionRepository

class PlansScreenViewModelFactory(
    private val planningDao: MonthPlanningDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val user: User?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlansScreenViewModel::class.java)) {
            val firestore = Firebase.firestore
            val userId = user?.id ?: throw IllegalStateException("O utilizador deve ter sessão iniciada")

            val planningRepository = MonthPlanningRepository(planningDao, firestore, userId)
            val transactionRepository = TransactionRepository(transactionDao, firestore, userId)
            val categoryRepository = CategoryRepository(categoryDao, firestore)

            @Suppress("UNCHECKED_CAST")
            return PlansScreenViewModel(
                planningRepository,
                transactionRepository,
                categoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}