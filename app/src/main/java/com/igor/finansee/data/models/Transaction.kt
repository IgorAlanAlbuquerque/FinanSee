package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDate
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val categoryId: Int,
    val value: Double,
    val description: String,
    val date: LocalDate,
    val type: TransactionType,
    @ServerTimestamp
    val lastUpdated: Date? = null
)