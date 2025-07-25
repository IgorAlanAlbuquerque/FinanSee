package com.igor.finansee.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.igor.finansee.data.AppDatabase
import com.igor.finansee.data.models.User
import com.igor.finansee.viewmodels.AccountViewModel
import com.igor.finansee.viewmodels.AccountViewModelFactory

@Composable
fun AddAccountScreen(
    navController: NavHostController,
    currentUser: User,
    initialTab: String
) {
    val initialIndex = if (initialTab == "cartao") 1 else 0
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val factory = AccountViewModelFactory(db.bankAccountDao(), db.creditCardDao(), currentUser)
    val viewModel: AccountViewModel = viewModel(factory = factory)

    var selectedTabIndex by remember { mutableStateOf(initialIndex) }
    val tabs = listOf("Conta", "Cartão de Crédito")

    var accountName by remember { mutableStateOf("") }
    var accountType by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf("") }

    var cardBankName by remember { mutableStateOf("") }
    var cardLastFour by remember { mutableStateOf("") }
    var cardLimit by remember { mutableStateOf("") }
    var cardClosingDay by remember { mutableStateOf("") }
    var cardDueDay by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            when (selectedTabIndex) {
                0 -> BankAccountForm(
                    name = accountName,
                    onNameChange = { accountName = it },
                    type = accountType,
                    onTypeChange = { accountType = it },
                    balance = initialBalance,
                    onBalanceChange = { initialBalance = it }
                )
                1 -> CreditCardForm(
                    bankName = cardBankName,
                    onBankNameChange = { cardBankName = it },
                    lastFour = cardLastFour,
                    onLastFourChange = { cardLastFour = it },
                    limit = cardLimit,
                    onLimitChange = { cardLimit = it },
                    closingDay = cardClosingDay,
                    onClosingDayChange = { cardClosingDay = it },
                    dueDay = cardDueDay,
                    onDueDayChange = { cardDueDay = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selectedTabIndex == 0) {
                        viewModel.addBankAccount(
                            name = accountName,
                            type = accountType,
                            initialBalance = initialBalance.toDoubleOrNull() ?: 0.0,
                            userId = currentUser.id
                        )
                    } else {
                        viewModel.addCreditCard(
                            bankName = cardBankName,
                            lastFour = cardLastFour,
                            limit = cardLimit.toDoubleOrNull() ?: 0.0,
                            closingDay = cardClosingDay.toIntOrNull() ?: 1,
                            dueDay = cardDueDay.toIntOrNull() ?: 10,
                            userId = currentUser.id
                        )
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar")
            }
        }
    }
}


@Composable
private fun BankAccountForm(
    name: String, onNameChange: (String) -> Unit,
    type: String, onTypeChange: (String) -> Unit,
    balance: String, onBalanceChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Adicionar Nova Conta", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nome da Conta (ex: Nubank, Carteira)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = type, onValueChange = onTypeChange, label = { Text("Tipo (ex: Conta Corrente)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = balance, onValueChange = onBalanceChange, label = { Text("Saldo Inicial (R$)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun CreditCardForm(
    bankName: String, onBankNameChange: (String) -> Unit,
    lastFour: String, onLastFourChange: (String) -> Unit,
    limit: String, onLimitChange: (String) -> Unit,
    closingDay: String, onClosingDayChange: (String) -> Unit,
    dueDay: String, onDueDayChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Adicionar Novo Cartão", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = bankName, onValueChange = onBankNameChange, label = { Text("Nome do Banco (ex: Itaú)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = lastFour, onValueChange = onLastFourChange, label = { Text("Últimos 4 dígitos") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = limit, onValueChange = onLimitChange, label = { Text("Limite do Cartão (R$)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = closingDay, onValueChange = onClosingDayChange, label = { Text("Dia do fechamento da fatura") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dueDay, onValueChange = onDueDayChange, label = { Text("Dia do vencimento da fatura") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
    }
}