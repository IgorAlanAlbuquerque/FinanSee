package com.igor.finansee.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igor.finansee.data.models.*
import com.igor.finansee.viewmodels.ExpenseScreenViewModel
import java.util.UUID
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.igor.finansee.data.AppDatabase
import com.igor.finansee.viewmodels.ExpenseScreenViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val factory = ExpenseScreenViewModelFactory(db.expenseDao(), db.categoryDao())
    val viewModel: ExpenseScreenViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()
    val categoryList by viewModel.categories.collectAsState()

    val expensesForMonth = uiState.expenses
    var editingExpenseId by remember { mutableStateOf<UUID?>(null) }

    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableStateOf<Category?>(null) }
    var data by remember { mutableStateOf("") }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun startEditing(expense: Expense) {
        editingExpenseId = expense.id
        descricao = expense.descricao
        valor = expense.valor.toString()
        categoriaSelecionada = expense.categoria
        data = expense.data.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.selectPreviousMonth() }) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Mês anterior", tint = Color.Black)
            }

            Text(
                text = uiState.selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy",
                    Locale("pt", "BR")
                )).replaceFirstChar { it.titlecase(Locale("pt", "BR")) },
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            IconButton(onClick = { viewModel.selectNextMonth() }) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Próximo mês", tint = Color.Black)
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        expensesForMonth.forEach { expense ->
            TextButton(
                onClick = { startEditing(expense) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${expense.descricao} - R$${expense.valor}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (editingExpenseId != null) {
            Text("Editar despesa", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Valor") },
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
                label = { Text("Data (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    val dataLocalDate = try {
                        LocalDate.parse(data)
                    } catch (e: Exception) {
                        null
                    }

                    if (dataLocalDate != null && categoriaSelecionada != null) {
                        viewModel.updateExpense(
                            editingExpenseId!!,
                            descricao,
                            valor.toDoubleOrNull() ?: 0.0,
                            categoriaSelecionada!!,
                            dataLocalDate
                        )
                        editingExpenseId = null
                    }
                }) {
                    Text("Salvar")
                }

                Button(onClick = {
                    viewModel.deleteExpense(editingExpenseId!!)
                    editingExpenseId = null
                }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Excluir")
                }
            }
        }
    }
}
