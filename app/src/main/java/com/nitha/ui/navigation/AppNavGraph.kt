package com.nitha.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nitha.ui.screens.ChatScreen
import com.nitha.ui.screens.DashboardScreen
import com.nitha.ui.screens.HomeScreen
import com.nitha.ui.screens.SettingsScreen
import com.nitha.ui.screens.SkillsScreen

/**
 * Navigation Graph for NITHA
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController = navController)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.Skills.route) {
            SkillsScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
