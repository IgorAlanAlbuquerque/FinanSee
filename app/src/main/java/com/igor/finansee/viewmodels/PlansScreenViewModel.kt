package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.daos.MonthPlanningDao
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.data.models.User
import com.igor.finansee.data.states.CategoryPlanWithProgress
import com.igor.finansee.data.states.PlansScreenUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class PlansScreenViewModel(
    private val planningDao: MonthPlanningDao,
    private val transactionDao: TransactionDao,
    private val currentUser: User
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now().withDayOfMonth(1))

    private val expenseTypes = listOf(TransactionType.EXPENSE, TransactionType.CREDIT_CARD_EXPENSE)

    val uiState: StateFlow<PlansScreenUiState> = _selectedDate.flatMapLatest { date ->
        val startDate = date
        val endDate = date.plusMonths(1)

        combine(
            planningDao.getPlanningForUserInPeriod(currentUser.id, startDate, endDate),
            transactionDao.getExpensesGroupedByCategory(currentUser.id, startDate, endDate, expenseTypes)
        ) { planning, actualSpendingResults ->

            var planDetails = listOf<CategoryPlanWithProgress>()

            if (planning != null) {
                planDetails = planning.categorySpendingPlan.map { categoryPlan ->
                    val actualSpending = actualSpendingResults
                        .find { it.categoryId == categoryPlan.categoryId }?.totalAmount ?: 0.0

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
}