package com.igor.finansee.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.igor.finansee.data.daos.*
import com.igor.finansee.data.models.User
import com.igor.finansee.data.repository.*

class PlansScreenViewModelFactory(
    private val transactionDao: TransactionDao,
    private val expenseDao: ExpenseDao, // <-- ADICIONE este parâmetro
    private val categoryDao: CategoryDao,
    private val planningDao: MonthPlanningDao,
    private val user: User?,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlansScreenViewModel::class.java)) {
            val firestore = Firebase.firestore
            val userId = user?.id ?: throw IllegalStateException("O utilizador deve ter sessão iniciada")

            val planningRepository = MonthPlanningRepository(planningDao, firestore, userId)
            val transactionRepository = TransactionRepository(transactionDao, firestore, userId)
            val categoryRepository = CategoryRepository(categoryDao, firestore)
            val expenseRepository = ExpenseRepository(expenseDao, firestore, userId) // <-- CRIE o expenseRepository

            @Suppress("UNCHECKED_CAST")
            return PlansScreenViewModel(
                planningRepository,
                transactionRepository,
                expenseRepository, // <-- PASSE o novo repositório
                categoryRepository,
                context
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
