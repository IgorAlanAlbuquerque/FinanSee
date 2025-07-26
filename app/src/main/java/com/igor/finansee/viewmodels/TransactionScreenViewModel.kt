package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.Transaction
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.data.models.TransactionWithCategory
import com.igor.finansee.data.repository.BankAccountRepository
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.TransactionRepository
import com.igor.finansee.data.states.TransactionScreenUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionScreenViewModel(
    private val transactionRepository: TransactionRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))

    init {
        transactionRepository.startListeningForRemoteChanges()
        bankAccountRepository.startListeningForRemoteChanges()
        categoryRepository.startListeningForRemoteChanges()
    }

    private val _monthlyDataFlow: Flow<MonthlyData> = _selectedMonth.flatMapLatest { month ->
        val startDate = month
        val endDate = month.plusMonths(1)
        transactionRepository.getTransactionsFromRoom(startDate, endDate)
            .map { transactionsWithCategory ->
                val income = transactionsWithCategory
                    .filter { it.transaction.type == TransactionType.INCOME || it.transaction.type == TransactionType.TRANSFER_IN }
                    .sumOf { it.transaction.value }
                val expenses = transactionsWithCategory
                    .filter { it.transaction.type == TransactionType.EXPENSE || it.transaction.type == TransactionType.TRANSFER_OUT || it.transaction.type == TransactionType.CREDIT_CARD_EXPENSE }
                    .sumOf { it.transaction.value }

                MonthlyData(
                    transactions = transactionsWithCategory,
                    totalIncome = income,
                    totalExpenses = expenses
                )
            }
    }

    val uiState: StateFlow<TransactionScreenUiState> = combine(
        bankAccountRepository.getOverallBalanceFromRoom(),
        _monthlyDataFlow,
        _selectedMonth
    ) { overallBalance, monthlyData, month ->
        TransactionScreenUiState(
            isLoading = false,
            selectedMonth = month,
            currentOverallBalance = overallBalance ?: 0.0,
            monthlyBalance = monthlyData.totalIncome - monthlyData.totalExpenses,
            transactionsByDate = monthlyData.transactions.groupBy { it.transaction.date }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TransactionScreenUiState(isLoading = true, selectedMonth = LocalDate.now())
    )

    val allCategories: StateFlow<List<Category>> = categoryRepository.getCategoriesFromRoom()
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

    fun saveTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.upsertTransaction(transaction)
        }
    }

    private data class MonthlyData(
        val transactions: List<TransactionWithCategory>,
        val totalIncome: Double,
        val totalExpenses: Double
    )

    override fun onCleared() {
        super.onCleared()
        transactionRepository.cancelScope()
        bankAccountRepository.cancelScope()
        categoryRepository.cancelScope()
    }
}