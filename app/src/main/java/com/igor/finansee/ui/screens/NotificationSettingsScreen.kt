package com.igor.finansee.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import com.igor.finansee.ui.components.SettingsClickableRow
import com.igor.finansee.ui.components.SettingsSwitchRow
import com.igor.finansee.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val userPreferences by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alertas e notificações") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                SettingsClickableRow(
                    title = "Configuração de Email",
                    subtitle = "Gerenciar conteúdo recebido por e-mail do Mobills",
                    onClick = { /* TODO: Navegar para uma tela específica ou abrir um diálogo */ }
                )
            }
            item {
                SettingsClickableRow(
                    title = "Leitura de notificação",
                    subtitle = "Habilitar permissão para ler notificações de cartões. Ex: Nubank, Digio.",
                    onClick = { /* TODO: Chamar viewModel para pedir permissão de NotificationListenerService */ }
                )
            }
            item {
                SettingsClickableRow(
                    title = "Leitura padrão de Notificação",
                    subtitle = "Define se a Notificação por padrão será um débito ou crédito.",
                    onClick = { /* TODO: Abrir um diálogo de seleção (débito/crédito) */ }
                )
            }
            item {
                SettingsSwitchRow(
                    title = "Alerta Pendências",
                    subtitle = "Notificar vencimento de despesas e receitas pendentes",
                    checked = userPreferences.alertPendencies,
                    onCheckedChange = viewModel::setAlertPendencies
                )
            }
            item {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    Text("Pendências e alertas", style = MaterialTheme.typography.bodyLarge)
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
            }
            item {
                SettingsClickableRow(
                    title = "Lembrete diário",
                    subtitle = "Sem alerta", // TODO: Mostrar o horário salvo se houver um
                    onClick = { /* TODO: Abrir um TimePickerDialog para o usuário escolher a hora */ }
                )
            }
        }
    }
}