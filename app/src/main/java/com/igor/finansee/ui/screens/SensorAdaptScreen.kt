/*
package com.igor.finansee.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.igor.finansee.viewmodels.SensorAdaptViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue


@Composable
fun SensorAdaptScreen(viewModel: SensorAdaptViewModel = viewModel()) {
    val lux by viewModel.lux.collectAsState()
    val battery by viewModel.batteryPct.collectAsState()
    val hour by viewModel.hour.collectAsState()
    val networkType by viewModel.networkType.collectAsState()
    val isUFCQuixada by viewModel.isUFCQuixada.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkIfUFCQuixada()
    }

    val isDarkTheme = when {
        battery <= 20 -> true
        hour >= 18 || hour < 6 -> lux < 30f
        lux < 15f -> true
        else -> false
    }

    MaterialTheme(colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()) {
        // Sua UI aqui usando lux, battery, etc
        // Exemplo simples:
        Column(Modifier.padding(16.dp)) {
            Text("Luminosidade: $lux lux")
            Text("Bateria: $battery%")
            Text("Hora: $hour h")
            Text("Rede: $networkType")
            Text("Está na UFC Quixadá? $isUFCQuixada")
        }
    }
}
*/
