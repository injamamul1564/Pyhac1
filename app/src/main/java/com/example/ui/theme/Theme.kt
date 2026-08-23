package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ElegantDarkColorScheme =
  darkColorScheme(
    primary = CyberGreen,
    onPrimary = Color(0xFF0B0F19),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = CyberCyan,
    onSecondary = Color(0xFF0B0F19),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFA5F3FC),
    tertiary = CyberIndigo,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF312E81),
    onTertiaryContainer = Color(0xFFC7D2FE),
    error = CyberRed,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorderLight
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = ElegantDarkColorScheme,
    typography = Typography,
    content = content
  )
}


