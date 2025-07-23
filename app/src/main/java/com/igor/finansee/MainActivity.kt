package com.igor.finansee

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.igor.finansee.data.datastore.UserPreferencesRepository
import com.igor.finansee.ui.components.BottomNavigationBar
import com.igor.finansee.ui.components.DrawerContent
import com.igor.finansee.ui.components.TopBar
import com.igor.finansee.ui.screens.HomeScreen
import com.igor.finansee.ui.screens.PlansScreen
import com.igor.finansee.ui.screens.ProfileScreen
import com.igor.finansee.ui.screens.TransactionScreen
import com.igor.finansee.ui.theme.FinanSeeTheme
import kotlinx.coroutines.launch
import com.igor.finansee.data.models.userList
import com.igor.finansee.ui.screens.DailyReminderScreen
import com.igor.finansee.ui.screens.EmailSettingsScreen
import com.igor.finansee.ui.screens.NotificationSettingsScreen
import com.igor.finansee.ui.screens.SettingsScreen
import com.igor.finansee.viewmodels.SettingsViewModel
import com.igor.finansee.viewmodels.SettingsViewModelFactory
import androidx.compose.material3.FabPosition
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.igor.finansee.ui.components.CircularActionMenu
import com.igor.finansee.ui.screens.AddAccountScreen
import com.igor.finansee.ui.screens.AddExpenseScreen
import com.igor.finansee.ui.screens.DonutChartScreen

import com.igor.finansee.ui.screens.EditExpenseScreen

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

        val settingsViewModel: SettingsViewModel by viewModels {
            SettingsViewModelFactory(UserPreferencesRepository(this))
        }

        setContent {
            val uiState by settingsViewModel.uiState.collectAsState()
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val currentUser = userList.first()

            FinanSeeTheme(selectedTheme = uiState.themeOption) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = true,
                    drawerContent = {
                        DrawerContent(navController, onCloseDrawer = {
                            scope.launch { drawerState.close() }
                        },) { }
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
                            bottomBar = { BottomNavigationBar(navController) },
                            floatingActionButton = {
                                Box(modifier = Modifier.offset(y = 55.dp)) {
                                    CircularActionMenu(navController)
                                }
                            },
                            floatingActionButtonPosition = FabPosition.Center,
                        ) { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = "home",
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("home") { HomeScreen(navController, currentUser) }
                                composable("profile") { ProfileScreen(navController, currentUser) }
                                composable("profile") { ProfileScreen(navController, currentUser) }
                                composable("plans") { PlansScreen(currentUser) }
                                composable("donutChart") { DonutChartScreen() }
                                composable("add_expense") { AddExpenseScreen(navController) }
                                composable("edit_expense") { EditExpenseScreen() }
                                composable("transactions") { TransactionScreen(currentUser) }
                                composable(
                                    route = "add_account_screen?initialTab={tab}",
                                    arguments = listOf(navArgument("tab") {
                                        type = NavType.StringType
                                        defaultValue = "banco"
                                    })
                                ) { backStackEntry ->
                                    val initialTab = backStackEntry.arguments?.getString("tab") ?: "banco"

                                    AddAccountScreen(
                                        navController = navController,
                                        currentUser = currentUser,
                                        initialTab = initialTab
                                    )
                                }

                                composable("settings") {
                                    val context = LocalContext.current
                                    val repository =
                                        remember { UserPreferencesRepository(context) }
                                    val factory =
                                        remember { SettingsViewModelFactory(repository) }
                                    val settingsViewModel: SettingsViewModel =
                                        viewModel(factory = factory)

                                    SettingsScreen(
                                        onNavigateBack = { navController.popBackStack() },
                                        viewModel = settingsViewModel
                                    )
                                }

                                composable("notification_settings") {
                                    val context = LocalContext.current
                                    val repository =
                                        remember { UserPreferencesRepository(context) }
                                    val factory =
                                        remember { SettingsViewModelFactory(repository) }
                                    val settingsViewModel: SettingsViewModel =
                                        viewModel(factory = factory)

                                    NotificationSettingsScreen(
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToEmailSettings = { navController.navigate("email_settings") },
                                        onNavigateDailyReminder = { navController.navigate("daily_reminder") },
                                        viewModel = settingsViewModel
                                    )
                                }

                                composable("email_settings") {
                                    val context = LocalContext.current
                                    val repository =
                                        remember { UserPreferencesRepository(context) }
                                    val factory =
                                        remember { SettingsViewModelFactory(repository) }
                                    val settingsViewModel: SettingsViewModel =
                                        viewModel(factory = factory)

                                    EmailSettingsScreen(
                                        onNavigateBack = { navController.popBackStack() },
                                        viewModel = settingsViewModel
                                    )
                                }

                                composable("daily_reminder") {
                                    val context = LocalContext.current
                                    val repository =
                                        remember { UserPreferencesRepository(context) }
                                    val factory =
                                        remember { SettingsViewModelFactory(repository) }
                                    val settingsViewModel: SettingsViewModel =
                                        viewModel(factory = factory)

                                    DailyReminderScreen(
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
