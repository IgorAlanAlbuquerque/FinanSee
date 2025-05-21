package com.igor.finansee.models

data class BankAccount(
    val id: String, // ID único para a conta (UUID)
    val userId: String, // ID do usuário ao qual esta conta pertence
    val name: String,
    val type: String, // Ex: "Checking Account", "Savings Account", "Cash", "Investment"
    val initialBalance: Double, // Saldo inicial ao cadastrar a conta
    val currentBalance: Double, // Saldo atual da conta, atualizado por transações
    val isDefault: Boolean, // Se é a conta padrão para novas transações
    val isActive: Boolean = true // Indica se a conta está ativa ou arquivada
)