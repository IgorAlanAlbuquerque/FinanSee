package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.daos.CreditCardDao

class AccountViewModelFactory(
    private val bankAccountDao: BankAccountDao,
    private val creditCardDao: CreditCardDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(bankAccountDao, creditCardDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}