package com.igor.finansee.data.models

data class Category(
    val id: Int,
    val name: String
)

val categoryList = listOf(
    // Receitas
    Category(id = 1, name = "Salário"),
    Category(id = 2, name = "Freelance"),
    Category(id = 3, name = "Reembolso"),
    Category(id = 4, name = "Presente"),
    Category(id = 5, name = "Investimentos"),

    // Despesas Comuns
    Category(id = 6, name = "Alimentação"),
    Category(id = 7, name = "Moradia"),
    Category(id = 8, name = "Transporte"),
    Category(id = 9, name = "Educação"),
    Category(id = 10, name = "Saúde"),
    Category(id = 11, name = "Lazer"),
    Category(id = 12, name = "Compras"),
    Category(id = 13, name = "Contas de Consumo"),
    Category(id = 14, name = "Assinaturas"),
    Category(id = 15, name = "Viagem"),
    Category(id = 16, name = "Impostos"),
    Category(id = 17, name = "Outras Despesas")
)