package com.igor.finansee.data.states

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.igor.finansee.data.models.Expense

data class ExpenseScreenUiState(
    val isLoading: Boolean = false,
    val expenses: List<Expense> = emptyList(),
    val totalExpenses: Double = 0.0,
    val selectedMonth: LocalDate = LocalDate.now().withDayOfMonth(1)
) {
    val monthDisplayName: String
        get() = selectedMonth.format(
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale("pt", "BR"))
        ).replaceFirstChar { it.uppercase() }
}
