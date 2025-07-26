package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDate
import java.util.Date


@Entity(
    tableName = "month_planning",
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
data class MonthPlanning(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val monthYear: LocalDate = LocalDate.now(),
    val totalMonthlyIncome: Double = 0.0,
    val targetSpendingPercentage: Double = 0.0,
    val categorySpendingPlan: List<PlannedCategorySpending> = emptyList(),
    @ServerTimestamp
    val lastUpdated: Date? = null
)