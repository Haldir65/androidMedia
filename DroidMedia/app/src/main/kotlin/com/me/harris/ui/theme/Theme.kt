package com.me.harris.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.core.view.WindowCompat
import com.me.harris.composeworkmanager.bluromatic.ui.theme.md_theme_dark_background
import com.me.harris.composeworkmanager.bluromatic.ui.theme.md_theme_dark_primary
import com.me.harris.composeworkmanager.bluromatic.ui.theme.md_theme_dark_secondaryContainer
import com.me.harris.composeworkmanager.bluromatic.ui.theme.md_theme_light_background
import com.me.harris.composeworkmanager.bluromatic.ui.theme.md_theme_light_primary
import com.me.harris.composeworkmanager.bluromatic.ui.theme.md_theme_light_secondaryContainer

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    background = md_theme_dark_background,
)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    secondaryContainer = md_theme_light_secondaryContainer,
    background = md_theme_light_background,
)

@Composable
fun ABluromaticTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Dynamic color in this app is turned off for learning purposes
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
