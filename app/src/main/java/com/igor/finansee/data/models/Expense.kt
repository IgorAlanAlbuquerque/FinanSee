package com.igor.finansee.data.models

import java.time.LocalDate
import java.util.UUID

data class Expense(
    val id: UUID = UUID.randomUUID(),
    val descricao: String,
    val valor: Double,
    val categoria: Category,
    val data: LocalDate
) {
    val monthYear: LocalDate
        get() = data.withDayOfMonth(1)
}

// Obtendo categorias para exemplo
val moradia = categoryList.find { it.name == "Moradia" }!!
val alimentacao = categoryList.find { it.name == "Alimentação" }!!
val transporte = categoryList.find { it.name == "Transporte" }!!
val lazer = categoryList.find { it.name == "Lazer" }!!
val saude = categoryList.find { it.name == "Saúde" }!!

// Usando mutableListOf em vez de listOf para permitir alterações
val expenseList = mutableListOf(
    Expense(descricao = "Aluguel Junho", valor = 450.0, categoria = moradia, data = LocalDate.of(2025, 6, 5)),
    Expense(descricao = "Compras da semana", valor = 120.50, categoria = alimentacao, data = LocalDate.of(2025, 6, 10)),
    Expense(descricao = "Gasolina", valor = 80.0, categoria = transporte, data = LocalDate.of(2025, 6, 12)),
    Expense(descricao = "Cinema", valor = 45.0, categoria = lazer, data = LocalDate.of(2025, 6, 15)),
    Expense(descricao = "Restaurante com amigos", valor = 75.80, categoria = alimentacao, data = LocalDate.of(2025, 6, 18)),
    Expense(descricao = "Farmácia", valor = 52.0, categoria = saude, data = LocalDate.of(2025, 6, 20)),
    Expense(descricao = "App de transporte", valor = 22.0, categoria = transporte, data = LocalDate.of(2025, 6, 22))
)
