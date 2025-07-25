import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.AuthRepository
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.ExpenseDao
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.Expense
import com.igor.finansee.data.models.ExpenseWithCategory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

data class ExpenseUiState(
    val expenses: List<ExpenseWithCategory> = emptyList(),
    val selectedMonth: LocalDate = LocalDate.now().withDayOfMonth(1),
    val isLoading: Boolean = false
)

class ExpenseScreenViewModel(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val authRepository: AuthRepository

) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))

    private val _uiState = MutableStateFlow(ExpenseUiState(isLoading = true))
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    val categories: StateFlow<List<Category>> = categoryDao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _selectedMonth.collectLatest { month ->
                _uiState.update { it.copy(isLoading = true) }

                val start = month
                val end = month.plusMonths(1)
                val expensesFlow = expenseDao.getExpensesWithCategoryBetween(start, end)

                expensesFlow.collect { expenses ->
                    _uiState.update {
                        it.copy(
                            expenses = expenses,
                            selectedMonth = month,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun updateExpense(
        id: UUID,
        descricao: String,
        valor: Double,
        categoryId: Int?,
        data: LocalDate
    ) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentLocalUser()?.id?.toString() ?: return@launch
            val updatedExpense = Expense(
                id = id,
                userId = userId,
                descricao = descricao,
                valor = valor,
                categoryId = categoryId,
                data = data
            )

            expenseDao.upsertExpense(updatedExpense)
        }
    }


    fun deleteExpense(expense: UUID) {
        viewModelScope.launch {
            expenseDao.deleteExpenseById(expense)
        }
    }
    fun selectPreviousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
    }

    fun selectNextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
    }

    fun addExpense(descricao: String, valor: Double, categoryId: Int?, data: LocalDate) {
        viewModelScope.launch {
            val userId = authRepository.getFirebaseUserId()

            if (userId != null && categoryId != null) {
                val novaDespesa = Expense(
                    userId = userId,
                    descricao = descricao,
                    valor = valor,
                    categoryId = categoryId,
                    data = data
                )
                expenseDao.upsertExpense(novaDespesa)
            }
        }
    }

}
