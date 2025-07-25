<<<<<<< HEAD:app/src/main/java/com/igor/finansee/ui/screens/SignUpScreen.kt
package com.igor.finansee.ui.screens
=======
package com.igor.finansee.view.screens

>>>>>>> upstream/main:app/src/main/java/com/igor/finansee/view/screens/SignUpScreen.kt
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.igor.finansee.R
import com.igor.finansee.viewmodels.AuthViewModel

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    val scrollState = rememberScrollState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    fun doSignUp() {
        if (password != confirmPassword) {
            errorMessage = "As senhas não coincidem"
            return
        }
        isLoading = true
        errorMessage = null
        authViewModel.register(email, password, name) { success ->
            isLoading = false
            if (success) {
                Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                onNavigateToLogin()
            } else {
                errorMessage = "Erro ao cadastrar usuário"
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Fundo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(0.7f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SignUpHeader()
                    Spacer(modifier = Modifier.height(20.dp))
                    SignUpFields(
                        name = name,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        onNameChange = { name = it },
                        onEmailChange = { email = it },
                        onPasswordChange = { password = it },
                        onConfirmPasswordChange = { confirmPassword = it }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    SignUpFooter(
                        onSignUpClick = { doSignUp() },
                        onNavigateToLogin = onNavigateToLogin
                    )
                    if (isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator()
                    }
                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun SignUpHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Crie sua conta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Preencha os dados para começar",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SignUpFields(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit
) {
    Column {
        CustomTextField(
            value = name,
            label = "Nome",
            placeholder = "Digite seu nome completo",
            onValueChange = onNameChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Ícone de nome") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = email,
            label = "Email",
            placeholder = "Digite seu email",
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Ícone de email") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = password,
            label = "Senha",
            placeholder = "Crie uma senha forte",
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Ícone de senha") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = confirmPassword,
            label = "Confirmar Senha",
            placeholder = "Digite sua senha novamente",
            onValueChange = onConfirmPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Ícone de confirmar senha") }
        )
    }
}

@Composable
fun SignUpFooter(
    onSignUpClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onSignUpClick,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Cadastrar")
        }
        TextButton(onClick = onNavigateToLogin) {
            Text(text = "Já tem uma conta? Faça login")
        }
    }
}
