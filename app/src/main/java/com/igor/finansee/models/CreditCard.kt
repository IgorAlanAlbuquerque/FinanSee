package com.igor.finansee.models

data class CreditCard(
    val id: String, // ID único para o cartão
    val userId: String, // ID do usuário ao qual este cartão pertence
    val bankName: String,
    val lastFourDigits: String, // Últimos 4 dígitos para identificação (NUNCA o número completo!)
    val creditLimit: Double,
    val statementClosingDay: Int, // Dia do mês em que a fatura fecha (ex: 25)
    val dueDate: Int, // Dia do mês em que a fatura vence (ex: 5)
    val isActive: Boolean = true // Indica se o cartão está ativo
)