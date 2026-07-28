package es.davidnavarro.androidcleanarchitecture.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PortalGreenDark,
    onPrimary = Color(0xFF173800),
    primaryContainer = PortalGreenContainerDark,
    onPrimaryContainer = PortalGreenLight,
    secondary = PortalGreenDark,
    onSecondary = Color(0xFF173800),
    secondaryContainer = PortalGreenContainerDark,
    onSecondaryContainer = PortalGreenLight,
    background = NightBackground,
    onBackground = NightInk,
    surface = NightSurface,
    onSurface = NightInk,
    onSurfaceVariant = NightInkVariant,
    surfaceContainer = NightSurface,
    surfaceContainerHigh = NightSurfaceHigh,
    outline = NightOutlineStrong,
    outlineVariant = NightOutline
)

private val LightColorScheme = lightColorScheme(
    primary = PortalGreen,
    onPrimary = WarmSurface,
    primaryContainer = PortalGreenLight,
    onPrimaryContainer = Color(0xFF183800),
    secondary = PortalGreen,
    onSecondary = WarmSurface,
    secondaryContainer = PortalGreenLight,
    onSecondaryContainer = Color(0xFF183800),
    background = WarmBackground,
    onBackground = Ink,
    surface = WarmSurface,
    onSurface = Ink,
    onSurfaceVariant = InkVariant,
    surfaceContainer = WarmSurface,
    surfaceContainerLow = WarmBackground,
    surfaceContainerHigh = WarmSurfaceHigh,
    outline = WarmOutlineStrong,
    outlineVariant = WarmOutline
)

@Composable
fun AndroidCleanArchitectureTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
