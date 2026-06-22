package com.nitha.ui.navigation

/**
 * Navigation Routes for NITHA
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Chat : Screen("chat")
    data object Dashboard : Screen("dashboard")
    data object Skills : Screen("skills")
    data object Settings : Screen("settings")
}
