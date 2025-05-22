package com.igor.finansee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.igor.finansee.models.User

val BackgroundGray = Color(0xFFF0F0F0)
val GreenSuccess = Color(0xFF4CAF50)
val RedExpense = Color(0xFFF44336)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (navController: NavHostController, currentUser: User) {
    var showBalance by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray), // Define a cor de fundo do LazyColumn
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp), // Espaçamento entre os "cards"
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp) // Padding ao redor do conteúdo
    ) {
        item {
            BalanceSection(
                balance = "R$0,00",
                showBalance = showBalance,
                onToggleVisibility = { showBalance = !showBalance }
            )
        }
        item {
            IncomeExpenseSection(
                totalIncome = "R$0,00",
                totalExpenses = "R$0,00"
            )
        }
        item {
            AccountsSection()
        }
        item {
            CreditCardSection()
        }
        item {
            ExpensesByCategory()
        }
        item {
            MonthPlan()
        }
    }
}

@Composable
fun BalanceSection(balance: String, showBalance: Boolean, onToggleVisibility: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Saldo em contas",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (showBalance) balance else "R$ ****,**",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (showBalance) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = "Alternar visibilidade do saldo",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun IncomeExpenseSection(totalIncome: String, totalExpenses: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IncomeExpenseCard(
            modifier = Modifier.weight(1f),
            type = "Receitas",
            amount = totalIncome,
            icon = Icons.AutoMirrored.Outlined.TrendingUp,
            iconTint = GreenSuccess,
            amountColor = GreenSuccess
        )

        Spacer(modifier = Modifier.width(16.dp))

        IncomeExpenseCard(
            type = "Despesas",
            amount = totalExpenses,
            icon = Icons.AutoMirrored.Outlined.TrendingDown,
            iconTint = RedExpense,
            amountColor = RedExpense
        )
    }
}

@Composable
fun IncomeExpenseCard(
    modifier: Modifier = Modifier,
    type: String,
    amount: String,
    icon: ImageVector,
    iconTint: Color,
    amountColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = type,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = type,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = amount,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = amountColor
                )
            }
        }
    }
}

@Composable
fun AccountsSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Contas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Text(
                text = "Nenhuma conta cadastrada ainda.",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun MonthPlan() {
    TODO("Not yet implemented")
}

@Composable
fun ExpensesByCategory() {
    TODO("Not yet implemented")
}

@Composable
fun CreditCardSection() {
    TODO("Not yet implemented")
}