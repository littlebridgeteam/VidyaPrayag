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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.FeeViewModel
import com.littlebridge.enrollplus.ui.v2.components.cards.VFeesHeroCard
import com.littlebridge.enrollplus.ui.v2.components.cards.VUpdateCard
import com.littlebridge.enrollplus.ui.v2.components.cards.UpdateAction
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
 * Premium parent fees — matches parent-portal.html Fees tab.
 * VFeesHeroCard with Pay Now, fee announcements, and payment history.
 */
@Composable
fun ParentFeesScreen(
    modifier: Modifier = Modifier,
    viewModel: FeeViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            Box(
                Modifier.fillMaxWidth().height(200.dp).clip(VShapes.Lg).background(VColors.SurfaceContainerLow),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading fees...", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
            return@PremiumTheme
        }

        if (state.error != null) {
            Box(
                Modifier.fillMaxWidth().height(200.dp).clip(VShapes.Lg).background(VColors.ErrorContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.error!!, style = VTypography.UpdateText.copy(color = VColors.OnErrorContainer))
            }
            return@PremiumTheme
        }

        // ── Fees Hero Card ──
        VFeesHeroCard(
            label = "Outstanding Balance",
            amount = state.outstandingFees,
            dueDate = if (state.overdueCount > 0) "${state.overdueCount} overdue" else "No overdue fees",
            onPayClick = { },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(24.dp))

        // ── Fee Announcements ──
        if (state.announcements.isNotEmpty()) {
            VSectionHeader("Fee Announcements", linkText = "All", onLinkClick = { })
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
                        actions = listOf(
                            UpdateAction("View Invoice", isPrimary = true, onClick = { }),
                            UpdateAction("Download", isPrimary = false, onClick = { }),
                        ),
                        onClick = { },
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // ── Payment History ──
        VSectionHeader("Payment History")
        Column(Modifier.padding(horizontal = 20.dp)) {
            // Placeholder payment items — in production these would come from the API
            PaymentItem("Q3 Tuition Fee", "Paid Dec 10, 2025 · Receipt #R4521", "₹12,500")
            Spacer(Modifier.height(10.dp))
            PaymentItem("Q2 Tuition Fee", "Paid Sep 8, 2025 · Receipt #R3892", "₹12,500")
            Spacer(Modifier.height(10.dp))
            PaymentItem("Q1 Tuition Fee", "Paid Jun 12, 2025 · Receipt #R3105", "₹12,500")
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PaymentItem(title: String, date: String, amount: String) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null) { }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            Modifier.size(44.dp).clip(VShapes.Md).background(VColors.TertiaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VColors.Tertiary, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = VTypography.PayTitle.copy(color = VColors.OnSurface))
            Spacer(Modifier.height(2.dp))
            Text(date, style = VTypography.PayDate.copy(color = VColors.OnSurfaceVariant))
        }
        Text(amount, style = VTypography.PayAmount.copy(color = VColors.OnSurface))
    }
}
