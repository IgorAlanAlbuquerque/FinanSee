package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.igor.finansee.R
import java.time.LocalDate

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
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