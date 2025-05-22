package com.igor.finansee.models

import java.time.LocalDate

data class User(
    val id: Int,
    val email: String,
    val name: String,
    val registrationDate: LocalDate,
    val password: String
)

val userList = listOf(
    User(
        id = 1,
        email = "igoralbuquerque102@hotmail.com",
        name = "Igor Alan Albuquerque de Sousa",
        registrationDate = LocalDate.of(2023, 1, 15),
        password = "123"
    )
)