package com.igor.finansee.models

data class CreditCard(
    val id: Int,
    val userId: Int,
    val bankName: String,
    val lastFourDigits: String,
    val creditLimit: Double,
    val statementClosingDay: Int,
    val dueDate: Int,
    val isActive: Boolean = true
)

val creditCardList = listOf(
    CreditCard(
        id = 1,
        userId = 1,
        bankName = "Banco X",
        lastFourDigits = "1234",
        creditLimit = 2500.00,
        statementClosingDay = 25,
        dueDate = 5,
        isActive = true
    ),
    CreditCard(
        id = 2,
        userId = 1,
        bankName = "FinanSee Card",
        lastFourDigits = "5678",
        creditLimit = 5000.00,
        statementClosingDay = 10,
        dueDate = 20,
        isActive = true
    )
)