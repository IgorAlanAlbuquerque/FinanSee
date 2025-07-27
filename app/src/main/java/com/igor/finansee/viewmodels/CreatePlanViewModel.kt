package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.MonthPlanning
import com.igor.finansee.data.models.PlannedCategorySpending
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.MonthPlanningRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

data class PlanInputState(
    val category: Category,
    var amount: String
)

class CreatePlanViewModel(
    private val monthPlanningRepository: MonthPlanningRepository,
    private val categoryRepository: CategoryRepository,
    private val userId: String,
    private val selectedMonth: LocalDate
) : ViewModel() {

    private val _totalIncome = MutableStateFlow("")
    val totalIncome: StateFlow<String> = _totalIncome.asStateFlow()

    private val _planInputs = MutableStateFlow<List<PlanInputState>>(emptyList())
    val planInputs: StateFlow<List<PlanInputState>> = _planInputs.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _navigateBack = MutableSharedFlow<Boolean>()
    val navigateBack: SharedFlow<Boolean> = _navigateBack.asSharedFlow()

    init {
        loadDataForMonth()
    }

    private fun loadDataForMonth() {
        viewModelScope.launch {
            _isLoading.value = true

            val existingPlan = monthPlanningRepository.getPlanningForMonthOnce(selectedMonth)
            val allCategories = categoryRepository.getAllCategoriesOnce()

            _totalIncome.value = existingPlan?.totalMonthlyIncome?.toString() ?: ""

            _planInputs.value = allCategories.map { category ->
                val existingCategoryPlan = existingPlan?.categorySpendingPlan?.find { it.categoryId == category.id }
                PlanInputState(
                    category = category,
                    amount = existingCategoryPlan?.plannedAmount?.toString() ?: ""
                )
            }
            _isLoading.value = false
        }
    }

    fun onTotalIncomeChange(newIncome: String) {
        _totalIncome.value = newIncome
    }

    fun onAmountChange(categoryId: String, newAmount: String) {
        val updatedList = _planInputs.value.map {
            if (it.category.id == categoryId) {
                it.copy(amount = newAmount)
            } else {
                it
            }
        }
        _planInputs.value = updatedList
    }

    fun savePlan() {
        viewModelScope.launch {
            val categorySpendingPlans = _planInputs.value.map { inputState ->
                PlannedCategorySpending(
                    categoryId = inputState.category.id,
                    plannedAmount = inputState.amount.toDoubleOrNull() ?: 0.0
                )
            }

            val monthAsDate = Date.from(selectedMonth.atStartOfDay(ZoneId.systemDefault()).toInstant())

            val idFormatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val planId = "${userId}_${idFormatter.format(monthAsDate)}"

            val planToSave = MonthPlanning(
                id = planId,
                userId = userId,
                monthYear = monthAsDate,
                totalMonthlyIncome = _totalIncome.value.toDoubleOrNull() ?: 0.0,
                targetSpendingPercentage = 0.0,
                categorySpendingPlan = categorySpendingPlans
            )

            monthPlanningRepository.upsertPlanning(planToSave)
            _navigateBack.emit(true)
        }
    }
}