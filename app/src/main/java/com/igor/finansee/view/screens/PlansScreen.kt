package com.igor.finansee.view.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.igor.finansee.data.AppDatabase
import com.igor.finansee.viewmodels.AuthViewModel
import com.igor.finansee.viewmodels.PlansScreenViewModel
import com.igor.finansee.viewmodels.PlansScreenViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> /* ... */ }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            if (permissionStatus == PackageManager.PERMISSION_DENIED) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
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
            val db = AppDatabase.getDatabase(context)
            PlansScreenViewModelFactory(
                transactionDao = db.transactionDao(),
                expenseDao = db.expenseDao(),
                categoryDao = db.categoryDao(),
                planningDao = db.monthPlanningDao(),
                user = currentUser,
                context = context.applicationContext
            )
        }

        val viewModel: PlansScreenViewModel = viewModel(factory = factory)
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val monthAsString = uiState.selectedDate.toString()
                        navController.navigate("createPlan/$monthAsString")
                    }
                ) {
                    if (uiState.currentPlanning == null) {
                        Icon(Icons.Filled.Add, contentDescription = "Criar Plano")
                    } else {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar Plano")
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                            if (uiState.isLoading) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            } else if (uiState.currentPlanning != null) {
                                Text(
                                    text = "Planeado para o mês: R$ %.2f".format(uiState.totalPlannedAmount),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                val totalSpent = uiState.totalActualAmount
                                val totalPlanned = uiState.totalPlannedAmount
                                val spentColor = if (totalPlanned > 0 && totalSpent > totalPlanned) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    Color.Black
                                }

                                Text(
                                    text = "Total gasto no mês: R$ %.2f".format(totalSpent),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = spentColor,
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
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 8.dp),
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
}
