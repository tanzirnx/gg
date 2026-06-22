package com.nitha.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nitha.ui.components.AIOrb
import com.nitha.ui.components.QuickActionButton
import com.nitha.ui.navigation.Screen
import com.nitha.ui.theme.*
import com.nitha.utils.Helpers
import com.nitha.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userProfile by viewModel.userProfile.collectAsState(initial = com.nitha.models.UserProfile())
    val aiStatus by viewModel.aiStatus.collectAsState(initial = "")
    val lastResponse by viewModel.lastResponse.collectAsState(initial = "")
    val isListening by viewModel.isListening.collectAsState(initial = false)
    val isSpeaking by viewModel.isSpeaking.collectAsState(initial = false)
    val batteryLevel by viewModel.batteryLevel.collectAsState(initial = 0)
    val ramUsage by viewModel.ramUsage.collectAsState(initial = "")
    val isOnline by viewModel.isOnline.collectAsState(initial = false)
    val apiStatus by viewModel.apiStatus.collectAsState(initial = false)
    val todayRequests by viewModel.todayRequests.collectAsState(initial = 0)
    val accessibilityEnabled by viewModel.accessibilityEnabled.collectAsState(initial = false)
    val notificationEnabled by viewModel.notificationEnabled.collectAsState(initial = false)

    var showPermissionDialog by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf("") }
    var showTextInput by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (!allGranted) {
            showPermissionDialog = true
        }
    }

    // Check permissions on launch
    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.CAMERA
        )
        val needsPermission = permissions.any {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needsPermission) {
            permissionLauncher.launch(permissions)
        }

        // Start foreground service
        viewModel.startForegroundService()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NITHA",
                            color = NithaPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (apiStatus) NithaSuccess else NithaError)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = NithaPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NithaBackground.copy(alpha = 0.95f)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NithaBackground.copy(alpha = 0.95f)
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = {},
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NithaPrimary,
                        selectedTextColor = NithaPrimary,
                        unselectedIconColor = NithaOnSurface.copy(alpha = 0.6f),
                        unselectedTextColor = NithaOnSurface.copy(alpha = 0.6f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
                    label = { Text("Chat") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Chat.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NithaPrimary,
                        selectedTextColor = NithaPrimary
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Skills") },
                    label = { Text("Skills") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Skills.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NithaPrimary,
                        selectedTextColor = NithaPrimary
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Dashboard.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NithaPrimary,
                        selectedTextColor = NithaPrimary
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { navController.navigate(Screen.Settings.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NithaPrimary,
                        selectedTextColor = NithaPrimary
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NithaBackground)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Dashboard
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = NithaSurface.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusItem("Battery", "$batteryLevel%", Icons.Default.BatteryFull)
                    StatusItem("RAM", ramUsage, Icons.Default.Memory)
                    StatusItem("Net", if (isOnline) "5G" else "OFF", Icons.Default.NetworkWifi)
                    StatusItem("API", if (apiStatus) "OK" else "--", Icons.Default.Cloud)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Orb
            AIOrb(
                status = aiStatus,
                modifier = Modifier.size(180.dp)
            )

            // Status Text
            Text(
                text = aiStatus,
                color = when (aiStatus) {
                    "Listening..." -> OrbListening
                    "Thinking..." -> OrbThinking
                    "Speaking..." -> OrbProcessing
                    else -> NithaPrimary.copy(alpha = 0.7f)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Last Response
            if (lastResponse.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = NithaSurface.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = lastResponse,
                        color = NithaOnSurface,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Voice / Text Input Area
            if (showTextInput) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ask NITHA anything...", color = NithaOnSurface.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NithaPrimary,
                        unfocusedBorderColor = NithaPrimary.copy(alpha = 0.3f),
                        focusedTextColor = NithaOnSurface,
                        unfocusedTextColor = NithaOnSurface
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    viewModel.processTextInput(textInput)
                                    textInput = ""
                                    showTextInput = false
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = NithaPrimary)
                        }
                    }
                )
            } else {
                // Main Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Voice Button
                    FilledIconButton(
                        onClick = {
                            if (isListening) {
                                viewModel.stopVoiceInput()
                            } else {
                                viewModel.startVoiceInput()
                            }
                        },
                        modifier = Modifier.size(72.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isListening) NithaError else NithaPrimary,
                            contentColor = NithaBackground
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Text Button
                    FilledIconButton(
                        onClick = { showTextInput = true },
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = NithaSurface,
                            contentColor = NithaPrimary
                        ),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Keyboard, contentDescription = "Text")
                    }

                    // Chat Button
                    FilledIconButton(
                        onClick = { navController.navigate(Screen.Chat.route) },
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = NithaSurface,
                            contentColor = NithaPrimary
                        ),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = "Chat")
                    }
                }
            }

            // Quick Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = { Icon(Icons.Default.Apps, contentDescription = "Apps", tint = NithaPrimary) },
                    label = "Apps",
                    onClick = { viewModel.processTextInput("Open app drawer") }
                )
                QuickActionButton(
                    icon = { Icon(Icons.Default.Folder, contentDescription = "Files", tint = NithaPrimary) },
                    label = "Files",
                    onClick = { viewModel.processTextInput("Open file manager") }
                )
                QuickActionButton(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = NithaPrimary) },
                    label = "Search",
                    onClick = { viewModel.processTextInput("Search web") }
                )
                QuickActionButton(
                    icon = { Icon(Icons.Default.Note, contentDescription = "Notes", tint = NithaPrimary) },
                    label = "Notes",
                    onClick = { viewModel.processTextInput("Create note") }
                )
            }
        }
    }

    // Permission Dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permissions Required", color = NithaPrimary) },
            text = { Text("NITHA needs microphone and notification permissions to work properly.", color = NithaOnSurface) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        permissionLauncher.launch(arrayOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.CAMERA
                        ))
                    }
                ) {
                    Text("Grant", color = NithaPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel", color = NithaOnSurface.copy(alpha = 0.7f))
                }
            },
            containerColor = NithaSurface
        )
    }
}

@Composable
private fun StatusItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = NithaPrimary.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = value,
            color = NithaOnSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
