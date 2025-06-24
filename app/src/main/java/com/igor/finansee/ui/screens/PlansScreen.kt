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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.igor.finansee.ui.theme.BackgroundGray
import com.igor.finansee.viewmodels.PlansScreenViewModel

@Composable
fun PlansScreen(
    navController: NavHostController,
    viewModel: PlansScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
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
            Text(
                text = "Planejamento Financeiro",
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
                IconButton(onClick = { viewModel.selectPreviousMonth() }) {
                    Icon(Icons.Filled.ChevronLeft, "Mês anterior", tint = Color.Black)
                }

                Text(
                    text = uiState.monthDisplayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                IconButton(onClick = { viewModel.selectNextMonth() }) {
                    Icon(Icons.Filled.ChevronRight, "Próximo mês", tint = Color.Black)
                }
            }

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
                    if (uiState.currentPlanning != null) {
                        Text(
                            text = "Valor total a ser gasto no mês: R$ %.2f".format(uiState.totalPlannedAmount),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn {
                            items(uiState.planDetailsWithProgress) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = item.categoryName)
                                            Text(text = "R$ %.2f".format(item.plannedAmount))
                                        }

                                        LinearProgressIndicator(
                                            progress = { item.progress },
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
                                                item.actualAmount, item.plannedAmount
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
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
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
}