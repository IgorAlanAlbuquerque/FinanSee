
package com.igor.finansee.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.igor.finansee.data.models.CircularMenuItem

@Composable
fun CircularActionMenu(navController: NavController) {
    var isMenuOpen by remember { mutableStateOf(false) }

    val menuItems = listOf(
        CircularMenuItem(Icons.Default.TrendingUp, Color(0xFF1ABC9C), "Receita") {
            println("Clicou em Nova Receita")
        },
        CircularMenuItem(Icons.Default.TrendingDown, Color(0xFFE74C3C), "Despesa") {
            navController.navigate("add_expense")

        },

        CircularMenuItem(Icons.Default.Assessment, Color(0xFF3498DB), "DonutChart") {
            navController.navigate("donutChart")
        }
    )

    val animationProgress by animateFloatAsState(
        targetValue = if (isMenuOpen) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "MenuAnimation"
    )

    val rotationAngle by animateFloatAsState(
        targetValue = if (isMenuOpen) 45f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "FabRotation"
    )

    Box(contentAlignment = Alignment.Center) {
        menuItems.forEachIndexed { index, item ->
            MenuItem(
                item = item,
                index = index,
                totalItems = menuItems.size,
                animationProgress = animationProgress
            )
        }

        FloatingActionButton(
            onClick = { isMenuOpen = !isMenuOpen },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Abrir ou fechar menu",
                modifier = Modifier.rotate(rotationAngle)
            )
        }
    }
}

