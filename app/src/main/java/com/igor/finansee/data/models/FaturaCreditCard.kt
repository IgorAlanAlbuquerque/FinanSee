package com.igor.finansee.data.models

import java.time.LocalDate
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(
    tableName = "fatura_credit_card",
    foreignKeys = [
        ForeignKey(
            entity = CreditCard::class,
            parentColumns = ["id"],
            childColumns = ["creditCardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["creditCardId"])]
)
data class FaturaCreditCard(
    @PrimaryKey
    val id: String = "",
    val creditCardId: String = "",
    val month: LocalDate = LocalDate.now(),
    val valor: Double = 0.0,
    @ServerTimestamp
    val lastUpdated: Date? = null
)
