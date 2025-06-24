package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.data.models.User
import com.igor.finansee.data.models.bankAccountList
import com.igor.finansee.data.models.transactionList
import com.igor.finansee.data.states.TransactionScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionScreenUiState())
    val uiState: StateFlow<TransactionScreenUiState> = _uiState.asStateFlow()

    private lateinit var currentUser: User

    private val allTransactions = transactionList
    private val allBankAccounts = bankAccountList

    fun loadInitialData(user: User) {
        this.currentUser = user
        updateDataForSelectedMonth()
    }

    fun selectNextMonth() {
        _uiState.update { it.copy(selectedMonth = it.selectedMonth.plusMonths(1)) }
        updateDataForSelectedMonth()
    }

    fun selectPreviousMonth() {
        _uiState.update { it.copy(selectedMonth = it.selectedMonth.minusMonths(1)) }
        updateDataForSelectedMonth()
    }

    private fun updateDataForSelectedMonth() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val month = _uiState.value.selectedMonth

            val overallBalance = allBankAccounts
                .filter { it.userId == currentUser.id }
                .sumOf { it.currentBalance }

            val userTransactionsForMonth = allTransactions
                .filter {
                    it.userId == currentUser.id &&
                            it.date.year == month.year &&
                            it.date.month == month.month
                }
                .sortedByDescending { it.date }

            val transactionsByDate = userTransactionsForMonth.groupBy { it.date }

            val income = userTransactionsForMonth
                .filter { it.type == TransactionType.INCOME || it.type == TransactionType.TRANSFER_IN }
                .sumOf { it.value }
            val expenses = userTransactionsForMonth
                .filter { it.type == TransactionType.EXPENSE || it.type == TransactionType.TRANSFER_OUT || it.type == TransactionType.CREDIT_CARD_EXPENSE }
                .sumOf { it.value }
            val monthlyBalance = income - expenses

            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentOverallBalance = overallBalance,
                    monthlyBalance = monthlyBalance,
                    transactionsByDate = transactionsByDate
                )
            }
        }
    }
}