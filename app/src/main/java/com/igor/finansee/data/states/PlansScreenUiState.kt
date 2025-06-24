package com.igor.finansee.data.states

import com.igor.finansee.data.models.MonthPlanning
import java.time.LocalDate

data class PlansScreenUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val monthDisplayName: String = "",
    val currentPlanning: MonthPlanning? = null,
    val totalPlannedAmount: Double = 0.0,
    val planDetailsWithProgress: List<CategoryPlanWithProgress> = emptyList(),
    val isLoading: Boolean = false
)