package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ApitoGold,
    onPrimary = ApitoBackground,
    primaryContainer = ApitoSurfaceElevated,
    onPrimaryContainer = ApitoGold,
    secondary = ApitoCyan,
    onSecondary = ApitoBackground,
    secondaryContainer = ApitoSurfaceVariant,
    onSecondaryContainer = ApitoCyan,
    tertiary = ApitoGreen,
    onTertiary = ApitoBackground,
    background = ApitoBackground,
    onBackground = ApitoTextPrimary,
    surface = ApitoSurface,
    onSurface = ApitoTextPrimary,
    surfaceVariant = ApitoSurfaceVariant,
    onSurfaceVariant = ApitoTextSecondary,
    outline = ApitoBorder,
    error = ApitoRed,
    onError = ApitoTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Mobile-first sleek dark aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ApitoBackground.toArgb()
                window.navigationBarColor = ApitoBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
