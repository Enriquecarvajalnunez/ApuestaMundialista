package com.example.pollafutbolera_android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PollaColorScheme = lightColorScheme(
    primary             = NavyBlue,
    onPrimary           = TextPrimary,
    primaryContainer    = Color(0xFFD6E4F0),
    onPrimaryContainer  = NavyBlue,

    secondary           = LimeGreen,
    onSecondary         = NavyBlue,
    secondaryContainer  = Color(0xFFEEF5C2),
    onSecondaryContainer = NavyBlue,

    background          = BackgroundLight,
    onBackground        = NavyBlue,

    surface             = SurfaceWhite,
    onSurface           = NavyBlue,
    surfaceVariant      = Color(0xFFEEEEEE),
    onSurfaceVariant    = TextSecondary,

    outline             = Color(0xFFCCCCCC),
    outlineVariant      = Color(0xFFE0E0E0),

    error               = Color(0xFFB00020),
    onError             = TextPrimary,
    errorContainer      = Color(0xFFFFDAD6),
    onErrorContainer    = Color(0xFF410002),
)

@Composable
fun PollaFutbolera_AndroidTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PollaColorScheme,
        typography = Typography,
        content = content
    )
}
