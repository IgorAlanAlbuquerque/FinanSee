package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.igor.finansee.data.AuthRepository
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.CreditCardDao
import com.igor.finansee.data.daos.FaturaCreditCardDao
import com.igor.finansee.data.daos.MonthPlanningDao
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.repository.BankAccountRepository
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.CreditCardRepository
import com.igor.finansee.data.repository.FaturaCreditCardRepository
import com.igor.finansee.data.repository.MonthPlanningRepository
import com.igor.finansee.data.repository.TransactionRepository

/**
 * Factory para criar o HomeScreenViewModel.
 * Agora, em vez de receber um 'User', recebe um 'AuthRepository' para obter o ID do utilizador.
 */
class HomeScreenViewModelFactory(
    private val bankAccountDao: BankAccountDao,
    private val creditCardDao: CreditCardDao,
    private val faturaDao: FaturaCreditCardDao,
    private val transactionDao: TransactionDao,
    private val planningDao: MonthPlanningDao,
    private val categoryDao: CategoryDao,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            val firestore = Firebase.firestore
            val userId = authRepository.getFirebaseUserId()
                ?: throw IllegalStateException("O utilizador deve ter sessão iniciada para aceder à HomeScreen")

            val transactionRepository = TransactionRepository(transactionDao, firestore, userId)
            val planningRepository = MonthPlanningRepository(planningDao, firestore, userId)
            val creditCardRepository = CreditCardRepository(creditCardDao, firestore, userId)
            val faturaRepository = FaturaCreditCardRepository(faturaDao, firestore, userId)
            val bankAccountRepository = BankAccountRepository(bankAccountDao, firestore, userId)
            val categoryRepository = CategoryRepository(categoryDao, firestore)

            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(
                transactionRepository = transactionRepository,
                planningRepository = planningRepository,
                faturaRepository = faturaRepository,
                creditCardRepository = creditCardRepository,
                bankAccountRepository = bankAccountRepository,
                categoryRepository = categoryRepository,
                authRepository = authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
