package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.daos.CreditCardDao
import com.igor.finansee.data.models.User
import com.igor.finansee.data.repository.BankAccountRepository
import com.igor.finansee.data.repository.CreditCardRepository

class AccountViewModelFactory(
    private val bankAccountDao: BankAccountDao,
    private val creditCardDao: CreditCardDao,
    private val user: User
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            val firestore = Firebase.firestore
            val userId = user.id.toString()

            val bankAccountRepository = BankAccountRepository(bankAccountDao, firestore, userId)
            val creditCardRepository = CreditCardRepository(creditCardDao, firestore, userId)

            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(bankAccountRepository, creditCardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}