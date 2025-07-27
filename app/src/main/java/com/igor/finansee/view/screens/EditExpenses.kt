package com.igor.finansee.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igor.finansee.data.AppDatabase
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.ExpenseWithCategory
import com.igor.finansee.viewmodels.AuthViewModel
import com.igor.finansee.viewmodels.ExpenseScreenViewModel
import com.igor.finansee.viewmodels.ExpenseScreenViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    val factory = remember(user) {
        val db = AppDatabase.getDatabase(context)
        ExpenseScreenViewModelFactory(db.expenseDao(), db.categoryDao(), user)
    }

    val viewModel: ExpenseScreenViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val categoryList by viewModel.categories.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var expenseToEdit by remember { mutableStateOf<ExpenseWithCategory?>(null) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            MonthSelector(
                monthDisplayName = uiState.selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("pt", "BR")))
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() },
                onPreviousMonth = { viewModel.selectPreviousMonth() },
                onNextMonth = { viewModel.selectNextMonth() }
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.expenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma despesa encontrada para este mês.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(uiState.expenses, key = { it.expense.id }) { expenseItem ->
                        ExpenseCard(
                            expenseWithCategory = expenseItem,
                            onClick = {
                                expenseToEdit = expenseItem
                                scope.launch { sheetState.show() }
                            }
                        )
                    }
                }
            }
        }

        if (sheetState.isVisible && expenseToEdit != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        expenseToEdit = null
                    }
                },
                sheetState = sheetState
            ) {
                EditExpenseForm(
                    expenseToEdit = expenseToEdit!!,
                    categoryList = categoryList,
                    onSave = { id, desc, valor, catId, data ->
                        viewModel.updateExpense(id, desc, valor, catId, data)
                        scope.launch {
                            sheetState.hide()
                            expenseToEdit = null
                        }
                    },
                    onDelete = {
                        viewModel.deleteExpenseById(it)
                        scope.launch {
                            sheetState.hide()
                            expenseToEdit = null
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun MonthSelector(
    monthDisplayName: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Mês anterior")
        }
        Text(
            text = monthDisplayName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Próximo mês")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpenseCard(
    expenseWithCategory: ExpenseWithCategory,
    onClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = "Despesa",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expenseWithCategory.expense.descricao,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = expenseWithCategory.category?.name ?: "Sem categoria",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "R$ %.2f".format(expenseWithCategory.expense.valor),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp
                )
                Text(
                    text = dateFormatter.format(expenseWithCategory.expense.data),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditExpenseForm(
    expenseToEdit: ExpenseWithCategory,
    categoryList: List<Category>,
    onSave: (String, String, Double, String?, Date) -> Unit,
    onDelete: (String) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var descricao by remember(expenseToEdit) { mutableStateOf(expenseToEdit.expense.descricao) }
    var valor by remember(expenseToEdit) { mutableStateOf(expenseToEdit.expense.valor.toString()) }
    var categoriaSelecionada by remember(expenseToEdit) { mutableStateOf(expenseToEdit.category) }
    var data by remember(expenseToEdit) { mutableStateOf(dateFormatter.format(expenseToEdit.expense.data)) }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Editar Despesa", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { onDelete(expenseToEdit.expense.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
            }
        }

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
            label = { Text("Valor") },
            prefix = { Text("R$ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        ExposedDropdownMenuBox(
            expanded = expandedCategoryDropdown,
            onExpandedChange = { expandedCategoryDropdown = !expandedCategoryDropdown }
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                value = categoriaSelecionada?.name ?: "Selecione uma categoria",
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoria") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown) }
            )
            ExposedDropdownMenu(
                expanded = expandedCategoryDropdown,
                onDismissRequest = { expandedCategoryDropdown = false }
            ) {
                categoryList.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            categoriaSelecionada = category
                            expandedCategoryDropdown = false
                        }
                    )
                }
            }
        }
        OutlinedTextField(
            value = data,
            onValueChange = { data = it },
            label = { Text("Data (dd/MM/yyyy)") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val dataDate = try {
                    dateFormatter.parse(data)
                } catch (e: Exception) { null }

                if (dataDate != null) {
                    onSave(
                        expenseToEdit.expense.id,
                        descricao,
                        valor.replace(",", ".").toDoubleOrNull() ?: 0.0,
                        categoriaSelecionada?.id,
                        dataDate
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Alterações")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
