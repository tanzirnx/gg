package com.nitha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nitha.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsScreen(navController: NavController) {
    val skills = listOf(
        SkillItem("App Control", Icons.Default.Apps, "Open and control apps"),
        SkillItem("File Manager", Icons.Default.Folder, "Manage files and folders"),
        SkillItem("Notifications", Icons.Default.Notifications, "Read and manage notifications"),
        SkillItem("Web Search", Icons.Default.Search, "Search the internet"),
        SkillItem("Vision", Icons.Default.CameraAlt, "OCR and image analysis"),
        SkillItem("Notes", Icons.Default.Note, "Create and manage notes"),
        SkillItem("Reminders", Icons.Default.Alarm, "Set reminders and alarms"),
        SkillItem("Media", Icons.Default.MusicNote, "Control media playback"),
        SkillItem("Automation", Icons.Default.AutoFixHigh, "Create custom routines"),
        SkillItem("Translation", Icons.Default.Translate, "Translate languages"),
        SkillItem("Weather", Icons.Default.WbSunny, "Get weather updates"),
        SkillItem("Calculator", Icons.Default.Calculate, "Quick calculations"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Skills",
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
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(skills.size) { index ->
                SkillCard(skill = skills[index])
            }
        }
    }
}

data class SkillItem(
    val name: String,
    val icon: ImageVector,
    val description: String
)

@Composable
private fun SkillCard(skill: SkillItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = NithaSurface.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = skill.icon,
                contentDescription = skill.name,
                tint = NithaPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = skill.name,
                color = NithaOnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = skill.description,
                color = NithaOnSurface.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
        }
    }
}
