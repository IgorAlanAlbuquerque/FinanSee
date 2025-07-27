package com.igor.finansee.data.models

import java.time.LocalDate
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.time.ZoneId
import java.util.Date

@Entity(
    tableName = "expense",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"]), Index(value = ["userId"])]
)
data class Expense(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val descricao: String = "",
    val valor: Double = 0.0,
    val categoryId: String? = "",
    val data: Date = Date(),
    @ServerTimestamp
    val lastUpdated: Date? = null
) {
    @get:Ignore
    val monthYear: LocalDate
        get() = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1)
}
