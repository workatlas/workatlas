package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = AtlasDarkNavy,
    primaryContainer = AtlasNavyBorder,
    onPrimaryContainer = MintAccent,
    secondary = EmeraldLight,
    onSecondary = AtlasDarkNavy,
    secondaryContainer = AtlasNavySurface,
    onSecondaryContainer = EmeraldPrimary,
    tertiary = CyanNeon,
    onTertiary = AtlasDarkNavy,
    background = AtlasDarkNavy,
    onBackground = TextPrimary,
    surface = AtlasNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = AtlasNavy,
    onSurfaceVariant = TextSecondary,
    outline = AtlasNavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF065F46),
    secondary = AtlasNavy,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = AtlasDarkNavy,
    tertiary = CyanNeon,
    onTertiary = AtlasDarkNavy,
    background = LightSurface,
    onBackground = LightTextPrimary,
    surface = LightCard,
    onSurface = LightTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // WorkAtlas uses an intentional, signature navy & emerald aesthetic
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // default dark for signature immersive tech vibe

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
