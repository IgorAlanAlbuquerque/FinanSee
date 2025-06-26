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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.data.models.expenseList
import com.igor.finansee.ui.components.DonutChart
import com.igor.finansee.ui.theme.BackgroundGray
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonutChartScreen() {
    var selectedMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    val filteredExpenses = expenseList.filter { it.monthYear == selectedMonth }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Grafíco",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    selectedMonth = selectedMonth.minusMonths(1)
                }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Mês anterior", tint = Color.Black)
                }

                Text(
                    text = selectedMonth.format(formatter).replaceFirstChar { it.uppercase() },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                IconButton(onClick = {
                    selectedMonth = selectedMonth.plusMonths(1)
                }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Próximo mês", tint = Color.Black)
                }
            }

            Text(
                text = "Distribuição de despesas por categoria",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DonutChart(
                expenses = filteredExpenses,
                transactionType = TransactionType.EXPENSE,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}
