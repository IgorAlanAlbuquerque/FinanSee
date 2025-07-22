package com.igor.finansee.data.models

import java.time.LocalDate
import java.util.UUID
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense")
data class Expense(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val descricao: String,
    val valor: Double,
    val categoria: Category,
    val data: LocalDate
) {
    val monthYear: LocalDate
        get() = data.withDayOfMonth(1)
}
