package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts")
data class BankAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val name: String,
    val type: String,
    val currentBalance: Double,
    val isActive: Boolean = true
)

val bankAccountList = listOf(
    BankAccount(
        id = 1,
        userId = 1,
        name = "Conta Corrente Principal",
        type = "Corrente",
        currentBalance = 1500.50,
        isActive = true
    ),
    BankAccount(
        id = 2,
        userId = 1,
        name = "Poupança Investimentos",
        type = "Poupança",
        currentBalance = 5000.00,
        isActive = true
    ),
    BankAccount(
        id = 3,
        userId = 1,
        name = "Conta Despesas Mensais",
        type = "Corrente",
        currentBalance = 320.80,
        isActive = true
    )
)