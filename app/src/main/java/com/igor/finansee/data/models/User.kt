package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDate
import java.util.Date

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val id: String,
    val email: String,
    val name: String,
    val registrationDate: LocalDate,
    val password: String,
    val fotoPerfil: Int? = null,
    val statusPremium: Boolean,
    @ServerTimestamp
    val lastUpdated: Date? = null
)