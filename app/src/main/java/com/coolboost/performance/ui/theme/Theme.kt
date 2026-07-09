package com.coolboost.performance.ui.theme

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

private val DarkColors = darkColorScheme(
    primary = CoolCyan,
    onPrimary = DeepSpace,
    secondary = BoosterOrange,
    onSecondary = DeepSpace,
    background = DeepSpace,
    onBackground = TextPrimaryDark,
    surface = DeepSpaceLighter,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceGlass,
    onSurfaceVariant = TextSecondaryDark,
    error = AlertRed
)

private val LightColors = lightColorScheme(
    primary = CoolCyanDim,
    onPrimary = Color.White,
    secondary = BoosterOrange,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE7ECF3),
    onSurfaceVariant = TextSecondaryLight,
    error = AlertRed
)

@Composable
fun CoolBoostTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CoolBoostTypography,
        shapes = CoolBoostShapes,
        content = content
    )
}
