package com.igor.finansee.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar (navController: NavController) {
    NavigationBar {
        val items = listOf(
            Triple("home", "Home", Icons.Default.Home),
            Triple("profile", "Profile", Icons.Default.Person),
            Triple("add", "Add", Icons.Default.AddCircle),
            Triple("plans", "Plans", Icons.Default.BarChart),
            Triple("transactions", "Transactions", Icons.Default.SwapVert)
        )
        items.forEach{
                (rota, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = false,
                onClick = {navController.navigate(rota)}
            )
        }
    }
}