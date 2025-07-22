package com.igor.finansee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igor.finansee.data.AppDatabase
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.ui.components.DonutChart
import com.igor.finansee.viewmodels.ExpenseScreenViewModel
import com.igor.finansee.viewmodels.ExpenseScreenViewModelFactory
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonutChartScreen() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val factory = ExpenseScreenViewModelFactory(db.expenseDao(), db.categoryDao())
    val viewModel: ExpenseScreenViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()
    val allCategories by viewModel.categories.collectAsState()

    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Gráfico",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.selectPreviousMonth() }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Mês anterior", tint = Color.Black)
                }

                Text(
                    text = uiState.selectedMonth.format(formatter).replaceFirstChar { it.uppercase() },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                IconButton(onClick = { viewModel.selectNextMonth() }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Próximo mês", tint = Color.Black)
                }
            }

            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Distribuição de despesas por categoria",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DonutChart(
                expenses = uiState.expenses,
                allCategories = allCategories,
                transactionType = TransactionType.EXPENSE,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}
