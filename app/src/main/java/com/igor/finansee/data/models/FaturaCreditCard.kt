package com.igor.finansee.data.models

import java.time.LocalDate
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fatura_credit_card")
data class FaturaCreditCard (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val creditCardId: Int,
    val month: LocalDate,
    val valor: Double
)