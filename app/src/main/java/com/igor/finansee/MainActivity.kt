package com.igor.finansee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.igor.finansee.data.datastore.UserPreferencesRepository
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
import com.igor.finansee.data.models.userList
import com.igor.finansee.ui.screens.NotificationSettingsScreen
import com.igor.finansee.ui.screens.SettingsScreen
import com.igor.finansee.viewmodels.SettingsViewModel
import com.igor.finansee.viewmodels.SettingsViewModelFactory

/*class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Cria o NavController
            val navController = rememberNavController()

            // Surface é só pra ter background correto
            Surface(color = MaterialTheme.colorScheme.background) {
                // Chama seu NavGraph passando o navController
                NavAuth(navController = navController)
            }
        }
    }
}*/


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val isDarkTheme = remember { mutableStateOf(false) }

            val currentUser = userList.first()

            FinanSeeTheme (darkTheme = isDarkTheme.value) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = true,
                    drawerContent = {
                        DrawerContent(navController) { }
                    },
                    content = {
                        var menuExpanded by remember { mutableStateOf(false) }
                        Scaffold(
                            topBar = {
                                TopBar(
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    isMenuExpanded = menuExpanded,
                                    onToggleMenu = { menuExpanded = !menuExpanded },
                                    onDismissMenu = { menuExpanded = false },
                                    onNavigate = { route ->
                                        menuExpanded = false
                                        navController.navigate(route)
                                    }
                                )
                            },
                            bottomBar = { BottomNavigationBar(navController) }
                        ) { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = "home",
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("home") { HomeScreen(navController, currentUser) }
                                composable("profile") { ProfileScreen(navController, currentUser) }
                                composable("add") { AddScreen(navController) }
                                composable("plans") { PlansScreen(navController) }
                                composable("transactions") { TransactionScreen(navController, currentUser) }

                                composable("settings") {
                                    val context = LocalContext.current
                                    val repository = remember { UserPreferencesRepository(context) }
                                    val factory = remember { SettingsViewModelFactory(repository) }
                                    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

                                    SettingsScreen(
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToNotifications = { navController.navigate("notification_settings") },
                                        viewModel = settingsViewModel
                                    )
                                }

                                composable("notification_settings") {
                                    val context = LocalContext.current
                                    val repository = remember { UserPreferencesRepository(context) }
                                    val factory = remember { SettingsViewModelFactory(repository) }
                                    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

                                    NotificationSettingsScreen(
                                        onNavigateBack = { navController.popBackStack() },
                                        viewModel = settingsViewModel
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
