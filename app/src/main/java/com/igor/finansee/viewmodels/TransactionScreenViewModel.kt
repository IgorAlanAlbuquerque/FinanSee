package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.data.models.User
import com.igor.finansee.data.states.TransactionScreenUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionScreenViewModel(
    private val transactionDao: TransactionDao,
    private val bankAccountDao: BankAccountDao,
    private val categoryDao: CategoryDao,
    private val currentUser: User
) : ViewModel() {
    private val _selectedMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))

    private val _monthlyDataFlow = _selectedMonth.flatMapLatest { month ->
        val startDate = month
        val endDate = month.plusMonths(1)
        val incomeTypes = listOf(TransactionType.INCOME, TransactionType.TRANSFER_IN)
        val expenseTypes = listOf(
            TransactionType.EXPENSE,
            TransactionType.TRANSFER_OUT,
            TransactionType.CREDIT_CARD_EXPENSE
        )

        combine(
            transactionDao.getTransactionsForPeriod(currentUser.id, startDate, endDate),
            transactionDao.getSumByTypesForPeriod(currentUser.id, incomeTypes, startDate, endDate),
            transactionDao.getSumByTypesForPeriod(currentUser.id, expenseTypes, startDate, endDate)
        ) { transactions, income, expenses ->
            MonthlyData(
                transactions = transactions,
                totalIncome = income ?: 0.0,
                totalExpenses = expenses ?: 0.0
            )
        }
    }

    val uiState: StateFlow<TransactionScreenUiState> = combine(
        bankAccountDao.getOverallBalanceForUser(currentUser.id),
        _monthlyDataFlow,
        _selectedMonth
    ) { overallBalance, monthlyData, month ->
        TransactionScreenUiState(
            isLoading = false,
            selectedMonth = month,
            currentOverallBalance = overallBalance ?: 0.0,
            monthlyBalance = monthlyData.totalIncome - monthlyData.totalExpenses,
            transactionsByDate = monthlyData.transactions.groupBy { it.date }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TransactionScreenUiState(isLoading = true, selectedMonth = LocalDate.now())
    )

    val allCategories: StateFlow<List<Category>> = categoryDao.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun selectNextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
    }

    fun selectPreviousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
    }

    private data class MonthlyData(
        val transactions: List<com.igor.finansee.data.models.Transaction>,
        val totalIncome: Double,
        val totalExpenses: Double
    )
}