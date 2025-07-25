package com.igor.finansee.view.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.igor.finansee.view.components.SettingsClickableRow
import com.igor.finansee.view.components.SettingsSwitchRow
import com.igor.finansee.utils.PermissionUtils
import com.igor.finansee.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEmailSettings: () -> Unit,
    onNavigateDailyReminder: () -> Unit,
    viewModel: SettingsViewModel
) {
    val userPreferences by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    var showPermissionDialog by remember { mutableStateOf(false) }

    val isPermissionGranted = PermissionUtils.isNotificationServiceEnabled(context)

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Leitura de Notificações") },
            text = { Text("Para capturar seus gastos automaticamente, o FinanSee precisa de permissão para ler as notificações de aplicativos como Nubank e outros bancos.\n\nSeus dados são processados localmente e nunca compartilhados. Deseja conceder a permissão?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    }
                ) {
                    Text("Conceder Permissão")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

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
                    onClick = onNavigateToEmailSettings
                )
            }
            item {
                SettingsClickableRow(
                    title = "Leitura de notificação",
                    subtitle = if (isPermissionGranted) "Ativado" else "Desativado",
                    onClick = {
                        if (!isPermissionGranted) {
                            showPermissionDialog = true
                        } else {
                            context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                        }
                    }
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
                val userPreferences by viewModel.uiState.collectAsState()
                val subtitle = if (userPreferences.dailyReminderHour != -1) {
                    "Lembrete às ${String.format("%02d:%02d", userPreferences.dailyReminderHour, userPreferences.dailyReminderMinute)}"
                } else {
                    "Sem alerta"
                }

                SettingsClickableRow(
                    title = "Lembrete diário",
                    subtitle = subtitle,
                    onClick = onNavigateDailyReminder
                )
            }
        }
    }
}