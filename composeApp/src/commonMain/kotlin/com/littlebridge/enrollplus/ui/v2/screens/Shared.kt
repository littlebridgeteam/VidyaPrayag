package com.littlebridge.enrollplus.ui.v2.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.util.AnalyticsTracker
import kotlinx.coroutines.flow.StateFlow

/**
 * Shared helpers for the `ui/v2` screen layer.
 *
 * Kept tiny and dependency-free so every portal screen can compose from the same vocabulary
 * (state collection, section headers, a portal top-bar) without re-importing the same boilerplate.
 */

/** Terse [StateFlow] collection used across all v2 screens (wraps Compose's [collectAsState]). */
@Composable
fun <T> StateFlow<T>.collectAsStateV2(): State<T> = collectAsState()

/**
 * VLoadingState — centered indeterminate spinner. The Loading leg of every wired screen
 * (LAW 3: every screen has Loading · Error · Empty). Used by [VStateHost].
 */
@Composable
fun VLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxSize().padding(vertical = 64.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(36.dp))
    }
}

/**
 * VErrorState — message + retry affordance. The Error leg of every wired screen (LAW 3).
 */
@Composable
fun VErrorState(
    message: String,
    onRetry: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(message) {
        if (message.isNotBlank()) {
            AnalyticsTracker.event("vp_error_shown", mapOf("error_message" to message.take(200)))
        }
    }
    VEmptyState(
        modifier = modifier,
        icon = VIcons.AlertTriangle,
        title = appString(StringKeys.COMMON_ERROR_GENERIC),
        body = message,
        action = if (onRetry != null) {
            {
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_RETRY),
                    onClick = {
                        AnalyticsTracker.event("vp_retry_tapped", emptyMap())
                        onRetry()
                    },
                    variant = VButtonVariant.Secondary,
                    tone = VButtonTone.Teal,
                    size = VButtonSize.Sm,
                )
            }
        } else null,
    )
}

/**
 * VStateHost — the single contract that gives every wired screen the three required UI states
 * (LAW 3). It inspects a [loading]/[error]/[isEmpty] triple and renders:
 *   • Loading  → [skeleton] shimmer (FEATURE 2) when supplied, else [VLoadingState] spinner
 *   • Error    → [VErrorState] with optional retry
 *   • Empty    → [emptyTitle]/[emptyBody] zero-state via [VEmptyState]
 *   • Content  → [content]
 *
 * Screens collect their VM state, then wrap their happy-path body in this host. This keeps the
 * loading/error/empty handling identical and dependency-free across the whole portal surface.
 *
 * FEATURE 2 — when a screen passes a [skeleton] composable, the loading leg shows that skeleton
 * instead of the spinner, and the loading→content hand-off crossfades (300ms) via [AnimatedContent]
 * so there is no jump-cut. Screens that do not pass a skeleton are unchanged (spinner as before).
 * The crossfade key is `loading || error != null || isEmpty` collapsed to a stable phase so a
 * recomposition that does not change phase never re-triggers the transition (RULE-2: no loop).
 */
@Composable
fun VStateHost(
    loading: Boolean,
    error: String?,
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
    emptyTitle: String = appString(StringKeys.COMMON_EMPTY),
    emptyBody: String? = null,
    emptyIcon: androidx.compose.ui.graphics.vector.ImageVector? = VIcons.FileText,
    onRetry: (() -> Unit)? = null,
    skeleton: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(isEmpty, error) {
        if (isEmpty && error == null && !loading) {
            AnalyticsTracker.event("vp_empty_state_shown", mapOf("empty_title" to emptyTitle))
        }
    }

    // No skeleton supplied → preserve the original behaviour exactly (spinner loading leg).
    if (skeleton == null) {
        when {
            loading -> VLoadingState(modifier)
            error != null -> VErrorState(message = error, onRetry = onRetry, modifier = modifier)
            isEmpty -> VEmptyState(modifier = modifier, icon = emptyIcon, title = emptyTitle, body = emptyBody)
            else -> content()
        }
        return
    }

    // Skeleton supplied → crossfade between the four phases. A stable enum key means only a real
    // phase change drives the 300ms fade; ordinary recompositions do not re-animate.
    val phase = when {
        loading -> VStatePhase.Loading
        error != null -> VStatePhase.Error
        isEmpty -> VStatePhase.Empty
        else -> VStatePhase.Content
    }
    AnimatedContent(
        targetState = phase,
        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
        label = "state-host-crossfade",
        modifier = modifier,
    ) { p ->
        when (p) {
            VStatePhase.Loading -> skeleton()
            VStatePhase.Error -> VErrorState(message = error ?: "", onRetry = onRetry)
            VStatePhase.Empty -> VEmptyState(icon = emptyIcon, title = emptyTitle, body = emptyBody)
            VStatePhase.Content -> content()
        }
    }
}

/** The four mutually-exclusive phases [VStateHost] crossfades between (FEATURE 2). */
private enum class VStatePhase { Loading, Error, Empty, Content }

/** A consistent ALL-CAPS section header + optional trailing action, used inside scroll content. */
@Composable
fun VSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, style = VTypography.label.copy(color = VColors.ink3))
        action?.invoke()
    }
}

/** Portal greeting bar: avatar + name + subtitle. Reused by Parent / Teacher / Admin home tabs. */
@Composable
fun VPortalHeader(
    name: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    photoUrl: String? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        VAvatar(name = name.ifBlank { "?" }, src = photoUrl, ring = true)
        Column(Modifier.weight(1f)) {
            Text(subtitle, style = VTypography.caption.copy(color = VColors.ink3), textAlign = TextAlign.Start)
            Text(name.ifBlank { "—" }, style = VTypography.h3.copy(color = VColors.ink))
        }
        trailing?.invoke()
    }
}
