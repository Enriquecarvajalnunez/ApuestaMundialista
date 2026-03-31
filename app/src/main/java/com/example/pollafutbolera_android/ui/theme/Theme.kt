package com.example.pollafutbolera_android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PollaColorScheme = darkColorScheme(
    primary            = MintGreen,
    onPrimary          = GreenDark,
    primaryContainer   = GreenLight,
    onPrimaryContainer = OnDarkText,

    secondary          = GoldBright,
    onSecondary        = GreenDark,
    secondaryContainer = GoldDeep,
    onSecondaryContainer = GreenDark,

    tertiary           = MintGreenLight,
    onTertiary         = GreenDark,

    background         = GreenDark,
    onBackground       = OnDarkText,

    surface            = SurfaceGlass,
    onSurface          = OnDarkText,
    surfaceVariant     = SurfaceGlassDark,
    onSurfaceVariant   = OnDarkVariant,

    outline            = GoldDeep,
    outlineVariant     = GreenLight,
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
