package com.littlebridge.enrollplus.ui.v2.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Android implementation — calls `enableEdgeToEdge` with light or dark
 * `SystemBarStyle` based on the active theme. Uses `DisposableEffect` so the
 * bars are re-applied whenever the theme changes.
 */
@Composable
actual fun VStatusBarAdapter(isDark: Boolean) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity ?: return

    DisposableEffect(isDark) {
        // BUG-021: Use the app's cream background (0xFFFBF8F4) as the status bar scrim
        // instead of TRANSPARENT — the window background is white by default, so a
        // transparent scrim made system icons invisible against the white area.
        val creamScrim = 0xFFFBF8F4.toInt()
        activity.enableEdgeToEdge(
            statusBarStyle = if (isDark) {
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(
                    creamScrim,
                    android.graphics.Color.TRANSPARENT,
                )
            },
            navigationBarStyle = if (isDark) {
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(
                    creamScrim,
                    android.graphics.Color.TRANSPARENT,
                )
            },
        )
        onDispose { }
    }
}
