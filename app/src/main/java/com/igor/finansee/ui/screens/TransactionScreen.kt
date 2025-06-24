package com.igor.finansee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.igor.finansee.data.models.User
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igor.finansee.data.models.Transaction
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.ui.components.formatDateHeader
import com.igor.finansee.ui.theme.AmountGreenTransactions
import com.igor.finansee.ui.theme.AmountRedTransactions
import com.igor.finansee.ui.theme.CardBackgroundTransactions
import com.igor.finansee.ui.components.getCategoryUIDetails
import com.igor.finansee.ui.theme.LightCardBackgroundColor
import com.igor.finansee.ui.theme.TextPrimaryLight
import com.igor.finansee.ui.theme.TextSecondaryLight
import com.igor.finansee.viewmodels.TransactionScreenViewModel

@Composable
fun TransactionScreen(
    navController: NavHostController,
    currentUser: User,
    modifier: Modifier = Modifier,
    viewModel: TransactionScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = currentUser) {
        viewModel.loadInitialData(currentUser)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightCardBackgroundColor)
            .padding(top = 16.dp)
    ) {
        MonthSelector(
            currentMonth = uiState.selectedMonth,
            onPreviousMonth = { viewModel.selectPreviousMonth() },
            onNextMonth = { viewModel.selectNextMonth() }
        )

        SummaryCards(
            currentBalance = uiState.currentOverallBalance,
            monthlyBalance = uiState.monthlyBalance
        )

        if (uiState.transactionsByDate.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Nenhuma transação para este mês.",
                    color = TextSecondaryLight,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.transactionsByDate.forEach { (date, transactionsInGroup) ->
                    item {
                        TransactionDateHeader(date = formatDateHeader(date))
                    }
                    items(transactionsInGroup, key = { it.id }) { transaction ->
                        TransactionItemRow(transaction = transaction)
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun MonthSelector(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousMonth, modifier = Modifier.size(32.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mês anterior", tint = TextSecondaryLight)
        }
        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() }} ${currentMonth.year}",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimaryLight
        )
        IconButton(onClick = onNextMonth, modifier = Modifier.size(32.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Próximo mês", tint = TextSecondaryLight)
        }
    }
}

@Composable
fun SummaryCards(currentBalance: Double, monthlyBalance: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Saldo atual",
            amount = currentBalance,
            icon = Icons.Filled.AccountBalanceWallet,
            modifier = Modifier.weight(1f),
            amountColor = if (currentBalance >= 0) TextPrimaryLight else AmountRedTransactions
        )
        SummaryCard(
            title = "Balanço mensal",
            amount = monthlyBalance,
            icon = Icons.AutoMirrored.Filled.ReceiptLong,
            modifier = Modifier.weight(1f),
            amountColor = if (monthlyBalance >= 0) AmountGreenTransactions else AmountRedTransactions
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    amountColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundTransactions)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = TextSecondaryLight,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 13.sp, color = TextSecondaryLight)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "R$%.2f".format(Locale.GERMANY, amount),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = amountColor
            )
        }
    }
}

@Composable
fun TransactionDateHeader(date: String) {
    Text(
        text = date,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondaryLight,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp)
    )
}


@Composable
fun TransactionItemRow(transaction: Transaction) {
    val categoryDetails = getCategoryUIDetails(transaction.type, transaction.categoryId)
    val amountColor = when (transaction.type) {
        TransactionType.INCOME, TransactionType.TRANSFER_IN -> AmountGreenTransactions
        TransactionType.EXPENSE, TransactionType.TRANSFER_OUT, TransactionType.CREDIT_CARD_EXPENSE -> AmountRedTransactions
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundTransactions)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp).height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(categoryDetails.iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryDetails.icon,
                    contentDescription = categoryDetails.name,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f).align(Alignment.CenterVertically)) {
                Text(categoryDetails.name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextPrimaryLight)
                Text(transaction.description, fontSize = 12.sp, color = TextSecondaryLight)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    text = "R$%.2f".format(Locale.GERMANY, transaction.value),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = amountColor,
                    textAlign = TextAlign.End
                )
                Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(18.dp)) {
                    when (transaction.type) {
                        TransactionType.CREDIT_CARD_EXPENSE -> {
                            Icon(imageVector = Icons.Filled.CreditCard, contentDescription = "Cartão de Crédito", tint = TextSecondaryLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Filled.Circle, contentDescription = "Status Cartão", tint = AmountRedTransactions.copy(alpha = 0.7f), modifier = Modifier.size(10.dp))
                        }
                        else -> {
                            Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Concluído", tint = AmountGreenTransactions, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}