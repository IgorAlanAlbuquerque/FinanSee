package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.ExpenseDao
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.Expense
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

class ExpenseScreenViewModel(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao
) : ViewModel(){
    private val _selectedMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ExpenseScreenUiState> = _selectedMonth.flatMapLatest { month ->
        val startDate = month
        val endDate = month.plusMonths(1)

        expenseDao.getExpensesForPeriod(startDate, endDate)
            .map{ expenses ->
                ExpenseScreenUiState(
                    selectedMonth = month,
                    expenses = expenses,
                    totalExpenses = expenses.sumOf { it.valor },
                    isLoading = false
                )
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ExpenseScreenUiState(isLoading = true)
    )

    val categories: StateFlow<List<Category>> = categoryDao.getAllCategories()
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

    fun addExpense(descricao: String, valor: Double, categoria: Category, data: LocalDate) {
        viewModelScope.launch {
            val newExpense = Expense(descricao = descricao, valor = valor, categoria = categoria, data = data)
            expenseDao.upsertExpense(newExpense)
        }
    }

    fun updateExpense(expenseId: UUID, descricao: String, valor: Double, categoria: Category, data: LocalDate) {
        viewModelScope.launch {
            val expenseToUpdate = Expense(expenseId, descricao, valor, categoria, data)
            expenseDao.upsertExpense(expenseToUpdate)
        }
    }

    fun deleteExpense(expenseId: UUID) {
        viewModelScope.launch {
            expenseDao.getExpenseById(expenseId)?.let { expenseToDelete ->
                expenseDao.deleteExpense(expenseToDelete)
            }
        }
    }
}
