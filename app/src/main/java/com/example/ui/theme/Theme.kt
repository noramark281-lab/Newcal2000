package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClinicColorScheme = lightColorScheme(
    primary = ClinicDarkTeal,
    onPrimary = Color.White,
    primaryContainer = ClinicLightTeal,
    onPrimaryContainer = ClinicDarkTealVariant,
    secondary = ClinicAccentTeal,
    onSecondary = Color.White,
    secondaryContainer = ClinicLightTeal,
    onSecondaryContainer = ClinicDarkTeal,
    tertiary = DentalGold,
    onTertiary = Color.White,
    background = ClinicSkyBlueBottom,
    onBackground = TextPrimaryDark,
    surface = Color.White,
    onSurface = TextPrimaryDark,
    surfaceVariant = GlassCardBg,
    onSurfaceVariant = TextSecondaryDark,
    outline = GlassCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ClinicColorScheme,
        typography = Typography,
        content = content
    )
}
