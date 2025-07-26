package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.BankAccount
import com.igor.finansee.data.models.CreditCard
import com.igor.finansee.data.repository.BankAccountRepository
import com.igor.finansee.data.repository.CreditCardRepository
import kotlinx.coroutines.launch

class AccountViewModel(
    private val bankAccountRepository: BankAccountRepository,
    private val creditCardRepository: CreditCardRepository
) : ViewModel() {

    init {
        bankAccountRepository.startListeningForRemoteChanges()
        creditCardRepository.startListeningForRemoteChanges()
    }

    fun addBankAccount(name: String, type: String, initialBalance: Double, userId: String) {
        viewModelScope.launch {
            val newAccount = BankAccount(
                name = name,
                type = type,
                currentBalance = initialBalance,
                userId = userId,
                isActive = true
            )
            bankAccountRepository.upsertAccount(newAccount)
        }
    }

    fun addCreditCard(bankName: String, lastFour: String, limit: Double, closingDay: Int, dueDay: Int, userId: String) {
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
            creditCardRepository.upsertCreditCard(newCard)
        }
    }

    override fun onCleared() {
        super.onCleared()
        bankAccountRepository.cancelScope()
        creditCardRepository.cancelScope()
    }
}