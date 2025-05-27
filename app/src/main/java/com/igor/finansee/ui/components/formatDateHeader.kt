package com.igor.finansee.ui.components

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDateHeader(date: LocalDate): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    return when (date) {
        today -> "Hoje"
        yesterday -> "Ontem"
        else -> date.format(DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", Locale("pt", "BR")))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() }
    }
}