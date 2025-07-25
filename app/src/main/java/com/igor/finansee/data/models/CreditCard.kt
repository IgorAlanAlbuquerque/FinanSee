package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "credit_card")
data class CreditCard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val bankName: String,
    val lastFourDigits: String,
    val creditLimit: Double,
    val statementClosingDay: Int,
    val dueDate: Int,
    val isActive: Boolean = true,
    @ServerTimestamp
    val lastUpdated: Date? = null
)