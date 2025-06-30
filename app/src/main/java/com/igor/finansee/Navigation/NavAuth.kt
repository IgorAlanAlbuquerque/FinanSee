package com.igor.finansee.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.igor.finansee.data.models.User
import com.igor.finansee.data.models.userList
import com.igor.finansee.ui.screens.*

object Routes {
    const val AUTO_CHOICE = "auto_choice"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val HOME = "home"
}

@Composable
fun NavAuth(navController: NavHostController) {
    val testUser = userList[0] // Pega o primeiro usuário da lista
    NavHost(
        navController = navController,
        startDestination = Routes.AUTO_CHOICE
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
                onNavigateToSignUp = {
                    navController.navigate(Routes.SIGN_UP) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                }
            )
        }
        composable(Routes.SIGN_UP) {
            SignUpScreen(
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
