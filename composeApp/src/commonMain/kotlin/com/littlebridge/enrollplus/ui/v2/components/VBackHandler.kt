package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.runtime.Composable
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState

/**
 * Drop-in replacement for the deprecated [androidx.compose.ui.backhandler.BackHandler].
 * Uses the new Navigation Event API under the hood.
 *
 * Call unconditionally — the [enabled] parameter controls whether the handler is active.
 */
@Composable
fun VBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
) {
    val navState = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationBackHandler(
        state = navState,
        isBackEnabled = enabled,
        onBackCompleted = onBack,
    )
}
