package com.igor.finansee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.igor.finansee.ui.screens.ForgotPasswordScreen
import com.igor.finansee.data.AuthRepository
import com.igor.finansee.data.datastore.UserPreferencesRepository
import com.igor.finansee.data.models.userList
import com.igor.finansee.ui.components.BottomNavigationBar
import com.igor.finansee.ui.components.CircularActionMenu
import com.igor.finansee.ui.components.DrawerContent
import com.igor.finansee.ui.components.TopBar
import com.igor.finansee.ui.screens.*
import com.igor.finansee.viewmodels.AuthViewModel
import com.igor.finansee.viewmodels.SettingsViewModel
import com.igor.finansee.viewmodels.SettingsViewModelFactory
import kotlinx.coroutines.launch
import com.igor.finansee.ui.theme.FinanSeeTheme
import com.igor.finansee.viewmodels.AuthViewModelFactory

object Routes {
    const val AUTO_CHOICE = "auto_choice"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val PLANS = "plans"
    const val DONUT_CHART = "donutChart"
    const val ADD_EXPENSE = "add_expense"
    const val EDIT_EXPENSE = "edit_expense"
    const val TRANSACTIONS = "transactions"
    const val SETTINGS = "settings"
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val EMAIL_SETTINGS = "email_settings"
    const val DAILY_REMINDER = "daily_reminder"
}
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

            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(AuthRepository())
            )

            FinanSeeTheme(selectedTheme = uiState.themeOption) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = true,
                    drawerContent = {
                        DrawerContent(navController, onCloseDrawer = {
                            scope.launch { drawerState.close() }
                        }) { }
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
                                startDestination = Routes.AUTO_CHOICE,  // Aqui iniciamos com a autenticação
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                // Tela de escolha de autenticação
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

                                // Tela de Login
                                composable(Routes.LOGIN) {
                                    LoginScreen(
                                        authViewModel = authViewModel,
                                        onNavigateToSignUp = {
                                            navController.navigate(Routes.SIGN_UP) {
                                                popUpTo(Routes.LOGIN) { inclusive = true }
                                            }
                                        },
                                        onNavigateToHome = {
                                            navController.navigate(Routes.HOME)
                                        },
                                        onNavigateToForgotPassword = {
                                            navController.navigate("forgot_password")
                                        }
                                    )
                                }

                                // Tela de recuperação de senha
                                composable("forgot_password") {
                                    ForgotPasswordScreen(
                                        navController = navController,
                                        authViewModel = authViewModel
                                    )
                                }

                                // Tela de cadastro
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
                                        currentUser = currentUser
                                    )
                                }

                                // Outras telas
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
