package com.igor.finansee.data.models

import java.time.LocalDate

data class FaturaCreditCard (
    val id: Int,
    val creditCardId: Int,
    val month: LocalDate,
    val valor: Double
)

val faturaCreditCardList = listOf(
    FaturaCreditCard(
        id = 1,
        creditCardId = 1,
        month = LocalDate.of(2025, 1, 1),
        valor = 850.50
    ),
    FaturaCreditCard(
        id = 2,
        creditCardId = 1,
        month = LocalDate.of(2025, 2, 1),
        valor = 1200.00
    ),
    FaturaCreditCard(
        id = 3,
        creditCardId = 1,
        month = LocalDate.of(2025, 3, 1),
        valor = 1000.00
    ),
    FaturaCreditCard(
        id = 4,
        creditCardId = 1,
        month = LocalDate.of(2025, 4, 1),
        valor = 250.00
    ),
    FaturaCreditCard(
        id = 5,
        creditCardId = 2,
        month = LocalDate.of(2025, 2, 1),
        valor = 300.00
    ),
    FaturaCreditCard(
        id = 6,
        creditCardId = 2,
        month = LocalDate.of(2025, 3, 1),
        valor = 50.00
    )
)