package com.igor.finansee.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.igor.finansee.data.AppDatabase
import com.igor.finansee.data.repository.CategoryRepository
import com.igor.finansee.data.repository.MonthPlanningRepository
import com.igor.finansee.viewmodels.AuthViewModel
import com.igor.finansee.viewmodels.CreatePlanViewModel
import com.igor.finansee.viewmodels.CreatePlanViewModelFactory
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlanScreen(
    authViewModel: AuthViewModel,
    selectedMonth: String,
    onNavigateBack: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val month = LocalDate.parse(selectedMonth)

    currentUser?.let { user ->
        val factory = remember(user, month) {
            val db = AppDatabase.getDatabase(context)
            val firestore = Firebase.firestore
            CreatePlanViewModelFactory(
                monthPlanningRepository = MonthPlanningRepository(db.monthPlanningDao(), firestore, user.id),
                categoryRepository = CategoryRepository(db.categoryDao(), firestore),
                userId = user.id,
                selectedMonth = month
            )
        }
        val viewModel: CreatePlanViewModel = viewModel(factory = factory)

        val totalIncome by viewModel.totalIncome.collectAsState()
        val planInputs by viewModel.planInputs.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.navigateBack.collect {
                onNavigateBack()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Plano de ${month.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))}") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
            },
            bottomBar = {
                Button(
                    onClick = { viewModel.savePlan() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !isLoading // Desabilita o botão enquanto salva
                ) {
                    Text("Salvar Planejamento")
                }
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = totalIncome,
                            onValueChange = { viewModel.onTotalIncomeChange(it) },
                            label = { Text("Renda Mensal Total") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            prefix = { Text("R$ ") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Planejamento por Categoria", style = MaterialTheme.typography.titleMedium)
                    }

                    items(planInputs) { inputState ->
                        OutlinedTextField(
                            value = inputState.amount,
                            onValueChange = { newAmount -> viewModel.onAmountChange(inputState.category.id, newAmount) },
                            label = { Text(inputState.category.name) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            prefix = { Text("R$ ") }
                        )
                    }
                }
            }
        }
    }
}
