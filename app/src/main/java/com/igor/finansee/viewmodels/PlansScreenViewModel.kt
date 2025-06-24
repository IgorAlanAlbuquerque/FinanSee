package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.data.models.mockMonthPlanningList
import com.igor.finansee.data.models.transactionList
import com.igor.finansee.data.states.CategoryPlanWithProgress
import com.igor.finansee.data.states.PlansScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.TextStyle
import java.util.Locale

class PlansScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PlansScreenUiState())
    val uiState: StateFlow<PlansScreenUiState> = _uiState.asStateFlow()

    fun loadInitialData(userId: Int = 1) {
        updateData(userId)
    }

    fun selectNextMonth() {
        _uiState.update { it.copy(selectedDate = it.selectedDate.plusMonths(1)) }
        updateData()
    }

    fun selectPreviousMonth() {
        _uiState.update { it.copy(selectedDate = it.selectedDate.minusMonths(1)) }
        updateData()
    }

    private fun updateData(userId: Int = 1) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val date = _uiState.value.selectedDate

            val planning = mockMonthPlanningList.find {
                it.monthYear.month == date.month && it.monthYear.year == date.year
            }

            var planDetails = listOf<CategoryPlanWithProgress>()
            var totalPlanned = 0.0

            if (planning != null) {
                totalPlanned = planning.categorySpendingPlan.sumOf { it.plannedAmount }
                planDetails = planning.categorySpendingPlan.map { categoryPlan ->
                    val actualSpending = transactionList
                        .filter { t ->
                            t.userId == userId &&
                                    t.date.year == date.year &&
                                    t.date.month == date.month &&
                                    t.categoryId == categoryPlan.categoryId &&
                                    (t.type == TransactionType.EXPENSE || t.type == TransactionType.CREDIT_CARD_EXPENSE)
                        }.sumOf { it.value }

                    val progress = if (categoryPlan.plannedAmount > 0) {
                        (actualSpending / categoryPlan.plannedAmount).toFloat()
                    } else 0f

                    CategoryPlanWithProgress(
                        categoryId = categoryPlan.categoryId,
                        categoryName = "Categoria ${categoryPlan.categoryId}",
                        plannedAmount = categoryPlan.plannedAmount,
                        actualAmount = actualSpending,
                        progress = progress.coerceIn(0f, 1f)
                    )
                }
            }

            val monthDisplayName = date.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() } + " " + date.year

            _uiState.update {
                it.copy(
                    isLoading = false,
                    monthDisplayName = monthDisplayName,
                    currentPlanning = planning,
                    totalPlannedAmount = totalPlanned,
                    planDetailsWithProgress = planDetails
                )
            }
        }
    }
}