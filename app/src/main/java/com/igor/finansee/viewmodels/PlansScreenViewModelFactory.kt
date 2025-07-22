package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.igor.finansee.data.daos.MonthPlanningDao
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.models.User

class PlansScreenViewModelFactory(
    private val planningDao: MonthPlanningDao,
    private val transactionDao: TransactionDao,
    private val user: User
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlansScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlansScreenViewModel(planningDao, transactionDao, user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}