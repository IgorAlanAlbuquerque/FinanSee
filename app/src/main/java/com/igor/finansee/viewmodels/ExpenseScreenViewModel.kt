package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.Expense
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.ExpenseRepository
import com.igor.finansee.data.states.ExpenseScreenUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseScreenViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))

    init {
        expenseRepository.startListeningForRemoteChanges()
        categoryRepository.startListeningForRemoteChanges()
    }

    val uiState: StateFlow<ExpenseScreenUiState> = _selectedMonth.flatMapLatest { month ->
        val startDate = month
        val endDate = month.plusMonths(1)

        expenseRepository.getExpensesFromRoom(startDate, endDate)
            .map { expensesWithCategory ->
                ExpenseScreenUiState(
                    selectedMonth = month,
                    expenses = expensesWithCategory,
                    totalExpenses = expensesWithCategory.sumOf { it.expense.valor },
                    isLoading = false
                )
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ExpenseScreenUiState(isLoading = true)
    )

    val categories: StateFlow<List<Category>> = categoryRepository.getCategoriesFromRoom()
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

    fun addExpense(descricao: String, valor: Double, categoryId: Int?, data: LocalDate) {
        viewModelScope.launch {
            val newExpense = Expense(descricao = descricao, valor = valor, categoryId = categoryId, data = data)
            expenseRepository.saveExpense(newExpense)
        }
    }

    fun updateExpense(expenseId: UUID, descricao: String, valor: Double, categoryId: Int?, data: LocalDate) {
        viewModelScope.launch {
            val expenseToUpdate = Expense(expenseId, descricao, valor, categoryId, data)
            expenseRepository.saveExpense(expenseToUpdate)
        }
    }

    fun deleteExpense(expenseId: UUID) {
        viewModelScope.launch {
            expenseRepository.getExpenseById(expenseId)?.let { expenseToDelete ->
                expenseRepository.deleteExpense(expenseToDelete)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        expenseRepository.cancelScope()
        categoryRepository.cancelScope()
    }
}