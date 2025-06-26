package com.igor.finansee.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.categoryList
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen() {
    var valor by remember { mutableStateOf("") }
    var categoriaSelecionada by remember { mutableStateOf<Category?>(null) }
    val categoriasDespesa = categoryList.filter { it.id >= 6 }
    val dataAtual = LocalDate.now()
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Cadastrar Despesa",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = categoriaSelecionada?.name ?: "Selecione uma categoria",
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoria") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categoriasDespesa.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria.name) },
                        onClick = {
                            categoriaSelecionada = categoria
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Data: $dataAtual")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                Log.d("Despesa", "Valor: $valor, Categoria: ${categoriaSelecionada?.name}, Data: $dataAtual")
            },
            enabled = valor.isNotBlank() && categoriaSelecionada != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Despesa")
        }
    }
}
