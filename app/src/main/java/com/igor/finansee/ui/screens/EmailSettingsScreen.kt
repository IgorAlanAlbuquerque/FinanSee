package com.igor.finansee.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.igor.finansee.ui.components.SettingsSwitchRow
import com.igor.finansee.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val userPreferences by viewModel.uiState.collectAsState()

    val isMasterSwitchChecked = userPreferences.receiveNews ||
            userPreferences.receiveFinancialAlerts ||
            userPreferences.receivePremiumInfo ||
            userPreferences.receivePartnerOffers

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuração de Email") },
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Receber notificações",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isMasterSwitchChecked,
                        onCheckedChange = { isEnabled ->
                            viewModel.setAllEmailPreferences(isEnabled)
                        }
                    )
                }
            }
            item {
                SettingsSwitchRow(
                    title = "Novidades do Mobills",
                    subtitle = "Novas funcionalidades e as melhores dicas para transformar sua vida financeira para melhor.",
                    checked = userPreferences.receiveNews,
                    onCheckedChange = viewModel::setReceiveNews
                )
            }
            item {
                SettingsSwitchRow(
                    title = "Alertas financeiros",
                    subtitle = "Lembretes sobre datas de vencimento, gastos, transferências, pagamentos, desempenho, planejamento mensal e objetivos.",
                    checked = userPreferences.receiveFinancialAlerts,
                    onCheckedChange = viewModel::setReceiveFinancialAlerts
                )
            }
            item {
                SettingsSwitchRow(
                    title = "Informações sobre o Premium",
                    subtitle = "Confirmação de pagamento, ativação do Premium, promoções e novidades dos planos.",
                    checked = userPreferences.receivePremiumInfo,
                    onCheckedChange = viewModel::setReceivePremiumInfo
                )
            }
            item {
                SettingsSwitchRow(
                    title = "Parcerias Mobills",
                    subtitle = "Cupons, produtos financeiros, clube de benefício, condições especiais e conteúdos.",
                    checked = userPreferences.receivePartnerOffers,
                    onCheckedChange = viewModel::setReceivePartnerOffers
                )
            }
        }
    }
}