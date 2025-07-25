package com.igor.finansee.view.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igor.finansee.data.AppDatabase
import com.igor.finansee.data.AuthRepository
import com.igor.finansee.data.models.User
import com.igor.finansee.viewmodels.PlansScreenViewModel
import com.igor.finansee.viewmodels.PlansScreenViewModelFactory

@Composable
fun PlansScreen() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val authRepository = remember { AuthRepository(db.userDao()) }

    val currentUser by produceState<User?>(initialValue = null, authRepository) {
        value = authRepository.getCurrentLocalUser()
    }

    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val factory = remember(currentUser) {
            PlansScreenViewModelFactory(
                planningDao = db.monthPlanningDao(),
                transactionDao = db.transactionDao(),
                categoryDao = db.categoryDao(),
                user = currentUser!!
            )
        }

        val viewModel: PlansScreenViewModel = viewModel(factory = factory)
        val uiState by viewModel.uiState.collectAsState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
                                                progress = item.progress,
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
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
}
