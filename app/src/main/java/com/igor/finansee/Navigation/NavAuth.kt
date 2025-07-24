package com.igor.finansee.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.igor.finansee.data.models.User
import com.igor.finansee.ui.screens.*
import com.igor.finansee.viewmodels.AuthViewModel
import com.igor.finansee.viewmodels.LoginScreenViewModel
import kotlinx.coroutines.launch

object Routes {
    const val AUTO_CHOICE = "auto_choice"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val HOME = "home"
    const val SENSOR_ADAPT = "sensor_adapt"
    const val FORGOT_PASSWORD = "forgot_password"
}

@Composable
fun NavAuth(navController: NavHostController, authViewModel: AuthViewModel) {
    // State para armazenar o usuário atual
    val currentUser = remember { mutableStateOf<User?>(null) }

    // Lidar com o estado assíncrono do usuário
    LaunchedEffect(Unit) {
        // A função getCurrentUser deve ser chamada de forma assíncrona
        try {
            val user = authViewModel.getCurrentUser()
            currentUser.value = user
        } catch (e: Exception) {
            Log.e("NavAuth", "Erro ao obter usuário: $e")
        }
    }

    // O NavHost usa startDestination baseado no estado do usuário
    NavHost(
        navController = navController,
        startDestination = if (currentUser.value != null) Routes.HOME else Routes.AUTO_CHOICE
    ) {
        composable(Routes.AUTO_CHOICE) {
            AutoChoiceScreen(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.AUTO_CHOICE) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.SIGN_UP) {
                        popUpTo(Routes.AUTO_CHOICE) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = {
                    navController.navigate(Routes.SIGN_UP) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Routes.SIGN_UP) {
            SignUpScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SIGN_UP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val user = currentUser.value
            if (user != null) {
                HomeScreen(
                    navController = navController,
                    currentUser = user
                )
            }
        }
    }
}
