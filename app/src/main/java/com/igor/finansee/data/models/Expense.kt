package com.igor.finansee.data.models

import java.time.LocalDate
import java.util.UUID
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(
    tableName = "expense",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Expense(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val descricao: String,
    val valor: Double,
    val categoryId: Int?,
    val data: LocalDate,
    @ServerTimestamp
    val lastUpdated: Date? = null
) {
    val monthYear: LocalDate
        get() = data.withDayOfMonth(1)
}
