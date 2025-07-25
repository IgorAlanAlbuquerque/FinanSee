package com.igor.finansee.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.igor.finansee.data.datastore.ThemeOption
import com.igor.finansee.view.components.SettingsClickableRow
import com.igor.finansee.view.components.SettingsSwitchRow
import com.igor.finansee.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val userPreferences by viewModel.uiState.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showDeleteDataDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = userPreferences.themeOption,
            onThemeSelected = { themeOption ->
                viewModel.setThemeOption(themeOption)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showDeleteDataDialog) {
        ConfirmationDialog(
            title = "Apagar todos os dados?",
            text = "Esta ação é irreversível. Todas as suas transações e configurações serão permanentemente removidas.",
            onConfirm = {
                // TODO: Chamar a função no ViewModel para apagar os dados
                showDeleteDataDialog = false
            },
            onDismiss = { showDeleteDataDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preferências") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
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
            item { SettingsHeader(title = "Geral") }
            item {
                val subtitleText = when (userPreferences.themeOption) {
                    ThemeOption.LIGHT -> "Claro"
                    ThemeOption.DARK -> "Escuro"
                    ThemeOption.SYSTEM -> "Padrão do sistema"
                }

                SettingsClickableRow(
                    title = "Tema",
                    subtitle = subtitleText,
                    onClick = { showThemeDialog = true }
                )
            }
            item {
                SettingsClickableRow(
                    title = "Moeda principal",
                    subtitle = "BRL (Real Brasileiro)", // TODO: Mostrar a moeda salva
                    onClick = { /* TODO: Abrir diálogo para selecionar moeda */ }
                )
            }
            item { SettingsHeader(title = "Interatividade") }
            item {
                SettingsSwitchRow(
                    title = "Ativar Animações",
                    subtitle = "Habilitar transições e efeitos visuais no app",
                    checked = userPreferences.areAnimationsEnabled,
                    onCheckedChange = viewModel::setAnimationsEnabled
                )
            }
            item { SettingsHeader(title = "Segurança e Dados") }
            item {
                SettingsSwitchRow(
                    title = "Proteger com senha",
                    subtitle = "Exigir biometria ou PIN para abrir o app",
                    checked = userPreferences.isAppLockEnabled,
                    onCheckedChange = viewModel::setAppLockEnabled
                )
            }
            item {
                SettingsClickableRow(
                    title = "Fazer backup dos dados",
                    subtitle = "Salvar uma cópia de segurança na nuvem",
                    onClick = { /* TODO: Implementar lógica de backup (ex: Google Drive API) */ }
                )
            }
            item {
                SettingsClickableRow(
                    title = "Exportar dados",
                    subtitle = "Salvar suas transações em um arquivo (CSV, PDF)",
                    onClick = { /* TODO: Implementar lógica para gerar e compartilhar arquivo */ }
                )
            }
            item {
                SettingsClickableRow(
                    title = "Apagar todos os dados",
                    subtitle = "Remover permanentemente suas informações",
                    onClick = { showDeleteDataDialog = true }
                )
            }

            item { SettingsHeader(title = "Sobre") }
            item {
                SettingsClickableRow(
                    title = "Avalie o aplicativo",
                    subtitle = "Deixe sua opinião na Play Store",
                    onClick = { /* TODO: Abrir o link da Play Store */ }
                )
            }
            item {
                SettingsClickableRow(
                    title = "Enviar feedback",
                    subtitle = "Ajude-nos a melhorar o aplicativo",
                    onClick = { /* TODO: Abrir cliente de email com seu endereço de suporte */ }
                )
            }
            item {
                SettingsClickableRow(
                    title = "Política de Privacidade",
                    subtitle = "",
                    onClick = { /* TODO: Abrir URL da sua política de privacidade */ }
                )
            }
            item {
                // Exemplo de como obter a versão do app. Requer configuração no build.gradle
                // val versionName = BuildConfig.VERSION_NAME
                SettingsInfoRow(title = "Versão", info = "1.0.0")
            }
        }
    }
}

@Composable
private fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(top = 16.dp)
    )
}

@Composable
private fun ThemeSelectionDialog(
    currentTheme: ThemeOption,
    onThemeSelected: (ThemeOption) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Escolha um tema") },
        text = {
            Column {
                val themeOptions = mapOf(
                    ThemeOption.LIGHT to "Claro",
                    ThemeOption.DARK to "Escuro",
                    ThemeOption.SYSTEM to "Padrão do sistema"
                )

                themeOptions.forEach { (option, label) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (currentTheme == option),
                                onClick = { onThemeSelected(option) }
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentTheme == option),
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("CONFIRMAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR")
            }
        }
    )
}

@Composable
fun SettingsInfoRow(title: String, info: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(text = info, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}