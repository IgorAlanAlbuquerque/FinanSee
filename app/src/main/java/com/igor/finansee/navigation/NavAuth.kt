package com.igor.finansee.navigation

import SignUpScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.igor.finansee.Routes
import com.igor.finansee.view.screens.AutoChoiceScreen
import com.igor.finansee.view.screens.ForgotPasswordScreen
import com.igor.finansee.view.screens.LoginScreen
import com.igor.finansee.viewmodels.AuthViewModel


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