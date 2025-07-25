package com.igor.finansee.viewmodels
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
data class ForgotPasswordUiState(
    val email: String = ""
)

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()


    fun updateEmail(email: String) {
        _uiState.update { currentState ->
            currentState.copy(email = email)
        }
    }
    fun sendPasswordResetEmail(authViewModel: AuthViewModel, onResult: (Boolean) -> Unit) {
        val email = _uiState.value.email
        if (email.isNotBlank()) {
            authViewModel.resetPassword(email, onResult)
        } else {
            onResult(false)
        }
    }
}