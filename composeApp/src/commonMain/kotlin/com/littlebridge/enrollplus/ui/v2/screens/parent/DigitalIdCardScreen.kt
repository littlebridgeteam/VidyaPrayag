package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.littlebridge.enrollplus.feature.idcard.domain.model.IdCardDto
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.QrCodeImage
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.parent.PremiumOverlayHeader
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DigitalIdCardScreen(
    childId: String? = null,
    isTeacher: Boolean = false,
    isStaff: Boolean = false,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: IdCardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var showFront by remember { mutableStateOf(true) }

    LaunchedEffect(childId, isTeacher, isStaff) {
        when {
            isTeacher -> viewModel.loadTeacherIdCard()
            isStaff -> viewModel.loadStaffIdCard()
            childId != null -> viewModel.loadChildIdCard(childId)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PremiumOverlayHeader(title = appString(StringKeys.DID_DIGITAL_ID_CARD), onBack = onBack)

            Spacer(modifier = Modifier.height(24.dp))

        state.error?.let { err ->
            VCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text(text = err, color = VColors.error, style = VTypography.body)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

            val card = state.currentCard
            if (card != null) {
                // OPT-02: Display server-rendered card image via Coil AsyncImage
                // Falls back to hand-drawn DigitalCard composable if URL is null
                card.digitalCardUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = appString(StringKeys.DID_DIGITAL_ID_CARD),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .aspectRatio(54f / 86f),
                    )
                } ?: DigitalCard(card = card, showFront = showFront)

                Spacer(modifier = Modifier.height(16.dp))

                // Only show flip button when using fallback (no server image)
                if (card.digitalCardUrl == null) {
                    VButton(
                        text = if (showFront) appString(StringKeys.DID_SHOW_BACK) else appString(StringKeys.DID_SHOW_FRONT),
                        onClick = { showFront = !showFront },
                        variant = VButtonVariant.Secondary,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = appString(StringKeys.DID_SCAN_QR_BACK),
                    style = VTypography.caption,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = appString(StringKeys.DID_VALID_TILL, "date" to (card.validTill ?: "N/A")),
                    style = VTypography.bodyStrong,
                )
            } else if (state.isLoading) {
                Text(
                    text = appString(StringKeys.DID_LOADING),
                    style = VTypography.body,
                )
            } else if (state.error == null) {
                Text(
                    text = appString(StringKeys.DID_NO_ID_CARD),
                    style = VTypography.body,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DigitalCard(card: IdCardDto, showFront: Boolean) {
    val primaryColor = VColors.violet

    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .size(width = 300.dp, height = 480.dp)
            .drawBehind {
                drawRoundRect(
                    color = primaryColor,
                    cornerRadius = CornerRadius(16f, 16f),
                    style = Stroke(width = 2f),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(20.dp),
        ) {
            if (showFront) {
                Text(
                    text = card.personName,
                    style = VTypography.h3,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = card.personType.replaceFirstChar { it.uppercase() },
                    style = VTypography.body,
                    color = primaryColor,
                )
            } else {
                Text(
                    text = appString(StringKeys.DID_QR_CODE),
                    style = VTypography.bodyStrong,
                )
                Spacer(modifier = Modifier.height(12.dp))
                QrCodeImage(
                    data = card.qrCodeData,
                    size = 160.dp,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = appString(StringKeys.DID_SCAN_VERIFY),
                    style = VTypography.caption,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = appString(StringKeys.DID_VALID_TILL, "date" to (card.validTill ?: "N/A")),
                    style = VTypography.caption,
                )
            }
        }
    }
}
