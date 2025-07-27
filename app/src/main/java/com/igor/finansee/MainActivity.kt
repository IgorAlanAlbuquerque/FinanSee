package com.igor.finansee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.igor.finansee.data.AppDatabase
import com.igor.finansee.data.datastore.UserPreferencesRepository
import com.igor.finansee.data.repository.AuthRepository
import com.igor.finansee.navigation.NavAuth
import com.igor.finansee.view.components.BottomNavigationBar
import com.igor.finansee.view.components.CircularActionMenu
import com.igor.finansee.view.components.DrawerContent
import com.igor.finansee.view.components.TopBar
import com.igor.finansee.view.screens.*
import com.igor.finansee.view.theme.FinanSeeTheme
import com.igor.finansee.viewmodels.AuthViewModel
import com.igor.finansee.viewmodels.AuthViewModelFactory
import com.igor.finansee.viewmodels.CreatePlanViewModel
import com.igor.finansee.viewmodels.SettingsViewModel
import com.igor.finansee.viewmodels.SettingsViewModelFactory
import kotlinx.coroutines.launch

object Routes {

    const val SENSOR = "sensor_adapt"
    const val SPLASH = "splash_screen"
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
    const val FORGOT_PASSWORD = "forgot_password"
    // Rota adicionada para a tela de criação/edição de plano
    const val CREATE_PLAN = "createPlan/{month}"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsViewModel: SettingsViewModel by viewModels {
            SettingsViewModelFactory(UserPreferencesRepository(this))
        }

        setContent {
            val context = LocalContext.current
            val uiState by settingsViewModel.uiState.collectAsState()
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val userDao = AppDatabase.getDatabase(context).userDao()
            val firestore = Firebase.firestore
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(AuthRepository(userDao, firestore))
            )

            FinanSeeTheme(selectedTheme = uiState.themeOption) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = true,
                    drawerContent = {
                        DrawerContent(
                            navController = navController,
                            onCloseDrawer = {
                                scope.launch { drawerState.close() }
                            },
                            onLogout = {
                                authViewModel.logout()
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onSendNotification = {}
                        )
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
                                startDestination = Routes.SPLASH,
                                modifier = Modifier.padding(innerPadding)
                            ) {

                                composable(Routes.SPLASH) {
                                    SplashScreen(navController, authViewModel)
                                }

                                NavAuth(navController, authViewModel)

                                composable(Routes.HOME) {
                                    HomeScreen(
                                        navController = navController,
                                        authViewModel
                                    )
                                }

                                composable(Routes.PROFILE) {
                                    ProfileScreen(navController, authViewModel)
                                }

                                composable(Routes.PLANS) {
                                    // Ordem dos parâmetros corrigida
                                    PlansScreen(
                                        authViewModel = authViewModel,
                                        navController = navController
                                    )
                                }

                                // Rota adicionada para a CreatePlanScreen
                                composable(
                                    route = Routes.CREATE_PLAN,
                                    arguments = listOf(navArgument("month") { type = NavType.StringType })
                                ) { backStackEntry ->
                                    val month = backStackEntry.arguments?.getString("month")
                                    if (month != null) {
                                        CreatePlanScreen(
                                            authViewModel = authViewModel,
                                            selectedMonth = month,
                                            onNavigateBack = {
                                                navController.popBackStack()
                                            }
                                        )
                                    }
                                }

                                composable(Routes.DONUT_CHART) {
                                    DonutChartScreen(authViewModel)
                                }

                                composable(Routes.ADD_EXPENSE) {
                                    AddExpenseScreen(navController, authViewModel)
                                }

                                composable(Routes.EDIT_EXPENSE) {
                                    EditExpenseScreen(
                                        authViewModel
                                    )
                                }

                                composable(Routes.TRANSACTIONS) {
                                    TransactionScreen(authViewModel)
                                }


                                composable(Routes.SETTINGS) {
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

                                composable(Routes.NOTIFICATION_SETTINGS) {
                                    val context = LocalContext.current
                                    val repository =
                                        remember { UserPreferencesRepository(context) }
                                    val factory =
                                        remember { SettingsViewModelFactory(repository) }
                                    val settingsViewModel: SettingsViewModel =
                                        viewModel(factory = factory)

                                    NotificationSettingsScreen(
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToEmailSettings = {
                                            navController.navigate(
                                                Routes.EMAIL_SETTINGS
                                            )
                                        },
                                        onNavigateDailyReminder = {
                                            navController.navigate(
                                                Routes.DAILY_REMINDER
                                            )
                                        },
                                        viewModel = settingsViewModel
                                    )
                                }

                                composable(Routes.EMAIL_SETTINGS) {
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
                                composable (Routes.SENSOR){
                                    SensorAdaptScreen()
                                }

                                composable(Routes.DAILY_REMINDER) {
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
                                        authViewModel,
                                        initialTab = initialTab,
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
