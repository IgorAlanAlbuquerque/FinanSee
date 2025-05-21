package com.igor.finansee.models

data class User(
    val id: String, // Um ID único para o usuário (ex: UUID ou ID de um sistema de autenticação)
    val email: String,
    val name: String?, // Nome do usuário, pode ser opcional
    val registrationDate: Long // Timestamp da data de cadastro em milissegundos
)