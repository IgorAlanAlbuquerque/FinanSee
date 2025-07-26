package com.igor.finansee.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.finansee.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.igor.finansee.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser
    private val authStateListener: FirebaseAuth.AuthStateListener =
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                viewModelScope.launch {
                    _currentUser.value = repository.getCurrentUser()
                }
            } else {
                _currentUser.value = null
            }
        }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

    fun register(email: String, password: String, name: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.registerUser(email, password, name)
            onResult(user != null)
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.loginUser(email, password)
            if (user != null) {
                _currentUser.value = user
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
    }

    fun resetPassword(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.resetPassword(email)
            onResult(success)
        }
    }

    fun loginWithGoogle(idToken: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.loginWithGoogle(idToken)
            if (user != null) {
                _currentUser.value = user
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        return repository.getGoogleSignInClient(context)
    }
}