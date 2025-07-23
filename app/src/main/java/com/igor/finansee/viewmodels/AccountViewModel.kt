package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.daos.CreditCardDao
import com.igor.finansee.data.models.BankAccount
import com.igor.finansee.data.models.CreditCard
import kotlinx.coroutines.launch

class AccountViewModel(
    private val bankAccountDao: BankAccountDao,
    private val creditCardDao: CreditCardDao
) : ViewModel() {

    fun addBankAccount(name: String, type: String, initialBalance: Double, userId: Int) {
        viewModelScope.launch {
            val newAccount = BankAccount(
                name = name,
                type = type,
                currentBalance = initialBalance,
                userId = userId,
                isActive = true
            )
            bankAccountDao.upsertBankAccount(newAccount)
        }
    }

    fun addCreditCard(bankName: String, lastFour: String, limit: Double, closingDay: Int, dueDay: Int, userId: Int) {
        viewModelScope.launch {
            val newCard = CreditCard(
                bankName = bankName,
                lastFourDigits = lastFour,
                creditLimit = limit,
                statementClosingDay = closingDay,
                dueDate = dueDay,
                userId = userId,
                isActive = true
            )
            creditCardDao.upsertCreditCard(newCard)
        }
    }
}