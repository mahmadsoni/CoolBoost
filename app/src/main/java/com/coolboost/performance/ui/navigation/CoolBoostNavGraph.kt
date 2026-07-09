@file:OptIn(ExperimentalMaterial3Api::class)

package com.coolboost.performance.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.coolboost.performance.core.AppContainer
import com.coolboost.performance.ui.screens.*

sealed class Destination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Dashboard : Destination("dashboard", "Home", Icons.Filled.Dashboard)
    data object Cooling : Destination("cooling", "Cooling", Icons.Filled.CleaningServices)
    data object Optimization : Destination("optimization", "Apps", Icons.Filled.Settings)
    data object Analytics : Destination("analytics", "Stats", Icons.Filled.Analytics)
    data object Battery : Destination("battery", "Battery", Icons.Filled.BatteryStd)
    data object Settings : Destination("settings", "Settings", Icons.Filled.Settings)
}

private val bottomNavItems = listOf(
    Destination.Dashboard, Destination.Cooling, Destination.Optimization, Destination.Analytics, Destination.Battery
)

@Composable
fun CoolBoostNavGraph(container: AppContainer, onRequestUsageAccess: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = { navController.navigate(Destination.Settings.route) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
            )
        },
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                bottomNavItems.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(
                Destination.Dashboard.route,
                enterTransition = { fadeIn(tween(200)) },
                exitTransition = { fadeOut(tween(200)) }
            ) {
                DashboardScreen(
                    container = container,
                    onNavigateToCooling = { navController.navigate(Destination.Cooling.route) },
                    onNavigateToOptimization = { navController.navigate(Destination.Optimization.route) },
                    onNavigateToAnalytics = { navController.navigate(Destination.Analytics.route) }
                )
            }
            composable(Destination.Cooling.route) { CoolingScreen(container) }
            composable(Destination.Optimization.route) { OptimizationScreen(container, onRequestUsageAccess) }
            composable(Destination.Analytics.route) { AnalyticsScreen(container) }
            composable(Destination.Battery.route) { BatteryHealthScreen(container) }
            composable(Destination.Settings.route) { SettingsScreen(container) }
        }
    }
}
