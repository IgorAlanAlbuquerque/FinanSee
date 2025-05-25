package com.igor.finansee.ui.screens

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
import androidx.compose.material.icons.filled.ReceiptLong
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
import com.igor.finansee.models.FaturaCreditCard
import com.igor.finansee.models.MonthPlanning
import com.igor.finansee.models.TransactionType
import com.igor.finansee.models.User
import com.igor.finansee.models.creditCardList
import com.igor.finansee.models.bankAccountList
import com.igor.finansee.models.categoryList
import com.igor.finansee.models.transactionList
import com.igor.finansee.models.faturaCreditCardList
import com.igor.finansee.models.mockMonthPlanningList
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
val CardBackgroundColor = Color(0xFF2C2C2E)
val TextColorPrimary = Color.White
val TextColorSecondary = Color(0xFFAAAAAA)
val ProgressBarExceededColor = Color(0xFFE74C3C)
val ProgressBarNormalColor = Color(0xFF4A90E2)
val IconBackgroundBlue = Color(0xFF4A90E2)
val IconBackgroundRed = Color(0xFFE74C3C)

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

    val expensesForSelectedMonth = remember(userTransactions, userCreditCards, faturaCreditCardList, selectedDate){
        val directExpenses = userTransactions
            .filter {
                it.date.month == selectedDate.month && it.date.year == selectedDate.year &&
                        it.type == TransactionType.EXPENSE // Apenas despesas diretas, sem considerar CREDIT_CARD_EXPENSE aqui
            }
            .sumOf { it.value }

        val creditCardInvoiceExpenses = faturaCreditCardList
            .filter { fatura ->
                // Filtra as faturas que pertencem aos cartões do usuário logado
                userCreditCards.any { it.id == fatura.creditCardId } &&
                        // E que são do mês e ano selecionados
                        fatura.month.month == selectedDate.month && fatura.month.year == selectedDate.year
            }
            .sumOf { it.valor }

        directExpenses + creditCardInvoiceExpenses
    }

    val transAgrByCategory = remember(userTransactions, selectedDate){
         val temp = userTransactions
            .filter {
                it.date.month == selectedDate.month && it.date.year == selectedDate.year &&
                        (it.type == TransactionType.EXPENSE || it.type == TransactionType.CREDIT_CARD_EXPENSE)
            }

        temp
            .groupBy { it.categoryId }
            .map { (categoryId, transactionsList) ->
                val category = categoryList.find { it.id == categoryId } ?: Category(categoryId, "Outros")
                CategoryWithAmount(category, transactionsList.sumOf { it.value })
            }
            .sortedByDescending { it.totalAmount }
    }

    // Lógica para obter a fatura atual para a CreditCardSection
    val currentMonthFaturasForUser = remember(userCreditCards, faturaCreditCardList, selectedDate) {
        // Filtra todas as faturas do usuário para o mês selecionado
        faturaCreditCardList.filter { fatura ->
            userCreditCards.any { it.id == fatura.creditCardId } &&
                    fatura.month.month == selectedDate.month &&
                    fatura.month.year == selectedDate.year
        }
    }

    // Calcula o total das faturas do mês selecionado para o rodapé do card de cartão
    val totalCreditCardInvoiceAmount = remember(currentMonthFaturasForUser) {
        currentMonthFaturasForUser.sumOf { it.valor }
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
            CreditCardSection(
                userCreditCards = userCreditCards,
                currentMonthFaturas = currentMonthFaturasForUser,
                totalCreditCardInvoiceAmount = totalCreditCardInvoiceAmount,
                selectedDate = selectedDate
            )
        }
        item {
            ExpensesByCategory(transAgrByCategory)
        }
        item {
            MonthPlan(
                selectedDate = selectedDate,
                currentUser = currentUser,
                actualTotalExpenses = expensesForSelectedMonth,
                actualExpensesByCategory = transAgrByCategory,
                allCategories = categoryList, // Passe a lista de todas as categorias
                planningList = mockMonthPlanningList // Passe a lista de planejamentos
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCardSection(
    userCreditCards: List<CreditCard>,
    currentMonthFaturas: List<FaturaCreditCard>, // Lista de faturas do mês selecionado para TODOS os cartões do usuário
    totalCreditCardInvoiceAmount: Double, // Total das faturas para o rodapé
    selectedDate: LocalDate // Passado para exibir a data de fechamento/vencimento
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Section Header (Cartões de Crédito, Icon)
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
                contentDescription = "Ícone de cartões", // More descriptive
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (userCreditCards.isEmpty()) {
            // "Nenhum cartão cadastrado" UI - Stays the same
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
                        onClick = { /* TODO: Navegar para tela de adicionar cartão */ },
                        colors = ButtonDefaults.buttonColors(containerColor = LightPurple /* Using your LightPurple */),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ADICIONAR CARTÃO", color = Color.White)
                    }
                }
            }
        } else {
            // Display each card
            userCreditCards.forEachIndexed { index, creditCard ->
                // Find the fatura for THIS specific creditCard for the selected month
                val currentFaturaForThisCard = currentMonthFaturas.find { it.creditCardId == creditCard.id }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Add bottom margin if it's not the last card, to space them from the total summary
                        .padding(bottom = if (index < userCreditCards.size -1) 16.dp else 0.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween // Pushes content to ends
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.LightGray.copy(alpha = 0.3f)), // Placeholder background
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CreditCard, // Replace with bank-specific logo if available
                                        contentDescription = "Logo do Cartão ${creditCard.bankName}",
                                        tint = LightPurple, // Or card-specific color
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Cartão ${creditCard.bankName}", // Or a nickname for the card
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                    // You could add last 4 digits of card here if available in CreditCard model
                                    // Text(text = "**** ${creditCard.lastFourDigits}", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            // Optional: Add an icon button for more options per card
                            // IconButton(onClick = { /* TODO: More options for this card */ }) {
                            //    Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                            // }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Buttons "Faturas abertas" e "Faturas fechadas" - per card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp) // Or Arrangement.Start
                        ) {
                            Button(
                                onClick = { /* TODO: Ação para Faturas abertas DESTE CARTÃO (creditCard.id) */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightPurple.copy(alpha = 0.2f),
                                    contentColor = LightPurple
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp) // Adjusted padding
                            ) {
                                Text("Faturas abertas", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { /* TODO: Ação para Faturas fechadas DESTE CARTÃO (creditCard.id) */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.LightGray.copy(alpha = 0.3f), // Slightly different gray
                                    contentColor = Color.DarkGray // Darker gray for better contrast
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp) // Adjusted padding
                            ) {
                                Text("Faturas fechadas", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        if (currentFaturaForThisCard != null) {
                            // Detalhes da fatura atual
                            Text(
                                text = "Fatura Atual:",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "R$%.2f".format(currentFaturaForThisCard.valor),
                                fontSize = 20.sp, // Slightly larger for emphasis
                                fontWeight = FontWeight.Bold,
                                color = LightPurple
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Data de fechamento da fatura
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
                            // You could also add due date here if available in your model, e.g.:
                            // val dueDateFatura = closingDateFatura.plusDays(creditCard.paymentDueDayOffset) // Example
                            // Text(text = "Vence em ${dueDateFatura.format(DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", Locale("pt", "BR")))}", fontSize = 14.sp, color = Color.DarkGray)

                        } else {
                            // No fatura for this card in the selected month
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally // Center text
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CreditCard, // Or a specific "no invoice" icon
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
                // Add a spacer between cards if listing multiple, but not after the last one before the total.
                if (index < userCreditCards.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // This "Total" footer is displayed ONCE, after all cards.
            // It summarizes the invoices for ALL cards for the selected month.
            Spacer(modifier = Modifier.height(16.dp)) // Space before the divider
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(12.dp)) // Space after divider, before total text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp), // Slight horizontal padding for the total row
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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                // TODO: Adicionar um botão para "Criar Planejamento" se desejar
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp) // Para não colar nas bordas
            ) {
                // Card: Planejamento Total
                val totalPlannedTargetAmount = currentPlanning.totalMonthlyIncome * (currentPlanning.targetSpendingPercentage / 100.0)
                item {
                    PlanningCard(
                        icon = Icons.Filled.AccountBalanceWallet,
                        title = "Planejamento total",
                        actualSpent = actualTotalExpenses, // Este é o total de despesas do mês
                        plannedAmount = totalPlannedTargetAmount,
                        iconBackgroundColor = IconBackgroundBlue,
                        onClick = { /* TODO: Navegar para detalhes do planejamento total */ }
                    )
                }

                // Cards: Categorias Planejadas
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
                            iconBackgroundColor = getIconBgColorForCategory(category.id, actualSpentForCategory > plannedCategory.plannedAmount),
                            onClick = { /* TODO: Navegar para detalhes desta categoria no planejamento */ }
                        )
                    }
                }
            }
        }
    }
}

fun getIconForCategory(categoryId: Int): ImageVector {
    return when (categoryId) {
        // IDs da sua categoryList
        6 -> Icons.Filled.Restaurant // Alimentação
        7 -> Icons.Filled.Home // Moradia
        8 -> Icons.Filled.DirectionsCar // Transporte
        9 -> Icons.Filled.School // Educação
        10 -> Icons.Filled.LocalHospital // Saúde
        11 -> Icons.Filled.Celebration // Lazer
        12 -> Icons.Filled.ShoppingCart // Compras
        13 -> Icons.AutoMirrored.Filled.ReceiptLong // Contas de Consumo
        14 -> Icons.Outlined.AttachMoney // Assinaturas (Ícone de dinheiro como no exemplo)
        15 -> Icons.Filled.Flight // Viagem
        16 -> Icons.Filled.AccountBalance // Impostos (Exemplo)
        17 -> Icons.Filled.MoreHoriz // Outras Despesas
        else -> Icons.Filled.Category // Ícone padrão
    }
}

fun getIconBgColorForCategory(categoryId: Int, isExceeded: Boolean): Color {
    if (isExceeded) return IconBackgroundRed // Se excedeu, pode ser vermelho
    return when (categoryId) {
        6 -> IconBackgroundRed // Alimentação (geralmente vermelho no exemplo)
        14 -> IconBackgroundBlue // Assinaturas (azul no exemplo)
        // Adicione mais cores específicas baseadas nos seus designs
        else -> IconBackgroundBlue.copy(alpha = 0.7f) // Um azul padrão para outros
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningCard(
    icon: ImageVector,
    title: String,
    actualSpent: Double,
    plannedAmount: Double,
    iconBackgroundColor: Color,
    onClick: () -> Unit
) {
    val percentage = if (plannedAmount > 0.005) (actualSpent / plannedAmount) * 100 else if (actualSpent > 0) 200.0 else 0.0
    // Usar 0.005 para evitar divisão por zero ou valores muito pequenos de plannedAmount
    // Coerce progress para o LinearProgressIndicator (0f a 1f)
    // Para o texto, o percentage pode ser > 100%
    val visualProgress = if (plannedAmount > 0.005) (actualSpent / plannedAmount).toFloat() else if (actualSpent > 0) 1f else 0f

    val difference = actualSpent - plannedAmount
    val statusText: String
    val statusTextColor: Color

    if (difference > 0.005) { // Excedeu (considerando uma pequena margem para igualdade de float)
        statusText = "Excedeu R$${"%.2f".format(difference)}"
        statusTextColor = ProgressBarExceededColor
    } else { // Resta ou está em dia
        statusText = "Restam R$${"%.2f".format(-difference)}" // -difference será o valor restante
        statusTextColor = TextColorSecondary // Cinza claro para "Restam"
    }

    val progressBarColor = if (difference > 0.005) ProgressBarExceededColor else ProgressBarNormalColor

    Card(
        modifier = Modifier
            .width(260.dp) // Largura do card
            .height(IntrinsicSize.Min), // Ajusta a altura ao conteúdo
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp) // Tamanho do círculo do ícone
                        .clip(CircleShape)
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = TextColorPrimary,
                        modifier = Modifier.size(20.dp) // Tamanho do ícone
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
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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

            Spacer(modifier = Modifier.height(18.dp)) // Aumentar espaço antes da barra

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { visualProgress.coerceIn(0f, 1f) }, // Progresso visual limitado a 100% na barra
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp) // Barra mais grossa
                        .clip(RoundedCornerShape(5.dp)),
                    color = progressBarColor,
                    trackColor = Color.DarkGray.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "${"%.0f".format(percentage)}%", // Porcentagem sem casas decimais como nos exemplos
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
                    // Texto como no exemplo: "366,30% Despesas previstas excedentes"
                    // Usando a porcentagem calculada e formatada:
                    "${"%.2f".format(percentage)}% Despesas previstas excedentes",
                    fontSize = 11.sp,
                    color = ProgressBarExceededColor,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
    }
}