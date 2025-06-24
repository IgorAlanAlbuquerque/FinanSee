package com.igor.finansee.data.states

import com.igor.finansee.data.models.BankAccount
import com.igor.finansee.data.models.CreditCard
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.FaturaCreditCard
import com.igor.finansee.data.models.MonthPlanning
import com.igor.finansee.ui.screens.CategoryWithAmount
import java.time.LocalDate

data class HomeScreenUiState(
    val userName: String = "",
    val showBalance: Boolean = true,
    val selectedDate: LocalDate = LocalDate.now(),
    val totalAccountBalance: Double = 0.0,
    val incomeForSelectedMonth: Double = 0.0,
    val expensesForSelectedMonth: Double = 0.0,
    val userBankAccounts: List<BankAccount> = emptyList(),
    val userCreditCards: List<CreditCard> = emptyList(),
    val currentMonthFaturas: List<FaturaCreditCard> = emptyList(),
    val totalCreditCardInvoiceAmount: Double = 0.0,
    val expensesByCategory: List<CategoryWithAmount> = emptyList(),
    val currentMonthPlanning: MonthPlanning? = null,
    val allCategories: List<Category> = emptyList()
)