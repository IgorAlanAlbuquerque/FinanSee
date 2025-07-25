package com.igor.finansee.view.components

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.TransactionType
import com.igor.finansee.view.theme.*
data class CategoryUIDetails(
    val name: String,
    val icon: ImageVector,
    val iconBackgroundColor: Color
)

fun getCategoryUIDetails(
    transactionType: TransactionType,
    categoryId: Int,
    allCategories: List<Category>
): CategoryUIDetails {
    val categoryFromList = allCategories.find { it.id == categoryId }

    val displayName: String
    val icon: ImageVector
    val bgColor: Color

    when (transactionType) {
        TransactionType.INCOME -> {
            displayName = categoryFromList?.name ?: "Receita"
            bgColor = CategoryColorsMap[categoryId] ?: IconBgGreen
            icon = when (categoryId) {
                1 -> Icons.Filled.MonetizationOn
                2 -> Icons.Filled.Work
                3 -> Icons.Filled.Redeem
                4 -> Icons.Filled.CardGiftcard
                5 -> Icons.AutoMirrored.Filled.TrendingUp
                else -> Icons.Filled.AttachMoney
            }
        }
        TransactionType.EXPENSE -> {
            displayName = categoryFromList?.name ?: "Despesa"
            bgColor = CategoryColorsMap[categoryId] ?: IconBackgroundRed
            icon = when (categoryId) {
                6 -> Icons.Filled.Restaurant
                7 -> Icons.Filled.Home
                8 -> Icons.Filled.DirectionsCar
                9 -> Icons.Filled.School
                10 -> Icons.Filled.LocalHospital
                11 -> Icons.Filled.SportsEsports
                12 -> Icons.Filled.ShoppingCart
                13 -> Icons.AutoMirrored.Filled.ReceiptLong
                14 -> Icons.Filled.Subscriptions
                15 -> Icons.Filled.Flight
                16 -> Icons.Filled.AccountBalance
                17 -> Icons.Filled.ErrorOutline
                else -> Icons.Filled.Payment
            }
        }
        TransactionType.CREDIT_CARD_EXPENSE -> {
            displayName = categoryFromList?.name ?: "Cartão"
            bgColor = CategoryColorsMap[categoryId] ?: IconBackgroundRed
            icon = when (categoryId) {
                6 -> Icons.Filled.Restaurant
                11 -> Icons.Filled.SportsEsports
                12 -> Icons.Filled.ShoppingCart
                else -> Icons.Filled.CreditCard
            }
        }
        TransactionType.TRANSFER_IN -> {
            displayName = "Transferência Recebida"
            bgColor = IconBackgroundBlue  // mantém o que você já tinha
            icon = Icons.AutoMirrored.Filled.ArrowForward
        }
        TransactionType.TRANSFER_OUT -> {
            displayName = "Transferência Enviada"
            bgColor = IconBgPurple  // mantém o que você já tinha
            icon = Icons.AutoMirrored.Filled.ArrowBack
        }
    }

    return CategoryUIDetails(displayName, icon, bgColor)
}