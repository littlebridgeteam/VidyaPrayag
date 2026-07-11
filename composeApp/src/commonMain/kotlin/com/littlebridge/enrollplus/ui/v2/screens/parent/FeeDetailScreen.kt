package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard

/**
 * FeeDetailScreen — fee detail view reached via deep link `/parent/fees/{id}`.
 * Shows the notification's title, body, and timestamp in a clean card.
 * An "Open in Fees" button navigates to the Fees tab where the full
 * fee list and payment actions live.
 */
@Composable
fun FeeDetailScreen(
    title: String,
    body: String,
    time: String,
    onBack: () -> Unit,
    onOpenInFees: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        PremiumOverlayHeader(title = "Fee Detail", onBack = onBack)

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    VBadge(text = "Fee", tone = VBadgeTone.Warning)
                    Text(
                        text = title,
                        style = VTypography.heading,
                        color = VColors.ink,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = time,
                        style = VTypography.caption,
                        color = VColors.ink2,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = body,
                        style = VTypography.body,
                        color = VColors.ink,
                    )
                }
            }

            VButton(
                text = "Open in Fees",
                variant = VButtonVariant.Outlined,
                tone = VButtonTone.Primary,
                onClick = onOpenInFees,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
