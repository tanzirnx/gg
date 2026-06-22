package com.nitha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.nitha.ui.navigation.AppNavGraph
import com.nitha.ui.theme.NithaTheme
import com.nitha.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NithaApp()
        }
    }
}

@Composable
fun NithaApp() {
    val settingsViewModel: SettingsViewModel = viewModel()
    val profile by settingsViewModel.userProfile.collectAsState(initial = com.nitha.models.UserProfile())

    NithaTheme(
        themeName = profile.theme,
        darkTheme = true
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            AppNavGraph(navController = navController)
        }
    }
}
