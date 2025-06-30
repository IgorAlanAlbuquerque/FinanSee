package com.igor.finansee.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igor.finansee.data.models.*
import com.igor.finansee.viewmodels.ExpenseScreenViewModel
import java.util.UUID
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
@Composable
fun EditExpenseScreen(
    viewModel: ExpenseScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val expensesForMonth = uiState.expenses
    var editingExpenseId by remember { mutableStateOf<UUID?>(null) }
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    fun startEditing(expense: Expense) {
        editingExpenseId = expense.id
        descricao = expense.descricao
        valor = expense.valor.toString()
        categoria = expense.categoria.name
        data = expense.data.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Linha para navegação entre meses
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
                text = uiState.monthDisplayName,
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

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoria") },
                modifier = Modifier.fillMaxWidth()
            )

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
                val categoryList = com.igor.finansee.data.models.categoryList
                Button(onClick = {
                    val selectedCategory = categoryList.find { it.name == categoria }
                    selectedCategory?.let { category ->
                        viewModel.updateExpense(
                            editingExpenseId!!,
                            descricao,
                            valor.toDoubleOrNull() ?: 0.0,
                            category,
                            data
                        )
                        editingExpenseId = null
                    }
                }) {
                    Text("Salvar")
                }

                Button(onClick = {
                    viewModel.deleteExpense(editingExpenseId!!)
                    editingExpenseId = null
                }) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
