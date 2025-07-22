package com.igor.finansee.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.igor.finansee.viewmodels.SettingsViewModel
import com.igor.finansee.R

@Composable
fun DailyReminderScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current

    var selectedHour by remember { mutableIntStateOf(17) }
    var selectedMinute by remember { mutableIntStateOf(0) }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            selectedHour = hour
            selectedMinute = minute
        }, selectedHour, selectedMinute, true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_clock),
            contentDescription = "Lembrete",
            modifier = Modifier.size(120.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Lembrete diário", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Sabemos que adotar o hábito de acompanhar seus gastos diariamente é complicado, e podemos ajudar você com isso. Quando você gostaria de receber nosso lembrete?",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { timePickerDialog.show() },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = String.format("%02d:%02d", selectedHour, selectedMinute),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                viewModel.setDailyReminder(context, selectedHour, selectedMinute)
                onNavigateBack()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("ACOMPANHAR LEMBRETE")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                viewModel.disableDailyReminder(context)
                onNavigateBack()
            }
        ) {
            Text("NÃO, OBRIGADO")
        }
    }
}