package com.igor.finansee.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.igor.finansee.Routes
import com.igor.finansee.data.models.User
import com.igor.finansee.ui.screens.*
import com.igor.finansee.viewmodels.AuthViewModel
import com.igor.finansee.viewmodels.LoginScreenViewModel
import kotlinx.coroutines.launch


fun NavGraphBuilder.NavAuth(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    composable(Routes.AUTO_CHOICE) {
        AutoChoiceScreen(
            onLoginClick = { navController.navigate(Routes.LOGIN) },
            onRegisterClick = { navController.navigate(Routes.SIGN_UP) }
        )
    }

    composable(Routes.LOGIN) {
        LoginScreen(
            authViewModel = authViewModel,
            onNavigateToSignUp = { navController.navigate(Routes.SIGN_UP) },
            onNavigateToHome = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            },
            onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
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
}