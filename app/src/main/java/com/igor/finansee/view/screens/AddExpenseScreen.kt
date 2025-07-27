package com.igor.finansee.view.screens

import com.igor.finansee.viewmodels.ExpenseScreenViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.igor.finansee.data.AppDatabase
import com.igor.finansee.data.models.Category
import com.igor.finansee.viewmodels.AuthViewModel
import com.igor.finansee.viewmodels.ExpenseScreenViewModelFactory
import java.time.LocalDate
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    val factory = remember(user) {
        val db = AppDatabase.getDatabase(context)
        ExpenseScreenViewModelFactory(db.expenseDao(), db.categoryDao(), user)
    }

    val viewModel: ExpenseScreenViewModel = viewModel(factory = factory)

    val categoryList by viewModel.categories.collectAsState()

    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableStateOf<Category?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val dataAtual = Date()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Cadastrar Despesa",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = categoriaSelecionada?.name ?: "Selecione uma categoria",
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoria") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categoryList.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria.name) },
                        onClick = {
                            categoriaSelecionada = categoria
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Data: $dataAtual")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val valorDouble = valor.toDoubleOrNull()
                if (valorDouble != null && categoriaSelecionada != null && descricao.isNotBlank()) {
                    viewModel.addExpense(
                        descricao = descricao,
                        valor = valorDouble,
                        categoryId = categoriaSelecionada?.id,
                        data = dataAtual
                    )
                    navController.popBackStack()
                }
            },
            enabled = valor.isNotBlank() && categoriaSelecionada != null && descricao.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Despesa")
        }
    }
}