package com.igor.finansee.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.igor.finansee.viewmodels.AuthViewModel
import com.igor.finansee.viewmodels.ForgotPasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
) {
    val uiState by forgotPasswordViewModel.uiState.collectAsState()
    val email = uiState.email // Acessa o email a partir do estado

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val isEmailValid by remember(email) {
        derivedStateOf {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    val isButtonEnabled = !isLoading && isEmailValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Esqueceu sua senha?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sem problemas! Digite seu email e enviaremos um link para você.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email, // Usa o email do ViewModel
            // 2. Envia o evento de mudança para o ViewModel
            onValueChange = { forgotPasswordViewModel.updateEmail(it) },
            label = { Text("Seu email de cadastro") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Ícone de Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = email.isNotEmpty() && !isEmailValid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        AnimatedVisibility(visible = email.isNotEmpty() && !isEmailValid) {
            Text(
                text = "Por favor, insira um email válido.",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isLoading = true
                focusManager.clearFocus()
                // 3. Chama a função de lógica de negócio do ViewModel
                forgotPasswordViewModel.sendPasswordResetEmail(authViewModel) { success ->
                    isLoading = false
                    if (success) {
                        Toast.makeText(context, "Email de recuperação enviado!", Toast.LENGTH_LONG).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Falha ao enviar o email. Tente novamente.", Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Enviar Link", fontSize = 16.sp)
            }
        }

        TextButton(
            onClick = { if (!isLoading) navController.popBackStack() },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Lembrei a senha! Voltar")
        }
    }
}