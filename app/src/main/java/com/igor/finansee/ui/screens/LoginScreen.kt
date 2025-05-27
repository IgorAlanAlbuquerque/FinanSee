package com.igor.finansee.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Email", color = Color.Black)
            OutlinedTextField(
                value = email,
                onValueChange = { newValue -> email = newValue },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Password", color = Color.Black)
            OutlinedTextField(
                value = password,
                onValueChange = { newValue -> password = newValue },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Esqueci minha senha")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Sign up")
        }
    }
}
