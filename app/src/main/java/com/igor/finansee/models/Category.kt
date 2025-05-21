package com.igor.finansee.models

data class Category(
    val id: String, // ID único para a categoria
    val userId: String?, // ID do usuário. Nulo para categorias padrão do app.
    val name: String,
    val type: String, // "Income" (Receita) ou "Expense" (Despesa)
    val iconResId: Int?, // Opcional: ID de um drawable para o ícone (ex: R.drawable.ic_food)
    val colorHex: String?, // Opcional: Código Hex da cor para a categoria (ex: "#FF0000")
    val isDefault: Boolean = false // Se é uma categoria pré-definida do app
)