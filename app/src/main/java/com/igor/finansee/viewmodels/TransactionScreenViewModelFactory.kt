package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.models.User
import com.igor.finansee.data.repository.BankAccountRepository
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.TransactionRepository

class TransactionScreenViewModelFactory(
    private val transactionDao: TransactionDao,
    private val bankAccountDao: BankAccountDao,
    private val categoryDao: CategoryDao,
    private val user: User?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionScreenViewModel::class.java)) {
            val firestore = Firebase.firestore
            val userId = user?.id.toString()

            val transactionRepository = TransactionRepository(transactionDao, firestore, userId)
            val bankAccountRepository = BankAccountRepository(bankAccountDao, firestore, userId)
            val categoryRepository =
                CategoryRepository(categoryDao, firestore)

            @Suppress("UNCHECKED_CAST")
            return TransactionScreenViewModel(
                transactionRepository = transactionRepository,
                bankAccountRepository = bankAccountRepository,
                categoryRepository = categoryRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}