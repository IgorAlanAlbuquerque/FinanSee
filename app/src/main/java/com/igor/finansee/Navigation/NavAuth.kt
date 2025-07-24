package com.igor.finansee.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.authapp2.view.ForgotPasswordScreen
import com.igor.finansee.data.models.userList
import com.igor.finansee.ui.screens.*
import com.igor.finansee.viewmodels.AuthViewModel

object Routes {
    const val AUTO_CHOICE = "auto_choice"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val HOME = "home"
    const val SENSOR_ADAPT = "sensor_adapt"

}

@Composable
fun NavAuth(navController: NavHostController, authViewModel: AuthViewModel) {
    val testUser = userList[0]

    NavHost(
        navController = navController,
        startDestination = Routes.AUTO_CHOICE
    ) {
        /*composable(Routes.SENSOR_ADAPT) {
            SensorAdaptScreen()
        }*/
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
                authViewModel = authViewModel,  // passe o viewModel
                onNavigateToSignUp = {
                    navController.navigate(Routes.SIGN_UP) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    // ...
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                }
            )
        }
        composable("forgot_password") {
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
            HomeScreen(
                navController = navController,
                currentUser = testUser
            )
        }
    }
}
