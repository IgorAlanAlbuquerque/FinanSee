package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(
    tableName = "credit_card",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class CreditCard(
    @PrimaryKey
    val id: String = "",
    val userId: String,
    val bankName: String = "",
    val lastFourDigits: String = "",
    val creditLimit: Double = 0.0,
    val statementClosingDay: Int = 0,
    val dueDate: Int = 0,
    val isActive: Boolean = true,
    @ServerTimestamp
    val lastUpdated: Date? = null
)