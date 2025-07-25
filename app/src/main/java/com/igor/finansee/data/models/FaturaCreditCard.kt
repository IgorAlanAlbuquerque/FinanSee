package com.igor.finansee.data.models

import java.time.LocalDate
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "fatura_credit_card")
data class FaturaCreditCard (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val creditCardId: Int,
    val month: LocalDate,
    val valor: Double,
    @ServerTimestamp
    val lastUpdated: Date? = null
)