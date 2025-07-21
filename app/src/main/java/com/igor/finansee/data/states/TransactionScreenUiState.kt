package com.igor.finansee.data.states

import com.igor.finansee.data.models.Transaction
import java.time.LocalDate

data class TransactionScreenUiState(
    val selectedMonth: LocalDate,
    val transactionsByDate: Map<LocalDate, List<Transaction>> = emptyMap(),
    val monthlyBalance: Double = 0.0,
    val currentOverallBalance: Double = 0.0,
    val isLoading: Boolean = true
)