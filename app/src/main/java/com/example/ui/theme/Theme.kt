package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat

private val MimeColorScheme = darkColorScheme(
    primary = MimeGold,
    onPrimary = MimePurpleDark,
    primaryContainer = MimePurpleCard,
    onPrimaryContainer = MimeTextCream,
    secondary = MimeCyan,
    onSecondary = MimePurpleDark,
    tertiary = MimePink,
    background = MimePurpleDark,
    onBackground = MimeTextCream,
    surface = MimePurpleMedium,
    onSurface = MimeTextCream,
    surfaceVariant = MimePurpleCard,
    onSurfaceVariant = MimeTextMuted
)

@Composable
fun MimeMasterTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MimePurpleDark.toArgb()
            window.navigationBarColor = MimePurpleDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    // Force RTL for Persian game experience
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = MimeColorScheme,
            typography = Typography,
            content = content
        )
    }
}
