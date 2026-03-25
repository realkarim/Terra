package com.realkarim.designsystem.theme

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

private val LightColorScheme = lightColorScheme(
    primary = MidnightBlue40,
    onPrimary = Color.White,
    primaryContainer = MidnightBlue90,
    onPrimaryContainer = MidnightBlue10,
    secondary = BurntOrange40,
    onSecondary = Color.White,
    secondaryContainer = BurntOrange90,
    onSecondaryContainer = BurntOrange10,
    tertiary = ForestGreen40,
    onTertiary = Color.White,
    tertiaryContainer = ForestGreen90,
    onTertiaryContainer = ForestGreen10,
    error = Error40,
    onError = Color.White,
    errorContainer = Error90,
    onErrorContainer = Error10,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,
    scrim = Color.Black,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    inversePrimary = MidnightBlue80,
)

private val DarkColorScheme = darkColorScheme(
    primary = MidnightBlue80,
    onPrimary = MidnightBlue20,
    primaryContainer = MidnightBlue30,
    onPrimaryContainer = MidnightBlue90,
    secondary = BurntOrange80,
    onSecondary = BurntOrange20,
    secondaryContainer = BurntOrange30,
    onSecondaryContainer = BurntOrange90,
    tertiary = ForestGreen80,
    onTertiary = ForestGreen20,
    tertiaryContainer = ForestGreen30,
    onTertiaryContainer = ForestGreen90,
    error = Error80,
    onError = Error10,
    errorContainer = Error30,
    onErrorContainer = Error80,
    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    outline = NeutralVariant60,
    outlineVariant = NeutralVariant30,
    scrim = Color.Black,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral20,
    inversePrimary = MidnightBlue40,
)

@Composable
fun TerraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
