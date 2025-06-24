package com.igor.finansee.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar (
    onOpenDrawer: () -> Unit,
    isMenuExpanded: Boolean,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onNavigate: (String) -> Unit
){
    TopAppBar(
        title =  { Text("FinanSee") },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Default.Menu, contentDescription = "Menu lateral")
            }
        },
        actions = {
            Box {
                IconButton(onClick = onToggleMenu) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Mais opções")
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = onDismissMenu
                ) {
                    DropdownMenuItem(
                        text = { Text("Configurações") },
                        onClick = { onNavigate("settings") }
                    )
                    DropdownMenuItem(
                        text = { Text("Alertas e notificações") },
                        onClick = { onNavigate("notification_settings") }
                    )
                }
            }
        }
    )
}