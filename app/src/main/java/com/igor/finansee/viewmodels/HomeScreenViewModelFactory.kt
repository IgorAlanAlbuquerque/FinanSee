package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.CreditCardDao
import com.igor.finansee.data.daos.FaturaCreditCardDao
import com.igor.finansee.data.daos.MonthPlanningDao
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.models.User
import com.igor.finansee.data.repository.BankAccountRepository
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.CreditCardRepository
import com.igor.finansee.data.repository.FaturaCreditCardRepository
import com.igor.finansee.data.repository.MonthPlanningRepository
import com.igor.finansee.data.repository.TransactionRepository

class HomeScreenViewModelFactory(
    private val bankAccountDao: BankAccountDao,
    private val creditCardDao: CreditCardDao,
    private val faturaDao: FaturaCreditCardDao,
    private val transactionDao: TransactionDao,
    private val planningDao: MonthPlanningDao,
    private val categoryDao: CategoryDao,
    private val user: User?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            val firestore = Firebase.firestore
            user?.id ?: throw IllegalStateException("O utilizador deve ter sessão iniciada")

            val transactionRepository = TransactionRepository(transactionDao, firestore, user.id)
            val planningRepository = MonthPlanningRepository(planningDao, firestore, user.id)
            val creditCardRepository = CreditCardRepository(creditCardDao, firestore, user.id)
            val faturaRepository = FaturaCreditCardRepository(faturaDao, firestore, user.id)
            val bankAccountRepository = BankAccountRepository(bankAccountDao, firestore, user.id)
            val categoryRepository = CategoryRepository(categoryDao, firestore)

            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(
                transactionRepository = transactionRepository,
                planningRepository = planningRepository,
                faturaRepository = faturaRepository,
                creditCardRepository = creditCardRepository,
                bankAccountRepository = bankAccountRepository,
                categoryRepository = categoryRepository,
                user = user
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
