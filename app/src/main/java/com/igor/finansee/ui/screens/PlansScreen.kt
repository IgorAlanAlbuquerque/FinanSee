package com.igor.finansee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.igor.finansee.models.MonthPlanning
import com.igor.finansee.models.mockMonthPlanningList
import com.igor.finansee.ui.theme.BackgroundGray
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun PlansScreen(navController: NavHostController) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val currentPlanning: MonthPlanning? = mockMonthPlanningList.find {
        it.monthYear.month == selectedDate.month && it.monthYear.year == selectedDate.year
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título
            Text(
                text = "Planejamento Financeiro",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Seletor de mês
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    selectedDate = selectedDate.minusMonths(1)
                }) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Mês anterior",
                        tint = Color.Black
                    )
                }

                Text(
                    text = selectedDate.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                        .replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale(
                                    "pt", "BR"
                                )
                            ) else it.toString()
                        } + " " + selectedDate.year,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black)

                IconButton(onClick = {
                    selectedDate = selectedDate.plusMonths(1)
                }) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Próximo mês",
                        tint = Color.Black
                    )
                }
            }

            // Conteúdo principal
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (currentPlanning != null) {
                        val totalPlanejado =
                            currentPlanning.categorySpendingPlan.sumOf { it.plannedAmount }

                        Text(
                            text = "Valor total a ser gasto no mês: R$ %.2f".format(totalPlanejado),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn {
                            items(currentPlanning.categorySpendingPlan) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(
                                            0xFFF5F5F5
                                        )
                                    ),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "Categoria ${item.categoryId}")
                                            Text(text = "R$ %.2f".format(item.plannedAmount))
                                        }

                                        // Barra de progresso (atualmente só exemplo, valor fixo de 10)
                                        val gastoAtual = 10.0
                                        val progresso = (gastoAtual / item.plannedAmount).coerceIn(0.0, 1.0).toFloat()
                                        LinearProgressIndicator(
                                            progress = { progresso }, // lambda que retorna o progresso
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = Color(0xFF2196F3),
                                            trackColor = Color.LightGray,
                                            strokeCap = StrokeCap.Round
                                        )

                                        Text(
                                            text = "Gasto: R$ %.2f / R$ %.2f".format(
                                                gastoAtual, item.plannedAmount
                                            ),
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Nenhum planejamento encontrado para este mês.",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}