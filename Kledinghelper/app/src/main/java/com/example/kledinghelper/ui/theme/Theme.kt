package com.example.kledinghelper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = KledingColors.WarmDonker,
    secondary = KledingColors.KoperAccent,
    tertiary = KledingColors.KoperLicht,
    background = KledingColors.WarmCreme,
    surface = KledingColors.Wit
)

private val LightColorScheme = lightColorScheme(
    primary = KledingColors.WarmDonker,
    secondary = KledingColors.KoperAccent,
    tertiary = KledingColors.KoperLicht,
    background = KledingColors.WarmCreme,
    surface = KledingColors.Wit
)

@Composable
fun KledingHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
