package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.MonthPlanningRepository
import java.time.LocalDate

class CreatePlanViewModelFactory(
    private val monthPlanningRepository: MonthPlanningRepository,
    private val categoryRepository: CategoryRepository,
    private val userId: String,
    private val selectedMonth: LocalDate
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreatePlanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreatePlanViewModel(monthPlanningRepository, categoryRepository, userId, selectedMonth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
