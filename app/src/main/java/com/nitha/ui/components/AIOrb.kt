package com.nitha.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nitha.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Animated AI Orb - centerpiece of NITHA
 */
@Composable
fun AIOrb(
    status: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")

    // Animation values
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val orbColor = when (status) {
        "Listening..." -> OrbListening
        "Thinking..." -> OrbThinking
        "Speaking..." -> OrbProcessing
        "Error" -> OrbError
        else -> OrbIdle
    }

    Box(
        modifier = modifier
            .size(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.minDimension / 2 * scale

            // Outer glow ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        orbColor.copy(alpha = glowAlpha * 0.5f),
                        orbColor.copy(alpha = glowAlpha * 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = radius * 1.5f
                ),
                radius = radius * 1.5f,
                center = Offset(centerX, centerY)
            )

            // Main orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        orbColor.copy(alpha = 0.9f),
                        orbColor.copy(alpha = 0.5f),
                        orbColor.copy(alpha = 0.2f)
                    ),
                    center = Offset(centerX, centerY),
                    radius = radius
                ),
                radius = radius,
                center = Offset(centerX, centerY)
            )

            // Inner ring
            drawCircle(
                color = orbColor.copy(alpha = 0.6f),
                radius = radius * 0.7f,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )

            // Rotating accent
            val accentX = centerX + kotlin.math.cos(Math.toRadians(rotation.toDouble())).toFloat() * radius * 0.5f
            val accentY = centerY + kotlin.math.sin(Math.toRadians(rotation.toDouble())).toFloat() * radius * 0.5f

            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = 4f,
                center = Offset(accentX, accentY)
            )
        }

        // NITHA text
        Text(
            text = "NITHA",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )
    }
}

/**
 * Voice Wave Animation
 */
@Composable
fun VoiceWave(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    val waveHeight by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        if (!isActive) return@Canvas

        val width = size.width
        val height = size.height
        val centerY = height / 2
        val barCount = 30
        val barWidth = width / barCount

        for (i in 0 until barCount) {
            val x = i * barWidth + barWidth / 2
            val phase = (i.toFloat() / barCount) * Math.PI * 4
            val barHeight = kotlin.math.abs(kotlin.math.sin(phase + System.currentTimeMillis() / 200.0)) * waveHeight + 5

            drawRoundRect(
                color = NithaPrimary.copy(alpha = 0.7f),
                topLeft = Offset(x - 2f, centerY - barHeight.toFloat() / 2),
                size = androidx.compose.ui.geometry.Size(4f, barHeight.toFloat()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
            )
        }
    }
}

/**
 * Dashboard Card Component
 */
@Composable
fun DashboardCard(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = NithaSurface.copy(alpha = 0.8f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = NithaPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = NithaOnSurface.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Quick Action Button
 */
@Composable
fun QuickActionButton(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                containerColor = NithaSurface.copy(alpha = 0.8f),
                contentColor = NithaPrimary
            )
        ) {
            icon()
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = NithaOnSurface.copy(alpha = 0.8f),
            fontSize = 11.sp
        )
    }
}
