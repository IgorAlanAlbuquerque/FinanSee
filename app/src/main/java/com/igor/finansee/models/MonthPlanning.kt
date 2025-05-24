package com.igor.finansee.models

import java.time.LocalDate

data class MonthPlanning (
    val id: Int,
    val userId: Int,
    val date: LocalDate,
)