package com.igor.finansee.data.states

data class CategoryPlanWithProgress(
    val categoryId: String,
    val categoryName: String,
    val plannedAmount: Double,
    val actualAmount: Double,
    val progress: Float
)