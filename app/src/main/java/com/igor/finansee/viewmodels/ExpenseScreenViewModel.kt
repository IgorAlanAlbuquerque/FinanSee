package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.Expense
import com.igor.finansee.data.models.User
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.ExpenseRepository
import com.igor.finansee.data.states.ExpenseScreenUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class ExpenseScreenViewModel(
    private val expenseRepository: ExpenseRepository,
    categoryRepository: CategoryRepository,
    private val user: User

) : ViewModel() {
    private val _currentUser = MutableStateFlow(user)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _selectedMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))

    init {
        expenseRepository.startListeningForRemoteChanges()
        categoryRepository.startListeningForRemoteChanges()
    }

    private val _uiState = MutableStateFlow(ExpenseScreenUiState(isLoading = true))
    val uiState: StateFlow<ExpenseScreenUiState> = _uiState.asStateFlow()

    val categories: StateFlow<List<Category>> = categoryRepository.getCategoriesFromRoom()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _selectedMonth.collectLatest { month ->
                _uiState.update { it.copy(isLoading = true) }

                // Converte os LocalDate para Date
                val startDate = Date.from(month.atStartOfDay(ZoneId.systemDefault()).toInstant())
                val endDate = Date.from(month.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

                // Passa os objetos Date para o repositório
                val expensesFlow = expenseRepository.getExpensesFromRoom(user.id, startDate, endDate)

                expensesFlow.collect { expenses ->
                    _uiState.update {
                        it.copy(
                            expenses = expenses,
                            selectedMonth = month, // A UI ainda pode usar o LocalDate
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun updateExpense(
        id: String,
        descricao: String,
        valor: Double,
        categoryId: String?,
        data: Date
    ) {
        viewModelScope.launch {
            val userId = user.id
            val updatedExpense = Expense(
                id = id,
                userId = userId,
                descricao = descricao,
                valor = valor,
                categoryId = categoryId,
                data = data
            )

            expenseRepository.upsertExpense(updatedExpense)
        }
    }


    fun deleteExpense(expense: String) {
        viewModelScope.launch {
            expenseRepository.deleteExpenseById(expense)
        }
    }
    fun selectPreviousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
    }

    fun selectNextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
    }

    fun addExpense(descricao: String, valor: Double, categoryId: String?, data: Date) {
        viewModelScope.launch {
            val userId = user.id

            val novaDespesa = Expense(
                userId = userId,
                descricao = descricao,
                valor = valor,
                categoryId = categoryId,
                data = data
            )
            expenseRepository.upsertExpense(novaDespesa)
        }
    }

}
