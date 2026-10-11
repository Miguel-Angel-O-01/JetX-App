package com.example.jetx.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = JetXAccentGold,
    background = JetXDarkBackground,
    surface = JetXGlassSurface,
    error = JetXErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = JetXAccentGold,
    background = JetXDarkBackground,
    surface = JetXGlassSurface,
    error = JetXErrorRed
)

@Composable
fun JetXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
