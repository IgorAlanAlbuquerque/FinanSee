package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.models.User

class TransactionScreenViewModelFactory(
    private val transactionDao: TransactionDao,
    private val bankAccountDao: BankAccountDao,
    private val user: User
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionScreenViewModel(transactionDao, bankAccountDao, user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}