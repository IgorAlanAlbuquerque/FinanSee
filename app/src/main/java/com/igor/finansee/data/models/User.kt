package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val fotoPerfil: Int? = null,
    val statusPremium: Boolean = false,
    var registrationTimestamp: Date? = null
)