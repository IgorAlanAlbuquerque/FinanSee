package com.igor.finansee.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.MonthPlanning
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.data.notifications.NotificationScheduler
import com.igor.finansee.data.repository.*
import com.igor.finansee.data.states.CategoryPlanWithProgress
import com.igor.finansee.data.states.PlansScreenUiState
import com.igor.finansee.utils.NotificationTracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class PlansScreenViewModel(
    private val planningRepository: MonthPlanningRepository,
    private val transactionRepository: TransactionRepository,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val context: Context
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    private val expenseTypes = listOf(TransactionType.EXPENSE, TransactionType.CREDIT_CARD_EXPENSE)
    private val notificationTracker = NotificationTracker(context)

    init {
        planningRepository.startListeningForRemoteChanges()
        transactionRepository.startListeningForRemoteChanges()
        expenseRepository.startListeningForRemoteChanges()
        categoryRepository.startListeningForRemoteChanges()
    }

    val uiState: StateFlow<PlansScreenUiState> = _selectedDate.flatMapLatest { date ->
        val startDateAsDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endDateAsDate = Date.from(date.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

        val transactionSpendingFlow = transactionRepository.getExpensesGroupedByCategory(expenseTypes, startDateAsDate, endDateAsDate)
        val expenseSpendingFlow = expenseRepository.getExpenseGroupedByCategory(startDateAsDate, endDateAsDate)

        combine(
            planningRepository.getPlanningFromRoom(date, date.plusMonths(1)),
            transactionSpendingFlow,
            expenseSpendingFlow,
            categoryRepository.getCategoriesFromRoom()
        ) { planning, transactionSpending, expenseSpending, allCategories ->

            val combinedSpending = (transactionSpending + expenseSpending)
                .groupBy { it.categoryId }
                .mapValues { entry -> entry.value.sumOf { it.totalAmount } }

            val totalActualAmount = combinedSpending.values.sum()

            var planDetails = listOf<CategoryPlanWithProgress>()

            if (planning != null) {
                planDetails = planning.categorySpendingPlan.map { categoryPlan ->
                    val actualSpending = combinedSpending[categoryPlan.categoryId] ?: 0.0
                    val categoryName = allCategories.find { it.id == categoryPlan.categoryId }?.name ?: "Desconhecida"

                    if (categoryPlan.plannedAmount > 0 && actualSpending >= categoryPlan.plannedAmount) {
                        if (!notificationTracker.hasNotificationBeenSent(date, categoryPlan.categoryId)) {
                            val uniqueWorkName = "budget_alert_${date.year}_${date.monthValue}_${categoryPlan.categoryId}"
                            NotificationScheduler.schedule(
                                context = context,
                                delay = 0,
                                timeUnit = TimeUnit.SECONDS,
                                uniqueWorkName = uniqueWorkName,
                                title = "Limite de Gastos Atingido!",
                                message = "Você atingiu o limite para a categoria: $categoryName"
                            )
                            notificationTracker.markNotificationAsSent(date, categoryPlan.categoryId)
                        }
                    }

                    val progress = if (categoryPlan.plannedAmount > 0) {
                        (actualSpending / categoryPlan.plannedAmount).toFloat()
                    } else if (actualSpending > 0) 1f else 0f

                    CategoryPlanWithProgress(
                        categoryId = categoryPlan.categoryId,
                        categoryName = categoryName,
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
                totalActualAmount = totalActualAmount,
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
            planningRepository.upsertPlanning(planning)
        }
    }

    override fun onCleared() {
        super.onCleared()
        planningRepository.cancelScope()
        transactionRepository.cancelScope()
        expenseRepository.cancelScope()
        categoryRepository.cancelScope()
    }
}
