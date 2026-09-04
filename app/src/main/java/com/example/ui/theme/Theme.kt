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
    primary = TerracottaSecondary,
    onPrimary = Color(0xFF3E1200),
    primaryContainer = Color(0xFF5F2305),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = AmberAccent,
    onSecondary = Color(0xFF412D00),
    secondaryContainer = Color(0xFF5E4200),
    onSecondaryContainer = Color(0xFFFFDEA3),
    tertiary = OliveGreen,
    onTertiary = Color(0xFF00390A),
    tertiaryContainer = Color(0xFF005313),
    onTertiaryContainer = Color(0xFF78DB7C),
    background = DarkBgLevel0,
    onBackground = TextPrimaryDark,
    surface = DarkSurfaceLevel1,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceLevel2,
    onSurfaceVariant = TextSecondaryDark,
    surfaceContainerHighest = DarkSurfaceLevel3,
    outline = Color(0xFF5C4133),
    outlineVariant = Color(0xFF3D2A20)
)

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = Color(0xFF3B0900),
    secondary = Color(0xFFE65100),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFF4E2600),
    tertiary = Color(0xFF2E7D32),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF002204),
    background = LightBgLevel0,
    onBackground = TextPrimaryLight,
    surface = LightSurfaceLevel1,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceLevel2,
    onSurfaceVariant = TextSecondaryLight,
    surfaceContainerHighest = LightSurfaceLevel3,
    outline = Color(0xFFD4C2B7),
    outlineVariant = Color(0xFFE8DCD5)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our rich bespoke culinary palette for striking UI
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
        content = content
    )
}
