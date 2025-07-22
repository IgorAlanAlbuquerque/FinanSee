package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.CreditCardDao
import com.igor.finansee.data.daos.FaturaCreditCardDao
import com.igor.finansee.data.daos.MonthPlanningDao
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.models.User

class HomeScreenViewModelFactory(
    private val bankAccountDao: BankAccountDao,
    private val creditCardDao: CreditCardDao,
    private val faturaDao: FaturaCreditCardDao,
    private val transactionDao: TransactionDao,
    private val planningDao: MonthPlanningDao,
    private val categoryDao: CategoryDao,
    private val user: User
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(bankAccountDao, creditCardDao, faturaDao, transactionDao, planningDao, categoryDao, user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}