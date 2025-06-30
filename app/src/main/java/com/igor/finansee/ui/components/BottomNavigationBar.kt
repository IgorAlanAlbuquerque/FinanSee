package com.igor.finansee.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomAppBar {
        val itemsParte1 = listOf(
            Triple("home", "Home", Icons.Default.Home),
            Triple("profile", "Profile", Icons.Default.Person)
        )
        val itemsParte2 = listOf(
            Triple("plans", "Plans", Icons.Default.BarChart),
            Triple("transactions", "Transactions", Icons.Default.SwapVert)
        )

        itemsParte1.forEach { (rota, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = false,
                onClick = { navController.navigate(rota) }
            )
        }

        NavigationBarItem(
            selected = false,
            onClick = { /* Não faz nada */ },
            icon = { },
            enabled = false
        )

        itemsParte2.forEach { (rota, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = false,
                onClick = { navController.navigate(rota) }
            )
        }
    }
}
