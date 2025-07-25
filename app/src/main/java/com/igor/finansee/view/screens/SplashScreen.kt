package com.igor.finansee.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.igor.finansee.Routes
import com.igor.finansee.viewmodels.AuthViewModel

@Composable
fun SplashScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.collectAsState()
    LaunchedEffect(key1 = true) {

        val destination = if (user != null) {
            Routes.HOME
        } else {
            Routes.AUTO_CHOICE
        }

        navController.navigate(destination) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}