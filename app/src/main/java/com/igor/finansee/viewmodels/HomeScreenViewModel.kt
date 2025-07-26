package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.models.*
import com.igor.finansee.data.repository.BankAccountRepository
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.CreditCardRepository
import com.igor.finansee.data.repository.FaturaCreditCardRepository
import com.igor.finansee.data.repository.MonthPlanningRepository
import com.igor.finansee.data.repository.TransactionRepository
import com.igor.finansee.data.states.HomeScreenUiState
import com.igor.finansee.view.screens.CategoryWithAmount
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val transactionRepository: TransactionRepository,
    private val planningRepository: MonthPlanningRepository,
    private val faturaRepository: FaturaCreditCardRepository,
    private val creditCardRepository: CreditCardRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val categoryRepository: CategoryRepository,
    private val user: User
) : ViewModel() {

    private val _currentUser = MutableStateFlow(user)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()


    init {
        transactionRepository.startListeningForRemoteChanges()
        planningRepository.startListeningForRemoteChanges()
        creditCardRepository.startListeningForRemoteChanges()
        faturaRepository.startListeningForRemoteChanges()
        bankAccountRepository.startListeningForRemoteChanges()
        categoryRepository.startListeningForRemoteChanges()
    }

    private val _selectedMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    private val _showBalance = MutableStateFlow(true)
    private val expenseTypes = listOf(TransactionType.EXPENSE, TransactionType.CREDIT_CARD_EXPENSE)

    private val _staticDataFlow = combine(
        bankAccountRepository.getOverallBalanceFromRoom(),
        bankAccountRepository.getAccountsFromRoom(),
        creditCardRepository.getCardsFromRoom(),
        categoryRepository.getCategoriesFromRoom()
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
        _currentUser,
        _selectedMonth,
        _showBalance,
        _staticDataFlow,
        _monthlyDataFlow
    ) { currentUser: User?,
        month: LocalDate,
        showBalance: Boolean,
        staticData: StaticData,
        monthlyData: MonthlyData ->
        val userName = currentUser?.name?.split(" ")?.firstOrNull() ?: "Utilizador"

        HomeScreenUiState(
            user = currentUser,
            userName = userName,
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
            selectedDate = _selectedMonth.value
        )
    )

    private fun createMonthlyDataFlow(month: LocalDate): Flow<MonthlyData> {
        val startDate = month
        val endDate = month.plusMonths(1)
        return combine(
            transactionRepository.getSumByTypesForPeriod(listOf(TransactionType.INCOME), startDate, endDate),
            transactionRepository.getSumByTypesForPeriod(expenseTypes, startDate, endDate),
            faturaRepository.getFaturasFromRoom(startDate, endDate),
            transactionRepository.getExpensesGroupedByCategory(expenseTypes, startDate, endDate),
            planningRepository.getPlanningFromRoom(startDate, endDate)
        ) { income, directExpenses, faturas, expensesByCategory, planning ->
            val totalExpenses = (directExpenses ?: 0.0) + faturas.sumOf { it.valor }
            MonthlyData(income ?: 0.0, totalExpenses, faturas, expensesByCategory, planning)
        }
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

    override fun onCleared() {
        super.onCleared()
        transactionRepository.cancelScope()
        planningRepository.cancelScope()
        creditCardRepository.cancelScope()
        faturaRepository.cancelScope()
        bankAccountRepository.cancelScope()
        categoryRepository.cancelScope()
    }
}
