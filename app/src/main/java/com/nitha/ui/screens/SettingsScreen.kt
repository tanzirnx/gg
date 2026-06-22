package com.nitha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nitha.ui.theme.*
import com.nitha.utils.Constants
import com.nitha.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val profile by viewModel.userProfile.collectAsState(initial = com.nitha.models.UserProfile())
    val apiKeyValid by viewModel.apiKeyValid.collectAsState(initial = com.nitha.models.UserProfile())
    val isChecking by viewModel.isChecking.collectAsState(initial = com.nitha.models.UserProfile())

    var showApiKey by remember { mutableStateOf(false) }
    var tempApiKey by remember { mutableStateOf(profile.apiKey) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NithaBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AI Settings Section
            SettingsSection(title = "AI Configuration") {
                // API Key
                OutlinedTextField(
                    value = tempApiKey,
                    onValueChange = { tempApiKey = it },
                    label = { Text("OpenRouter API Key", color = NithaOnSurface.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { showApiKey = !showApiKey }) {
                                Icon(
                                    imageVector = if (showApiKey) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle visibility",
                                    tint = NithaPrimary
                                )
                            }
                            IconButton(onClick = { viewModel.checkApiKey(tempApiKey) }) {
                                if (isChecking) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = NithaPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = when (apiKeyValid) {
                                            true -> Icons.Default.CheckCircle
                                            false -> Icons.Default.Error
                                            null -> Icons.Default.Help
                                        },
                                        contentDescription = "API status",
                                        tint = when (apiKeyValid) {
                                            true -> NithaSuccess
                                            false -> NithaError
                                            null -> NithaOnSurface.copy(alpha = 0.5f)
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NithaPrimary,
                        unfocusedBorderColor = NithaPrimary.copy(alpha = 0.3f),
                        focusedTextColor = NithaOnSurface,
                        unfocusedTextColor = NithaOnSurface
                    )
                )

                Button(
                    onClick = { viewModel.updateApiKey(tempApiKey) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NithaPrimary,
                        contentColor = NithaBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save API Key")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Model Selection
                Text(
                    text = "AI Model",
                    color = NithaOnSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                val models = listOf(
                    Constants.MODEL_DEEPSEEK_CHAT to "DeepSeek Chat (Free)",
                    Constants.MODEL_DEEPSEEK_R1 to "DeepSeek R1 (Free)",
                    Constants.MODEL_QWEN3 to "Qwen 3 (Free)",
                    Constants.MODEL_LLAMA4_SCOUT to "Llama 4 Scout (Free)",
                    Constants.MODEL_LLAMA4_MAVERICK to "Llama 4 Maverick (Free)",
                    Constants.MODEL_GEMMA3 to "Gemma 3 (Free)",
                    Constants.MODEL_MISTRAL_SMALL to "Mistral Small (Free)",
                    Constants.MODEL_OPENROUTER_FREE to "OpenRouter Free"
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = models.find { it.first == profile.selectedModel }?.second ?: profile.selectedModel,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NithaPrimary,
                            unfocusedBorderColor = NithaPrimary.copy(alpha = 0.3f),
                            focusedTextColor = NithaOnSurface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(NithaSurface)
                    ) {
                        models.forEach { (id, name) ->
                            DropdownMenuItem(
                                text = { Text(name, color = NithaOnSurface) },
                                onClick = {
                                    viewModel.updateModel(id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Voice Settings
            SettingsSection(title = "Voice Settings") {
                // Voice Persona
                Text(
                    text = "Voice Persona",
                    color = NithaOnSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PersonaButton(
                        name = "LUNA",
                        description = "Soft",
                        selected = profile.voicePersona == Constants.VOICE_LUNA,
                        onClick = { viewModel.updateVoicePersona(Constants.VOICE_LUNA) }
                    )
                    PersonaButton(
                        name = "MIRA",
                        description = "Smart",
                        selected = profile.voicePersona == Constants.VOICE_MIRA,
                        onClick = { viewModel.updateVoicePersona(Constants.VOICE_MIRA) }
                    )
                    PersonaButton(
                        name = "NOVA",
                        description = "Futuristic",
                        selected = profile.voicePersona == Constants.VOICE_NOVA,
                        onClick = { viewModel.updateVoicePersona(Constants.VOICE_NOVA) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Speech Speed
                Text(
                    text = "Speech Speed: ${String.format("%.1f", profile.speechSpeed)}x",
                    color = NithaOnSurface,
                    fontSize = 14.sp
                )
                Slider(
                    value = profile.speechSpeed,
                    onValueChange = { viewModel.updateSpeechSpeed(it) },
                    valueRange = 0.5f..2.0f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = NithaPrimary,
                        activeTrackColor = NithaPrimary,
                        inactiveTrackColor = NithaPrimary.copy(alpha = 0.3f)
                    )
                )

                // Speech Pitch
                Text(
                    text = "Speech Pitch: ${String.format("%.1f", profile.speechPitch)}",
                    color = NithaOnSurface,
                    fontSize = 14.sp
                )
                Slider(
                    value = profile.speechPitch,
                    onValueChange = { viewModel.updateSpeechPitch(it) },
                    valueRange = 0.5f..2.0f,
                    steps = 14,
                    colors = SliderDefaults.colors(
                        thumbColor = NithaPrimary,
                        activeTrackColor = NithaPrimary,
                        inactiveTrackColor = NithaPrimary.copy(alpha = 0.3f)
                    )
                )

                // Auto Speak Toggle
                SettingsToggle(
                    title = "Auto Speak",
                    description = "Automatically speak AI responses",
                    checked = profile.autoSpeak,
                    onCheckedChange = { viewModel.updateAutoSpeak(it) }
                )

                // Short Mode Toggle
                SettingsToggle(
                    title = "Short Mode",
                    description = "Get brief, concise responses",
                    checked = profile.shortMode,
                    onCheckedChange = { viewModel.updateShortMode(it) }
                )
            }

            // Appearance
            SettingsSection(title = "Appearance") {
                Text(
                    text = "Theme",
                    color = NithaOnSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeButton(
                        name = "NITHA",
                        color = NithaPrimary,
                        selected = profile.theme == Constants.THEME_NITHA_DARK,
                        onClick = { viewModel.updateTheme(Constants.THEME_NITHA_DARK) }
                    )
                    ThemeButton(
                        name = "Cyber",
                        color = CyberPrimary,
                        selected = profile.theme == Constants.THEME_CYBER_PURPLE,
                        onClick = { viewModel.updateTheme(Constants.THEME_CYBER_PURPLE) }
                    )
                    ThemeButton(
                        name = "Matrix",
                        color = MatrixPrimary,
                        selected = profile.theme == Constants.THEME_MATRIX_GREEN,
                        onClick = { viewModel.updateTheme(Constants.THEME_MATRIX_GREEN) }
                    )
                    ThemeButton(
                        name = "Iron",
                        color = IronPrimary,
                        selected = profile.theme == Constants.THEME_IRON_HUD,
                        onClick = { viewModel.updateTheme(Constants.THEME_IRON_HUD) }
                    )
                }
            }

            // User Profile
            SettingsSection(title = "User Profile") {
                OutlinedTextField(
                    value = profile.name,
                    onValueChange = { viewModel.updateUserName(it) },
                    label = { Text("Your Name", color = NithaOnSurface.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NithaPrimary,
                        unfocusedBorderColor = NithaPrimary.copy(alpha = 0.3f),
                        focusedTextColor = NithaOnSurface
                    )
                )

                OutlinedTextField(
                    value = profile.nickname,
                    onValueChange = { viewModel.updateNickname(it) },
                    label = { Text("Nickname (what NITHA calls you)", color = NithaOnSurface.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NithaPrimary,
                        unfocusedBorderColor = NithaPrimary.copy(alpha = 0.3f),
                        focusedTextColor = NithaOnSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = NithaSurface.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                color = NithaPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = NithaOnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                color = NithaOnSurface.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NithaPrimary,
                checkedTrackColor = NithaPrimary.copy(alpha = 0.5f),
                uncheckedThumbColor = NithaOnSurface.copy(alpha = 0.5f),
                uncheckedTrackColor = NithaOnSurface.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun PersonaButton(
    name: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) NithaPrimary.copy(alpha = 0.3f) else NithaSurface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(2.dp, NithaPrimary)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                color = if (selected) NithaPrimary else NithaOnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = NithaOnSurface.copy(alpha = 0.6f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ThemeButton(
    name: String,
    color: androidx.compose.ui.graphics.Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color.copy(alpha = 0.3f) else NithaSurface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(2.dp, color)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(color, RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                color = if (selected) color else NithaOnSurface,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
