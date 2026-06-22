package com.nitha.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NithaDarkColorScheme = darkColorScheme(
    primary = NithaPrimary,
    secondary = NithaAccent,
    tertiary = NithaPrimary,
    background = NithaBackground,
    surface = NithaSurface,
    onPrimary = NithaBackground,
    onSecondary = NithaBackground,
    onTertiary = NithaBackground,
    onBackground = NithaOnSurface,
    onSurface = NithaOnSurface,
    error = NithaError
)

private val CyberPurpleColorScheme = darkColorScheme(
    primary = CyberPrimary,
    secondary = CyberAccent,
    tertiary = CyberPrimary,
    background = CyberBackground,
    surface = CyberSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = NithaError
)

private val MatrixGreenColorScheme = darkColorScheme(
    primary = MatrixPrimary,
    secondary = MatrixAccent,
    tertiary = MatrixPrimary,
    background = MatrixBackground,
    surface = MatrixSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = MatrixPrimary,
    onSurface = MatrixPrimary,
    error = NithaError
)

private val IronHudColorScheme = darkColorScheme(
    primary = IronPrimary,
    secondary = IronAccent,
    tertiary = IronPrimary,
    background = IronBackground,
    surface = IronSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = IronPrimary,
    onSurface = IronPrimary,
    error = NithaError
)

@Composable
fun NithaTheme(
    themeName: String = "NITHA_DARK",
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeName == "CYBER_PURPLE" -> CyberPurpleColorScheme
        themeName == "MATRIX_GREEN" -> MatrixGreenColorScheme
        themeName == "IRON_HUD" -> IronHudColorScheme
        else -> NithaDarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
