package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.CreditCardDao
import com.igor.finansee.data.daos.FaturaCreditCardDao
import com.igor.finansee.data.daos.MonthPlanningDao
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.daos.CategoryExpenseResult
import com.igor.finansee.data.models.*
import com.igor.finansee.data.states.HomeScreenUiState
import com.igor.finansee.ui.screens.CategoryWithAmount
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

private data class MonthlyData(
    val income: Double,
    val totalExpenses: Double,
    val faturas: List<FaturaCreditCard>,
    val expensesByCategory: List<CategoryExpenseResult>,
    val planning: MonthPlanning?
)

private data class StaticData(
    val balance: Double,
    val accounts: List<BankAccount>,
    val cards: List<CreditCard>,
    val categories: List<Category>
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModel(
    private val bankAccountDao: BankAccountDao,
    private val creditCardDao: CreditCardDao,
    private val faturaDao: FaturaCreditCardDao,
    private val transactionDao: TransactionDao,
    private val planningDao: MonthPlanningDao,
    private val categoryDao: CategoryDao,
    private val currentUser: User
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    private val _showBalance = MutableStateFlow(true)
    private val expenseTypes = listOf(TransactionType.EXPENSE, TransactionType.CREDIT_CARD_EXPENSE)

    private val _staticDataFlow = combine(
        bankAccountDao.getOverallBalanceForUser(currentUser.id),
        bankAccountDao.getAccountsForUser(currentUser.id),
        creditCardDao.getCardsForUser(currentUser.id),
        categoryDao.getAllCategories()
    ) { balance, accounts, cards, categories ->
        StaticData(
            balance = balance ?: 0.0,
            accounts = accounts,
            cards = cards,
            categories = categories
        )
    }
    private val _monthlyDataFlow: Flow<MonthlyData> = _selectedMonth.flatMapLatest { month ->
        createMonthlyDataFlow(month)
    }

    val uiState: StateFlow<HomeScreenUiState> = combine(
        _selectedMonth,
        _showBalance,
        _staticDataFlow,
        _monthlyDataFlow
    ) { month, showBalance, staticData, monthlyData ->
        HomeScreenUiState(
            userName = currentUser.name.split(" ").firstOrNull() ?: "",
            selectedDate = month,
            showBalance = showBalance,
            totalAccountBalance = staticData.balance,
            incomeForSelectedMonth = monthlyData.income,
            expensesForSelectedMonth = monthlyData.totalExpenses,
            userBankAccounts = staticData.accounts,
            userCreditCards = staticData.cards,
            currentMonthFaturas = monthlyData.faturas,
            totalCreditCardInvoiceAmount = monthlyData.faturas.sumOf { it.valor },
            expensesByCategory = mapCategoryExpenses(monthlyData.expensesByCategory, staticData.categories),
            currentMonthPlanning = monthlyData.planning,
            allCategories = staticData.categories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeScreenUiState(
            userName = currentUser.name.split(" ").firstOrNull() ?: "",
            selectedDate = _selectedMonth.value
        )
    )

    private fun createMonthlyDataFlow(month: LocalDate) = combine(
        transactionDao.getSumByTypesForPeriod(currentUser.id, listOf(TransactionType.INCOME), month, month.plusMonths(1)),
        transactionDao.getSumByTypesForPeriod(currentUser.id, expenseTypes, month, month.plusMonths(1)),
        faturaDao.getFaturasForUserInPeriod(currentUser.id, month, month.plusMonths(1)),
        transactionDao.getExpensesGroupedByCategory(currentUser.id, month, month.plusMonths(1), expenseTypes),
        planningDao.getPlanningForUserInPeriod(currentUser.id, month, month.plusMonths(1))
    ) { income, directExpenses, faturas, expensesByCategory, planning ->
        val totalExpenses = (directExpenses ?: 0.0) + faturas.sumOf { it.valor }
        MonthlyData(income ?: 0.0, totalExpenses, faturas, expensesByCategory, planning)
    }

    private fun mapCategoryExpenses(results: List<CategoryExpenseResult>, categories: List<Category>): List<CategoryWithAmount> {
        return results.map { result ->
            val category = categories.find { it.id == result.categoryId } ?: Category(result.categoryId, "Desconhecida")
            CategoryWithAmount(category, result.totalAmount)
        }
    }

    fun toggleBalanceVisibility() { _showBalance.value = !_showBalance.value }
    fun selectNextMonth() { _selectedMonth.value = _selectedMonth.value.plusMonths(1) }
    fun selectPreviousMonth() { _selectedMonth.value = _selectedMonth.value.minusMonths(1) }
}