package com.igor.finansee.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

data class PlannedCategorySpending(
    val categoryId: Int,
    val plannedAmount: Double
)

@Entity(tableName = "month_planning")
data class MonthPlanning (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val monthYear: LocalDate,
    val totalMonthlyIncome: Double,
    val targetSpendingPercentage: Double,
    val categorySpendingPlan: List<PlannedCategorySpending>
)