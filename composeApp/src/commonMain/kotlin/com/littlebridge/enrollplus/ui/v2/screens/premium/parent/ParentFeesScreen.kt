package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.FeeViewModel
import com.littlebridge.enrollplus.ui.v2.components.cards.VFeesHeroCard
import com.littlebridge.enrollplus.ui.v2.components.cards.VUpdateCard
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.components.misc.VPullRefreshPremium
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium parent fees — rebuilt with gradient hero, premium loading/error
 * states, pull-to-refresh, VStaggeredItem entrances, and 140dp bottom padding.
 */
@Composable
fun ParentFeesScreen(
    modifier: Modifier = Modifier,
    viewModel: FeeViewModel = koinViewModel(),
    onPayClick: () -> Unit = {},
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()

    VPullRefreshPremium(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.reload() },
        modifier = modifier.fillMaxSize().background(VColors.Surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 140.dp),
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Loading state ──
            if (state.isLoading) {
                VStaggeredItem(delayMs = 0) {
                    SkeletonCard(variant = "hero", modifier = Modifier.padding(horizontal = 20.dp))
                }
                VStaggeredItem(delayMs = 60) {
                    SkeletonCard(variant = "card", modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))
                }
                VStaggeredItem(delayMs = 120) {
                    SkeletonCard(variant = "list", modifier = Modifier.padding(vertical = 8.dp))
                }
                VStaggeredItem(delayMs = 180) {
                    SkeletonCard(variant = "list", modifier = Modifier.padding(vertical = 8.dp))
                }
                return@Column
            }

            // ── Error state ──
            if (state.error != null) {
                ErrorStateCard(
                    message = state.error ?: "Unknown error",
                    onRetry = { viewModel.reload() },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 48.dp),
                )
                return@Column
            }

            // ── Fees Hero Card ──
            VStaggeredItem(delayMs = 0) {
                VFeesHeroCard(
                    label = "Outstanding Balance",
                    amount = state.outstandingFees,
                    dueDate = if (state.overdueCount > 0) "${state.overdueCount} overdue" else "No overdue fees",
                    onPayClick = onPayClick,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Fee Announcements ──
            if (state.announcements.isNotEmpty()) {
                VStaggeredItem(delayMs = 60) {
                    VSectionHeader("Fee Announcements")
                }
                VStaggeredItem(delayMs = 100) {
                    Column(Modifier.padding(horizontal = 20.dp)) {
                        state.announcements.forEach { ann ->
                            val typeColor = when (ann.type) {
                                "Emergency" -> VColors.Error
                                "Payment" -> VColors.Tertiary
                                else -> VColors.Primary
                            }
                            VUpdateCard(
                                source = ann.type,
                                timestamp = ann.time,
                                title = ann.title,
                                text = ann.description,
                                avatarIcon = {
                                    Icon(
                                        Icons.Filled.Info,
                                        contentDescription = null,
                                        tint = typeColor,
                                        modifier = Modifier.size(20.dp),
                                    )
                                },
                                actions = emptyList(),
                                onClick = {},
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // ── Payment History ──
            VStaggeredItem(delayMs = 150) {
                VSectionHeader("Payment History")
            }
            VStaggeredItem(delayMs = 200) {
                Column(Modifier.padding(horizontal = 20.dp)) {
                    EmptyStateCard(
                        title = "No Payment History",
                        body = "Payment records will appear here once fees are paid.",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
