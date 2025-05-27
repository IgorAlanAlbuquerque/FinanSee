package com.igor.finansee.models

import com.igor.finansee.R
import java.time.LocalDate

data class User(
    val id: Int,
    val email: String,
    val name: String,
    val registrationDate: LocalDate,
    val password: String,
    val fotoPerfil: Int? = null,
    val statusPremium: Boolean
)

val userList = listOf(
    User(
        id = 1,
        email = "igoralbuquerque102@hotmail.com",
        name = "Igor Alan Albuquerque de Sousa",
        registrationDate = LocalDate.of(2023, 1, 15),
        password = "123",
        statusPremium = false,
        fotoPerfil = R.drawable.perfil
    )
)