package com.igor.finansee.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.states.SignUpUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { currentState ->
            currentState.copy(name = name)
        }
    }

    fun updateEmail(email: String) {
        _uiState.update { currentState ->
            currentState.copy(email = email)
        }
    }

    fun updatePassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(password = password)
        }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(confirmPassword = confirmPassword)
        }
    }

    fun signUp() {
        val currentState = _uiState.value

        if (currentState.name.isBlank() || currentState.email.isBlank() || currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor, preencha todos os campos.") }
            return
        }

        if (currentState.password != currentState.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "As senhas não coincidem.") }
            return
        }


        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            delay(2000)

            val isSuccess = true

            if (isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, isSuccess = true)
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Erro ao cadastrar usuário.")
                }
            }
        }
    }
}