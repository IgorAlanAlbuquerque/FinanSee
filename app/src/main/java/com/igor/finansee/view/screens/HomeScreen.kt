package com.igor.finansee.view.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.igor.finansee.data.models.BankAccount
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.CreditCard
import com.igor.finansee.data.models.FaturaCreditCard
import com.igor.finansee.data.models.MonthPlanning
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igor.finansee.data.AppDatabase
import com.igor.finansee.data.models.User
import com.igor.finansee.view.theme.*
import com.igor.finansee.viewmodels.AuthViewModel
import com.igor.finansee.viewmodels.HomeScreenViewModel
import com.igor.finansee.viewmodels.HomeScreenViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class CategoryWithAmount(
    val category: Category,
    val totalAmount: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.currentUser.collectAsState()

    val context = LocalContext.current

    val factory = remember(user) {
        val db = AppDatabase.getDatabase(context)
        HomeScreenViewModelFactory(
            transactionDao = db.transactionDao(),
            bankAccountDao = db.bankAccountDao(),
            categoryDao = db.categoryDao(),
            creditCardDao = db.creditCardDao(),
            faturaDao = db.faturaCreditCardDao(),
            planningDao = db.monthPlanningDao(),
            user = user
        )
    }

    val viewModel: HomeScreenViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Olá, ${uiState.userName}!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.selectPreviousMonth() }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Mês anterior")
                }
                Text(
                    text = uiState.selectedDate.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")) + " " + uiState.selectedDate.year,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { viewModel.selectNextMonth() }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Próximo mês")
                }
            }
        }
        item {
            BalanceSection(
                balance = "R$%.2f".format(uiState.totalAccountBalance),
                showBalance = uiState.showBalance,
                onToggleVisibility = { viewModel.toggleBalanceVisibility() }
            )
        }
        item {
            IncomeExpenseSection(
                totalIncome = "R$%.2f".format(uiState.incomeForSelectedMonth),
                totalExpenses = "R$%.2f".format(uiState.expensesForSelectedMonth)
            )
        }
        item {
            AccountsSection(uiState.userBankAccounts, navController)
        }
        item {
            CreditCardSection(
                userCreditCards = uiState.userCreditCards,
                currentMonthFaturas = uiState.currentMonthFaturas,
                totalCreditCardInvoiceAmount = uiState.totalCreditCardInvoiceAmount,
                navController
            )
        }
        item {
            ExpensesByCategory(uiState.expensesByCategory)
        }
        item {
            uiState.currentMonthPlanning?.let {
                MonthPlan(
                    selectedDate = uiState.selectedDate,
                    currentUser = uiState.user ?: return@let,
                    actualTotalExpenses = uiState.expensesForSelectedMonth,
                    actualExpensesByCategory = uiState.expensesByCategory,
                    allCategories = uiState.allCategories,
                    planningList = listOf(it)
                )
            }
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
            iconTint = Color.LightGray,
            amountColor = Color.Green
        )

        Spacer(modifier = Modifier.width(16.dp))

        IncomeExpenseCard(
            modifier = Modifier.weight(1f),
            type = "Despesas",
            amount = totalExpenses,
            icon = Icons.AutoMirrored.Outlined.TrendingDown,
            iconTint = Color.Red,
            amountColor = Color.Red
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
fun AccountsSection(userBankAccounts: List<BankAccount>, navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Contas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ListAlt,
                contentDescription = "Ver todas as contas",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (userBankAccounts.isEmpty()) {
                    Text(
                        text = "Nenhuma conta cadastrada ainda.",
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("add_account_screen?initialTab=banco") },
                        colors = ButtonDefaults.buttonColors(containerColor = LightPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ADICIONAR CONTA", color = Color.White)
                    }
                } else {
                    userBankAccounts.forEach { account ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val accountIcon = when (account.name.lowercase()) {
                                    "carteira" -> Icons.Outlined.AccountBalanceWallet
                                    "nubank" -> Icons.Outlined.CreditCard
                                    else -> Icons.Outlined.Home
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = accountIcon,
                                        contentDescription = account.name,
                                        tint = LightPurple,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = account.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "R$%.2f".format(account.currentBalance),
                                        fontSize = 14.sp,
                                        color = if (account.currentBalance >= 0) GreenSuccess else RedExpense,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                    )

                    val totalBalanceOfAllAccounts = userBankAccounts.sumOf { it.currentBalance }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "R$%.2f".format(totalBalanceOfAllAccounts),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (totalBalanceOfAllAccounts >= 0) GreenSuccess else RedExpense
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCardSection(
    userCreditCards: List<CreditCard>,
    currentMonthFaturas: List<FaturaCreditCard>,
    totalCreditCardInvoiceAmount: Double,
    navController: NavHostController
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cartões de Crédito",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Outlined.CreditCard,
                contentDescription = "Ícone de cartões",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (userCreditCards.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CreditCard,
                        contentDescription = "Nenhum cartão cadastrado",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ops! Você ainda não tem cartões cadastrados.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("add_account_screen?initialTab=cartao") },
                        colors = ButtonDefaults.buttonColors(containerColor = LightPurple /* Using your LightPurple */),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ADICIONAR CARTÃO", color = Color.White)
                    }
                }
            }
        } else {

            userCreditCards.forEachIndexed { index, creditCard ->

                val currentFaturaForThisCard = currentMonthFaturas.find { it.creditCardId == creditCard.id }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()

                        .padding(bottom = if (index < userCreditCards.size -1) 16.dp else 0.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.LightGray.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CreditCard,
                                        contentDescription = "Logo do Cartão ${creditCard.bankName}",
                                        tint = LightPurple,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Cartão ${creditCard.bankName}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { /* TODO: Ação para Faturas abertas DESTE CARTÃO (creditCard.id) */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightPurple.copy(alpha = 0.2f),
                                    contentColor = LightPurple
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Faturas abertas", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { /* TODO: Ação para Faturas fechadas DESTE CARTÃO (creditCard.id) */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.LightGray.copy(alpha = 0.3f),
                                    contentColor = Color.DarkGray
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Faturas fechadas", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        if (currentFaturaForThisCard != null) {
                            Text(
                                text = "Fatura Atual:",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "R$%.2f".format(currentFaturaForThisCard.valor),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightPurple
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val closingDateFatura = LocalDate.of(
                                currentFaturaForThisCard.month.year,
                                currentFaturaForThisCard.month.month,
                                creditCard.statementClosingDay
                            )
                            Text(
                                text = "Fecha em ${closingDateFatura.format(DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", Locale("pt", "BR")))}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CreditCard,
                                    contentDescription = "Nenhuma fatura",
                                    modifier = Modifier.size(32.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Nenhuma fatura encontrada para este cartão neste mês.",
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                if (index < userCreditCards.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Faturas do Mês",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = "R$%.2f".format(totalCreditCardInvoiceAmount),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesByCategory(transactionsByCategory: List<CategoryWithAmount>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Despesas por categoria",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (transactionsByCategory.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ListAlt,
                        contentDescription = "Nenhuma despesa cadastrada",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Opa! Você não tem despesas cadastradas nesse mês.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Adicione seus gastos no mês atual para ver seus gráficos.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            val totalExpenses = transactionsByCategory.sumOf { it.totalAmount }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                var startAngle = 0f
                                transactionsByCategory.forEachIndexed { index, data ->
                                    val sweepAngle = (data.totalAmount.toFloat() / totalExpenses.toFloat()) * 360f
                                    val color = pieChartColors.getOrElse(index % pieChartColors.size) { pieChartColors.first() }

                                    drawArc(
                                        color = color,
                                        startAngle = startAngle,
                                        sweepAngle = sweepAngle,
                                        useCenter = false,
                                        topLeft = Offset.Zero,
                                        size = Size(size.width, size.height),
                                        style = Stroke(width = 20.dp.toPx())
                                    )
                                    startAngle += sweepAngle
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            transactionsByCategory.forEachIndexed { index, data ->
                                val categoryName = data.category.name
                                val color = pieChartColors.getOrElse(index % pieChartColors.size) { pieChartColors.first() }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = categoryName,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    transactionsByCategory.forEachIndexed { index, data ->
                        val categoryName = data.category.name
                        val totalAmount = data.totalAmount
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(pieChartColors.getOrElse(index % pieChartColors.size) { pieChartColors.first() })
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = categoryName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "R$%.2f".format(totalAmount),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total de Despesas", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "R$%.2f".format(totalExpenses), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MonthPlan(
    selectedDate: LocalDate,
    currentUser: User,
    actualTotalExpenses: Double,
    actualExpensesByCategory: List<CategoryWithAmount>,
    allCategories: List<Category>,
    planningList: List<MonthPlanning>
) {
    val currentPlanning = remember(planningList, selectedDate, currentUser.id) {
        planningList.find {
            it.userId == currentUser.id &&
                    it.monthYear.year == selectedDate.year &&
                    it.monthYear.month == selectedDate.month
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Planejamento mensal",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextColorPrimary,
            modifier = Modifier.padding(start = 4.dp, bottom = 16.dp, top = 8.dp)
        )

        if (currentPlanning == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundColor)
            ) {
                Text(
                    "Nenhum planejamento encontrado para este mês.",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    color = TextColorSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                val totalPlannedTargetAmount = currentPlanning.totalMonthlyIncome * (currentPlanning.targetSpendingPercentage / 100.0)
                item {
                    PlanningCard(
                        icon = Icons.Filled.AccountBalanceWallet,
                        title = "Planejamento total",
                        actualSpent = actualTotalExpenses,
                        plannedAmount = totalPlannedTargetAmount,
                        iconBackgroundColor = IconBackgroundBlue
                    )
                }

                items(currentPlanning.categorySpendingPlan) { plannedCategory ->
                    val category = allCategories.find { it.id == plannedCategory.categoryId }
                    val actualSpentForCategory = actualExpensesByCategory
                        .find { it.category.id == plannedCategory.categoryId }?.totalAmount ?: 0.0

                    if (category != null) {
                        PlanningCard(
                            icon = getIconForCategory(category.id),
                            title = category.name,
                            actualSpent = actualSpentForCategory,
                            plannedAmount = plannedCategory.plannedAmount,
                            iconBackgroundColor = getIconBgColorForCategory(category.id, actualSpentForCategory > plannedCategory.plannedAmount)
                        )
                    }
                }
            }
        }
    }
}

fun getIconForCategory(categoryId: String): ImageVector {
    return when (categoryId) {
        "1" -> Icons.Filled.Restaurant // Alimentação
        "2" -> Icons.Filled.Home // Moradia
        "3" -> Icons.Filled.DirectionsCar // Transporte
        "4" -> Icons.Filled.School // Educação
        "5" -> Icons.Filled.LocalHospital // Saúde
        "6" -> Icons.Filled.Celebration // Lazer
        "7" -> Icons.Filled.ShoppingCart // Compras
        "8" -> Icons.AutoMirrored.Filled.ReceiptLong // Contas de Consumo
        "9" -> Icons.Outlined.AttachMoney // Assinaturas (Ícone de dinheiro como no exemplo)
        "10" -> Icons.Filled.Flight // Viagem
        "11" -> Icons.Filled.AccountBalance // Impostos (Exemplo)
        "12" -> Icons.Filled.MoreHoriz // Outras Despesas
        else -> Icons.Filled.Category // Ícone padrão
    }
}

fun getIconBgColorForCategory(categoryId: String, isExceeded: Boolean): Color {
    if (isExceeded) return IconBackgroundRed
    return when (categoryId) {
        "6"-> IconBackgroundRed
        "14" -> IconBackgroundBlue
        else -> IconBackgroundBlue.copy(alpha = 0.7f)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningCard(
    icon: ImageVector,
    title: String,
    actualSpent: Double,
    plannedAmount: Double,
    iconBackgroundColor: Color
) {
    val percentage = if (plannedAmount > 0.005) (actualSpent / plannedAmount) * 100 else if (actualSpent > 0) 200.0 else 0.0
    val visualProgress = if (plannedAmount > 0.005) (actualSpent / plannedAmount).toFloat() else if (actualSpent > 0) 1f else 0f

    val difference = actualSpent - plannedAmount
    val statusText: String
    val statusTextColor: Color

    if (difference > 0.005) {
        statusText = "Excedeu R$${"%.2f".format(difference)}"
        statusTextColor = ProgressBarExceededColor
    } else {
        statusText = "Restam R$${"%.2f".format(-difference)}"
        statusTextColor = TextColorSecondary
    }

    val progressBarColor = if (difference > 0.005) ProgressBarExceededColor else ProgressBarNormalColor

    Card(
        modifier = Modifier
            .width(260.dp)
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = TextColorPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextColorPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        statusText,
                        fontSize = 12.sp,
                        color = statusTextColor
                    )
                }
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = "Detalhes",
                    tint = TextColorSecondary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { visualProgress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = progressBarColor,
                    trackColor = Color.DarkGray.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "${"%.0f".format(percentage)}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (difference > 0.005) ProgressBarExceededColor else TextColorPrimary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "R$${"%.2f".format(actualSpent)} de R$${"%.2f".format(plannedAmount)}",
                fontSize = 12.sp,
                color = TextColorSecondary
            )

            if (difference > 0.005 && plannedAmount > 0.005) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "${"%.2f".format(percentage)}% Despesas previstas excedentes",
                    fontSize = 11.sp,
                    color = ProgressBarExceededColor,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
    }
}