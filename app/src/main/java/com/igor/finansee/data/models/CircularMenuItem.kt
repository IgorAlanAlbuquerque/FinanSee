package com.igor.finansee.data.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CircularMenuItem(
    val icon: ImageVector,
    val color: Color,
    val label: String,
    val onClick: () -> Unit
)