package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.littlebridge.enrollplus.ui.v2.tokens.VColors

/**
 * Premium pull-to-refresh — thin wrapper over Material3's PullToRefreshBox
 * with brand-tinted indicator using M3 Expressive tokens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VPullRefreshPremium(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = state,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = VColors.SurfaceContainerLowest,
                color = VColors.Primary,
            )
        },
    ) {
        Box(Modifier.fillMaxSize()) { content() }
    }
}
