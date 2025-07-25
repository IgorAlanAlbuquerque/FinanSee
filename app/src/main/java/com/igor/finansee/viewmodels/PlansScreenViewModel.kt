package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.MonthPlanning
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.MonthPlanningRepository
import com.igor.finansee.data.repository.TransactionRepository
import com.igor.finansee.data.states.CategoryPlanWithProgress
import com.igor.finansee.data.states.PlansScreenUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class PlansScreenViewModel(
    private val planningRepository: MonthPlanningRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    private val expenseTypes = listOf(TransactionType.EXPENSE, TransactionType.CREDIT_CARD_EXPENSE)

    init {
        planningRepository.startListeningForRemoteChanges()
        transactionRepository.startListeningForRemoteChanges()
        categoryRepository.startListeningForRemoteChanges()
    }

    val uiState: StateFlow<PlansScreenUiState> = _selectedDate.flatMapLatest { date ->
        val startDate = date
        val endDate = date.plusMonths(1)

        combine(
            planningRepository.getPlanningFromRoom(startDate, endDate),
            transactionRepository.getExpensesGroupedByCategory(expenseTypes, startDate, endDate),
            categoryRepository.getCategoriesFromRoom()
        ) { planning, actualSpendingResults, allCategories ->

            var planDetails = listOf<CategoryPlanWithProgress>()

            if (planning != null) {
                planDetails = planning.categorySpendingPlan.map { categoryPlan ->
                    val actualSpending = actualSpendingResults
                        .find { it.categoryId == categoryPlan.categoryId }?.totalAmount ?: 0.0

                    val categoryName = allCategories.find { it.id == categoryPlan.categoryId }?.name ?: "Desconhecida"

                    val progress = if (categoryPlan.plannedAmount > 0) {
                        (actualSpending / categoryPlan.plannedAmount).toFloat()
                    } else if (actualSpending > 0) 1f else 0f

                    CategoryPlanWithProgress(
                        categoryId = categoryPlan.categoryId,
                        categoryName = categoryName, // Usa o nome real da categoria
                        plannedAmount = categoryPlan.plannedAmount,
                        actualAmount = actualSpending,
                        progress = progress.coerceIn(0f, 1f)
                    )
                }
            }

            val monthDisplayName = date.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                .replaceFirstChar { it.titlecase(Locale("pt", "BR")) } + " " + date.year

            PlansScreenUiState(
                isLoading = false,
                selectedDate = date,
                monthDisplayName = monthDisplayName,
                currentPlanning = planning,
                totalPlannedAmount = planning?.categorySpendingPlan?.sumOf { it.plannedAmount } ?: 0.0,
                planDetailsWithProgress = planDetails
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = PlansScreenUiState(isLoading = true)
    )

    fun selectNextMonth() {
        _selectedDate.value = _selectedDate.value.plusMonths(1)
    }

    fun selectPreviousMonth() {
        _selectedDate.value = _selectedDate.value.minusMonths(1)
    }

    fun savePlanning(planning: MonthPlanning) {
        viewModelScope.launch {
            planningRepository.savePlanning(planning)
        }
    }

    override fun onCleared() {
        super.onCleared()
        planningRepository.cancelScope()
        transactionRepository.cancelScope()
        categoryRepository.cancelScope()
    }
}