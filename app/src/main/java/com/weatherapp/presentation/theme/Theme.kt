package com.weatherapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryLight, onPrimary = Color.Black,

    primaryContainer = TealPrimaryDark, onPrimaryContainer = Color.White,

    secondary = TealSecondary, onSecondary = Color.Black,

    secondaryContainer = TealSecondaryDark, onSecondaryContainer = Color.White,

    tertiary = TealAccent, onTertiary = Color.Black,

    tertiaryContainer = TealAccentDark, onTertiaryContainer = Color.White,

    background = DarkBackground, onBackground = TextPrimary,

    surface = SurfaceDark, onSurface = TextPrimary,

    surfaceVariant = SurfaceDarkVariant, onSurfaceVariant = TextSecondary,

    surfaceTint = TealPrimary,

    inverseSurface = SurfaceLight, inverseOnSurface = DarkTextPrimary,

    error = Error, onError = Color.White,

    errorContainer = Error.copy(alpha = 0.20f), onErrorContainer = Color.White,

    outline = DividerDark, outlineVariant = DividerDark.copy(alpha = 0.5f),

    scrim = OverlayDark

)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealContainer,
    onPrimaryContainer = OnTealContainer,

    secondary = TealSecondary,
    onSecondary = Color.White,

    secondaryContainer = TealSecondaryLight,
    onSecondaryContainer = DarkTextPrimary,

    tertiary = TealAccent,
    onTertiary = Color.White,

    tertiaryContainer = TealAccentLight,
    onTertiaryContainer = DarkTextPrimary,

    background = LightBackground,
    onBackground = DarkTextPrimary,

    surface = SurfaceLight,
    onSurface = DarkTextPrimary,

    surfaceVariant = SurfaceLightVariant,
    onSurfaceVariant = DarkTextSecondary,

    surfaceTint = TealPrimary,

    inverseSurface = SurfaceDark,
    inverseOnSurface = TextPrimary,

    error = Error,
    onError = Color.White,

    errorContainer = Error.copy(alpha = 0.12f),
    onErrorContainer = Error,

    outline = DividerLight,
    outlineVariant = DividerLight.copy(alpha = 0.5f),

    scrim = OverlayLight

)


@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to enforce our custom teal and glassomorphism theme
    dynamicColor: Boolean = false, content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as androidx.activity.ComponentActivity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            window.statusBarColor =
                if (darkTheme) DarkBackground.toArgb() else LightBackground.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography(), content = content
    )
}

// Extension to help with SideEffect color conversion
private fun Color.toArgb(): Int {
    return (this.value shr 32).toInt()
}
