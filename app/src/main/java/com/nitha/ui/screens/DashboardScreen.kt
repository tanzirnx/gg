package com.nitha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nitha.ui.components.DashboardCard
import com.nitha.ui.theme.*
import com.nitha.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel()
) {
    val batteryLevel by viewModel.batteryLevel.collectAsState(initial = 0)
    val ramUsage by viewModel.ramUsage.collectAsState(initial = "")
    val storageInfo by viewModel.storageInfo.collectAsState(initial = "")
    val isOnline by viewModel.isOnline.collectAsState(initial = false)
    val memoryCount by viewModel.memoryCount.collectAsState(initial = 0)
    val apiStatus by viewModel.apiStatus.collectAsState(initial = false)
    val selectedModel by viewModel.selectedModel.collectAsState(initial = "")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Dashboard",
                        color = NithaPrimary,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = NithaPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NithaBackground.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(NithaBackground)
                .padding(paddingValues)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                DashboardCard(
                    title = "Battery",
                    value = "$batteryLevel%",
                    icon = { 
                        Icon(
                            Icons.Default.BatteryFull, 
                            contentDescription = null,
                            tint = if (batteryLevel > 20) NithaSuccess else NithaError,
                            modifier = Modifier.size(28.dp)
                        ) 
                    }
                )
            }
            item {
                DashboardCard(
                    title = "RAM",
                    value = ramUsage,
                    icon = { 
                        Icon(
                            Icons.Default.Memory, 
                            contentDescription = null,
                            tint = NithaPrimary,
                            modifier = Modifier.size(28.dp)
                        ) 
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Storage",
                    value = storageInfo,
                    icon = { 
                        Icon(
                            Icons.Default.Storage, 
                            contentDescription = null,
                            tint = NithaAccent,
                            modifier = Modifier.size(28.dp)
                        ) 
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Internet",
                    value = if (isOnline) "Online" else "Offline",
                    icon = { 
                        Icon(
                            Icons.Default.NetworkWifi, 
                            contentDescription = null,
                            tint = if (isOnline) NithaSuccess else NithaError,
                            modifier = Modifier.size(28.dp)
                        ) 
                    }
                )
            }
            item {
                DashboardCard(
                    title = "AI Model",
                    value = selectedModel.split("/").last().take(15),
                    icon = { 
                        Icon(
                            Icons.Default.SmartToy, 
                            contentDescription = null,
                            tint = NithaPrimary,
                            modifier = Modifier.size(28.dp)
                        ) 
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Memories",
                    value = "$memoryCount",
                    icon = { 
                        Icon(
                            Icons.Default.Psychology, 
                            contentDescription = null,
                            tint = OrbThinking,
                            modifier = Modifier.size(28.dp)
                        ) 
                    }
                )
            }
            item {
                DashboardCard(
                    title = "API Status",
                    value = if (apiStatus) "Connected" else "Disconnected",
                    icon = { 
                        Icon(
                            Icons.Default.Cloud, 
                            contentDescription = null,
                            tint = if (apiStatus) NithaSuccess else NithaError,
                            modifier = Modifier.size(28.dp)
                        ) 
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Version",
                    value = "1.0.0",
                    icon = { 
                        Icon(
                            Icons.Default.Info, 
                            contentDescription = null,
                            tint = NithaPrimary,
                            modifier = Modifier.size(28.dp)
                        ) 
                    }
                )
            }
        }
    }
}
