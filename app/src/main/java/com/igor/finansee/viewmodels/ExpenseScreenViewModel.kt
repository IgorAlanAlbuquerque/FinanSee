package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.repositories.ExpenseRepository
import com.igor.finansee.data.states.ExpenseScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class ExpenseScreenViewModel(
    private val expenseRepository: ExpenseRepository = ExpenseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseScreenUiState())
    val uiState: StateFlow<ExpenseScreenUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        updateExpensesForSelectedMonth()
    }

    fun selectNextMonth() {
        _uiState.update { it.copy(selectedMonth = it.selectedMonth.plusMonths(1)) }
        updateExpensesForSelectedMonth()
    }

    fun selectPreviousMonth() {
        _uiState.update { it.copy(selectedMonth = it.selectedMonth.minusMonths(1)) }
        updateExpensesForSelectedMonth()
    }

    private fun updateExpensesForSelectedMonth() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val month = _uiState.value.selectedMonth
            val expenses = expenseRepository.getAllExpenses().filter {
                it.data.year == month.year && it.data.month == month.month
            }

            val total = expenses.sumOf { it.valor }

            _uiState.update {
                it.copy(
                    expenses = expenses,
                    totalExpenses = total,
                    isLoading = false
                )
            }
        }
    }

    fun updateExpense(expenseId: UUID, descricao: String, valor: Double, categoria: Category, data: String) {
        viewModelScope.launch {
            expenseRepository.updateExpense(expenseId, descricao, valor, categoria, data)
            updateExpensesForSelectedMonth()
        }
    }

    fun deleteExpense(expenseId: UUID) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId)
            updateExpensesForSelectedMonth()
        }
    }
}
