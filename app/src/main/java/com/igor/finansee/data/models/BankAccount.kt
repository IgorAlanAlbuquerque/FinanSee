package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(
    tableName = "bank_accounts",
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
data class BankAccount(
    @PrimaryKey
    val id: String = "",
    val userId: String,
    val name: String = "",
    val type: String = "",
    val currentBalance: Double = 0.0,
    val isActive: Boolean = true,
    @ServerTimestamp
    val lastUpdated: Date? = null
)