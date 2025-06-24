package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.*
import com.igor.finansee.data.states.HomeScreenUiState
import com.igor.finansee.ui.screens.CategoryWithAmount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    private lateinit var currentUser: User

    private val allTransactions = transactionList
    private val allBankAccounts = bankAccountList
    private val allCreditCards = creditCardList
    private val allFaturas = faturaCreditCardList
    private val allPlanning = mockMonthPlanningList
    private val allCategories = categoryList

    fun loadInitialData(user: User) {
        this.currentUser = user
        _uiState.update { it.copy(
            userName = user.name.split(" ").firstOrNull() ?: "",
            allCategories = this.allCategories
        )}
        updateDataForSelectedDate()
    }

    fun toggleBalanceVisibility() {
        _uiState.update { it.copy(showBalance = !it.showBalance) }
    }

    fun selectNextMonth() {
        val newDate = _uiState.value.selectedDate.plusMonths(1)
        _uiState.update { it.copy(selectedDate = newDate) }
        updateDataForSelectedDate()
    }

    fun selectPreviousMonth() {
        val newDate = _uiState.value.selectedDate.minusMonths(1)
        _uiState.update { it.copy(selectedDate = newDate) }
        updateDataForSelectedDate()
    }

    private fun updateDataForSelectedDate() {
        viewModelScope.launch {
            val date = _uiState.value.selectedDate
            val userId = currentUser.id

            val userTransactions = allTransactions.filter { it.userId == userId }
            val userBankAccounts = allBankAccounts.filter { it.userId == userId }
            val userCreditCards = allCreditCards.filter { it.userId == userId }

            val totalAccountBalance = userBankAccounts.sumOf { it.currentBalance }

            val incomeForSelectedMonth = userTransactions
                .filter { it.date.month == date.month && it.date.year == date.year && it.type == TransactionType.INCOME }
                .sumOf { it.value }

            val directExpenses = userTransactions
                .filter { it.date.month == date.month && it.date.year == date.year && it.type == TransactionType.EXPENSE }
                .sumOf { it.value }

            val currentMonthFaturas = allFaturas
                .filter { fatura -> userCreditCards.any { it.id == fatura.creditCardId } && fatura.month.month == date.month && fatura.month.year == date.year }

            val creditCardInvoiceExpenses = currentMonthFaturas.sumOf { it.valor }
            val totalExpenses = directExpenses + creditCardInvoiceExpenses
            val totalCreditCardInvoiceAmount = creditCardInvoiceExpenses

            val expensesByCategory = userTransactions
                .filter { it.date.month == date.month && it.date.year == date.year && (it.type == TransactionType.EXPENSE || it.type == TransactionType.CREDIT_CARD_EXPENSE) }
                .groupBy { it.categoryId }
                .map { (categoryId, transactions) ->
                    val category = allCategories.find { it.id == categoryId } ?: Category(categoryId, "Outros")
                    CategoryWithAmount(category, transactions.sumOf { it.value })
                }
                .sortedByDescending { it.totalAmount }

            val currentPlanning = allPlanning.find {
                it.userId == userId && it.monthYear.year == date.year && it.monthYear.month == date.month
            }

            _uiState.update { currentState ->
                currentState.copy(
                    totalAccountBalance = totalAccountBalance,
                    incomeForSelectedMonth = incomeForSelectedMonth,
                    expensesForSelectedMonth = totalExpenses,
                    userBankAccounts = userBankAccounts,
                    userCreditCards = userCreditCards,
                    currentMonthFaturas = currentMonthFaturas,
                    totalCreditCardInvoiceAmount = totalCreditCardInvoiceAmount,
                    expensesByCategory = expensesByCategory,
                    currentMonthPlanning = currentPlanning
                )
            }
        }
    }
}