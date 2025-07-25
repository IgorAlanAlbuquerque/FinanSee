package com.igor.finansee.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun DrawerContent(navController: NavHostController, onCloseDrawer: () -> Unit, onSendNotification: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(380.dp)
            .padding(end = 20.dp)
            .background(MaterialTheme.colorScheme.surface),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Menu",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Contas",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCloseDrawer()
                        navController.navigate("add_account_screen?initialTab=banco")
                    }
                    .padding(vertical = 8.dp)
            )
            Text(
                text = "Cartão de Crédito",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCloseDrawer()
                        navController.navigate("add_account_screen?initialTab=cartao")
                    }
                    .padding(vertical = 8.dp)
            )
            Text(
                text = "Objetivos",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {}
                    .padding(vertical = 8.dp)
            )
            Text(
                text = "Sair",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .clickable { }
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Versão 0.0.0.1",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}