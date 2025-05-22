package com.igor.finansee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.igor.finansee.ui.components.BottomNavigationBar
import com.igor.finansee.ui.components.DrawerContent
import com.igor.finansee.ui.components.TopBar
import com.igor.finansee.ui.screens.AddScreen
import com.igor.finansee.ui.screens.HomeScreen
import com.igor.finansee.ui.screens.PlansScreen
import com.igor.finansee.ui.screens.ProfileScreen
import com.igor.finansee.ui.screens.TransactionScreen
import com.igor.finansee.ui.theme.FinanSeeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val isDarkTheme = remember { mutableStateOf(false) }

            FinanSeeTheme (darkTheme = isDarkTheme.value) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = true,
                    drawerContent = {
                        DrawerContent(navController) { }
                    },
                    content = {
                        Scaffold(
                            topBar = {
                                TopBar(
                                    on3Points = {  },
                                    onOpenDrawer = { scope.launch { drawerState.open() } }
                                )
                            },
                            bottomBar = { BottomNavigationBar(navController) }
                        ) { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = "home",
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("home") {
                                    HomeScreen(
                                        navController,
                                        context = LocalContext.current
                                    )
                                }
                                composable("profile") { ProfileScreen(navController) }
                                composable("add") { AddScreen(navController) }
                                composable("plans") { PlansScreen(navController) }
                                composable("transactions") { TransactionScreen(navController) }
                            }
                        }
                    }
                )
            }
        }
    }
}