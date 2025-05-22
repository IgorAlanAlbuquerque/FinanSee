package com.igor.finansee.models

import java.time.LocalDate

data class Transaction(
    val id: Int,
    val userId: Int,

    val value: Double,
    val description: String,
    val date: LocalDate,

    val type: TransactionType,

)

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER,
    CREDIT_CARD_EXPENSE
}

val transactionList = listOf(
    Transaction(
        id = 1,
        userId = 1,
        value = 3500.00,
        description = "Salário - Janeiro",
        date = LocalDate.of(2025, 1, 31),
        type = TransactionType.INCOME
    ),
    Transaction(
        id = 2,
        userId = 1,
        value = 1200.00,
        description = "Aluguel",
        date = LocalDate.of(2025, 1, 5),
        type = TransactionType.EXPENSE
    ),
    Transaction(
        id = 3,
        userId = 1,
        value = 250.75,
        description = "Supermercado - Cartão",
        date = LocalDate.of(2025, 1, 10),
        type = TransactionType.CREDIT_CARD_EXPENSE
    ),
    Transaction(
        id = 4,
        userId = 1,
        value = 800.00,
        description = "Freelance - Projeto X",
        date = LocalDate.of(2025, 1, 15),
        type = TransactionType.INCOME
    ),
    Transaction(
        id = 5,
        userId = 1,
        value = 99.90,
        description = "Conta de Internet",
        date = LocalDate.of(2025, 1, 20),
        type = TransactionType.EXPENSE
    ),
    Transaction(
        id = 6,
        userId = 1,
        value = 500.00,
        description = "Transferência para Poupança",
        date = LocalDate.of(2025, 2, 1),
        type = TransactionType.TRANSFER
    ),
    Transaction(
        id = 7,
        userId = 1,
        value = 85.50,
        description = "Jantar com amigos",
        date = LocalDate.of(2025, 2, 10),
        type = TransactionType.CREDIT_CARD_EXPENSE
    ),
    Transaction(
        id = 8,
        userId = 1,
        value = 60.00,
        description = "Recarga Bilhete Único",
        date = LocalDate.of(2025, 2, 3),
        type = TransactionType.EXPENSE
    ),
    Transaction(
        id = 9,
        userId = 1,
        value = 150.00,
        description = "Reembolso Despesas Viagem",
        date = LocalDate.of(2025, 2, 25),
        type = TransactionType.INCOME
    ),
    Transaction(
        id = 10,
        userId = 1,
        value = 320.00,
        description = "Compra Online - Roupa",
        date = LocalDate.of(2025, 2, 20),
        type = TransactionType.CREDIT_CARD_EXPENSE
    )
)