package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val categoryId: String = "",
    val value: Double = 0.0,
    val description: String = "",
    val date: Date? = null,
    val type: TransactionType = TransactionType.TRANSFER_OUT,
    @ServerTimestamp
    val lastUpdated: Date? = null
)