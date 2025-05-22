package com.igor.finansee.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountBalanceWallet
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.igor.finansee.models.BankAccount
import com.igor.finansee.models.Category
import com.igor.finansee.models.CreditCard
import com.igor.finansee.models.TransactionType
import com.igor.finansee.models.User
import com.igor.finansee.models.creditCardList
import com.igor.finansee.models.bankAccountList
import com.igor.finansee.models.categoryList
import com.igor.finansee.models.transactionList
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

val LightPurple = Color(0xFF9C27B0)
val BackgroundGray = Color(0xFFF0F0F0)
val GreenSuccess = Color(0xFF4CAF50)
val RedExpense = Color(0xFFF44336)

val pieChartColors = listOf(
    Color(0xFFE91E63), // Pink
    Color(0xFF673AB7), // Deep Purple
    Color(0xFF2196F3), // Blue
    Color(0xFF00BCD4), // Cyan
    Color(0xFF8BC34A), // Light Green
    Color(0xFFFFC107), // Amber
    Color(0xFFFF5722), // Deep Orange
    Color(0xFF9E9E9E), // Grey
    Color(0xFF795548), // Brown
    Color(0xFF607D8B)  // Blue Grey
)

data class CategoryWithAmount(
    val category: Category,
    val totalAmount: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (navController: NavHostController, currentUser: User) {
    var showBalance by remember { mutableStateOf(true) }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val currentUserId = currentUser.id
    val userTransactions = remember(currentUserId) { transactionList.filter { it.userId == currentUserId } }
    val userBankAccounts = remember(currentUserId) { bankAccountList.filter { it.userId == currentUserId } }
    val userCreditCards = remember(currentUserId) { creditCardList.filter { it.userId == currentUserId } }
    val totalAccountBalance = remember(userBankAccounts) { userBankAccounts.sumOf { it.currentBalance } }

    val incomeForSelectedMonth = remember(userTransactions, selectedDate) {
        userTransactions
            .filter {
                it.date.month == selectedDate.month && it.date.year == selectedDate.year && it.type == TransactionType.INCOME
            }
            .sumOf { it.value }
    }

    val expensesForSelectedMonth = remember(userTransactions, selectedDate) {
        userTransactions
            .filter {
                it.date.month == selectedDate.month && it.date.year == selectedDate.year &&
                        (it.type == TransactionType.EXPENSE || it.type == TransactionType.CREDIT_CARD_EXPENSE)
            }
            .sumOf { it.value }
    }

    val transactions = remember(userTransactions, selectedDate){
         userTransactions
            .filter {
                it.date.month == selectedDate.month && it.date.year == selectedDate.year &&
                        (it.type == TransactionType.EXPENSE || it.type == TransactionType.CREDIT_CARD_EXPENSE)
            }
    }

    val transAgrByCategory = remember(transactions) {
        transactions
            .groupBy { it.categoryId }
            .map { (categoryId, transactionsList) ->
                val category = categoryList.find { it.id == categoryId } ?: Category(categoryId, "Outros")
                CategoryWithAmount(category, transactionsList.sumOf { it.value })
            }
            .sortedByDescending { it.totalAmount }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray), // Define a cor de fundo do LazyColumn
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp), // Espaçamento entre os "cards"
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp) // Padding ao redor do conteúdo
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Olá, ${currentUser.name.split(" ")[0]}!",
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
                IconButton(onClick = { selectedDate = selectedDate.minusMonths(1) }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Mês anterior")
                }
                Text(
                    text = selectedDate.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")) + " " + selectedDate.year,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { selectedDate = selectedDate.plusMonths(1) }) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Próximo mês")
                }
            }
        }
        item {
            BalanceSection(
                balance = "R$%.2f".format(totalAccountBalance),
                showBalance = showBalance,
                onToggleVisibility = { showBalance = !showBalance }
            )
        }
        item {
            IncomeExpenseSection(
                totalIncome = "R$%.2f".format(incomeForSelectedMonth),
                totalExpenses = "R$%.2f".format(expensesForSelectedMonth)
            )
        }
        item {
            AccountsSection(userBankAccounts)
        }
        item {
            CreditCardSection(userCreditCards)
        }
        item {
            ExpensesByCategory(transAgrByCategory)
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
            modifier = Modifier.weight(1f),
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
fun AccountsSection(userBankAccounts: List<BankAccount>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row( // Título e ícone no cabeçalho da seção
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
            // Ícone "quadradinhos" como na imagem, representando "ver todas"
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ListAlt, // Ou um ícone mais adequado, como um de "grid"
                contentDescription = "Ver todas as contas",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        // O GRANDE CARD que conterá todas as contas e o total
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp), // Use um arredondamento maior para o Card principal
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) { // Padding interno para o conteúdo do Card
                if (userBankAccounts.isEmpty()) {
                    // Mensagem se não houver contas
                    Text(
                        text = "Nenhuma conta cadastrada ainda.",
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { /* TODO: Navegar para tela de adicionar conta */ },
                        colors = ButtonDefaults.buttonColors(containerColor = LightPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ADICIONAR CONTA", color = Color.White)
                    }
                } else {
                    // Itera sobre as contas e exibe cada uma
                    userBankAccounts.forEach { account ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp), // Espaçamento entre cada linha de conta
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Ícone da conta (ex: R$ para carteira, logo do Nubank para Nubank)
                                // Você pode adicionar lógica para escolher o ícone baseado no nome/tipo do banco
                                val accountIcon = when (account.name.lowercase()) {
                                    "carteira" -> Icons.Outlined.AccountBalanceWallet
                                    "nubank" -> Icons.Outlined.CreditCard // Ou um ícone mais genérico se não tiver o logo
                                    else -> Icons.Outlined.Home // Ícone padrão
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray.copy(alpha = 0.2f)), // Fundo leve para o ícone
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = accountIcon,
                                        contentDescription = account.name,
                                        tint = LightPurple, // Cor do ícone
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
                            // Ícone de adição à direita, como na imagem do Nubank
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Adicionar",
                                tint = LightPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                    ) // Linha divisória

                    // Saldo Total de todas as contas
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

@Composable
fun CreditCardSection(userCreditCards: List<CreditCard>) {

}


@OptIn(ExperimentalMaterial3Api::class) // Se você usa APIs experimentais do Material3
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

        // Verifica se a lista de despesas por categoria está vazia
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
            // Se houver despesas, calcule o total para o gráfico e lista
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
                            .height(120.dp) // Altura fixa para o gráfico
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Gráfico de Pizza (Círculo Vazado)
                        Box(
                            modifier = Modifier
                                .size(100.dp) // Tamanho do círculo do gráfico
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
                                        useCenter = false, // Não preenche o centro
                                        topLeft = Offset.Zero,
                                        size = Size(size.width, size.height),
                                        style = Stroke(width = 20.dp.toPx()) // Espessura do anel
                                    )
                                    startAngle += sweepAngle
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Lista de Legendas das Categorias (Pontos e Nomes)
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

                    // Lista de Categorias com valores e botão de adição
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
                                // Ponto colorido antes do nome da categoria
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
                                // Botão de adição ao lado do valor
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Adicionar despesa a $categoryName",
                                    tint = LightPurple,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    // Divisor e Total Geral de Despesas
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
fun MonthPlan() {
}