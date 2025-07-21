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

val mockMonthPlanningList: List<MonthPlanning> = listOf(
    MonthPlanning(
        id = 1,
        userId = 1,
        monthYear = LocalDate.of(2025, 1, 1),
        totalMonthlyIncome = 5000.00,
        targetSpendingPercentage = 80.0,
        categorySpendingPlan = listOf(
            PlannedCategorySpending(categoryId = 6, plannedAmount = 1000.00),
            PlannedCategorySpending(categoryId = 7, plannedAmount = 1500.00),
            PlannedCategorySpending(categoryId = 8, plannedAmount = 400.00),
            PlannedCategorySpending(categoryId = 11, plannedAmount = 600.00),
            PlannedCategorySpending(categoryId = 13, plannedAmount = 300.00),
            PlannedCategorySpending(categoryId = 17, plannedAmount = 200.00)
        )
    ),

    MonthPlanning(
        id = 2,
        userId = 1,
        monthYear = LocalDate.of(2025, 2, 1),
        totalMonthlyIncome = 5200.00,
        targetSpendingPercentage = 75.0,
        categorySpendingPlan = listOf(
            PlannedCategorySpending(categoryId = 6, plannedAmount = 950.00),
            PlannedCategorySpending(categoryId = 7, plannedAmount = 1400.00),
            PlannedCategorySpending(categoryId = 9, plannedAmount = 500.00),
            PlannedCategorySpending(categoryId = 10, plannedAmount = 450.00),
            PlannedCategorySpending(categoryId = 14, plannedAmount = 100.00),
            PlannedCategorySpending(categoryId = 12, plannedAmount = 500.00)
        )
    ),

    MonthPlanning(
        id = 3,
        userId = 1,
        monthYear = LocalDate.of(2025, 3, 1),
        totalMonthlyIncome = 4800.00,
        targetSpendingPercentage = 85.0,
        categorySpendingPlan = listOf(
            PlannedCategorySpending(categoryId = 6, plannedAmount = 1000.00),
            PlannedCategorySpending(categoryId = 7, plannedAmount = 1300.00),
            PlannedCategorySpending(categoryId = 8, plannedAmount = 380.00),
            PlannedCategorySpending(categoryId = 15, plannedAmount = 700.00),
            PlannedCategorySpending(categoryId = 11, plannedAmount = 400.00),
            PlannedCategorySpending(categoryId = 16, plannedAmount = 300.00)
        )
    )
)