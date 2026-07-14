package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.littlebridge.enrollplus.feature.idcard.domain.model.IdCardDto
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardState
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardViewModel
import com.littlebridge.enrollplus.ui.v2.components.ShimmerBox
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VTag
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.util.AppConfig

@Composable
internal fun CardsTab(
    state: IdCardState,
    viewModel: IdCardViewModel,
) {
        var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf<String?>(null) }
    var cardToDelete by remember { mutableStateOf<IdCardDto?>(null) }
    var cardToVerify by remember { mutableStateOf<IdCardDto?>(null) }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    LaunchedEffect(state.pdfUrl) {
        state.pdfUrl?.let { url ->
            uriHandler.openUri(url)
            viewModel.clearPdfUrl()
        }
    }

    val filteredCards = state.cards.filter { card ->
        (filterType == null || card.personType == filterType) &&
        (searchQuery.isBlank() || card.personName.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        state.error?.let { errMsg ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VColors.coral.copy(alpha = 0.1f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = errMsg,
                    style = VTypography.caption.copy(color = VColors.coral, fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(VColors.coral.copy(alpha = 0.15f))
                        .clickable {
                            viewModel.clearMessages()
                            viewModel.loadCards()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("Retry", style = VTypography.caption.copy(color = VColors.coral, fontWeight = FontWeight.Bold, fontSize = 11.sp))
                }
            }
        }

        state.infoMessage?.let { msg ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VColors.mint.copy(alpha = 0.1f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = msg,
                    style = VTypography.caption.copy(color = VColors.mint, fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { viewModel.clearMessages() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Dismiss", tint = VColors.mint, modifier = Modifier.size(14.dp))
                }
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(appString(StringKeys.SCH_SEARCH_BY_NAME)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp),
        ) {
            listOf(null to appString(StringKeys.SCH_ALL), "student" to appString(StringKeys.SCH_STUDENTS), "teacher" to appString(StringKeys.SCH_TEACHERS), "staff" to appString(StringKeys.SCH_STAFF)).forEach { (type, label) ->
                VTag(
                    text = label,
                    active = filterType == type,
                    onClick = { filterType = type },
                    accentActive = true,
                )
            }
        }

        Text(
            text = appString(StringKeys.SCH_CARDS_COUNT, "filtered" to filteredCards.size.toString(), "total" to state.cards.size.toString()),
            style = VTypography.caption.copy(color = VColors.ink3),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (state.cards.isEmpty() && state.isLoading) {
            repeat(4) {
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    height = 120.dp,
                    shape = RoundedCornerShape(12.dp),
                )
            }
        } else if (state.cards.isEmpty() && state.error != null) {
            VEmptyState(
                title = "Failed to load cards",
                body = state.error ?: "Unknown error",
                icon = Icons.Filled.School,
                modifier = Modifier.padding(top = 48.dp),
            )
        } else if (filteredCards.isEmpty()) {
            VEmptyState(
                title = if (searchQuery.isNotBlank()) appString(StringKeys.SCH_NO_CARDS_MATCH, "query" to searchQuery) else appString(StringKeys.SCH_NO_CARDS_YET),
                body = if (searchQuery.isNotBlank()) appString(StringKeys.SCH_TRY_DIFFERENT_SEARCH) else appString(StringKeys.SCH_GO_TO_GENERATE),
                icon = Icons.Filled.School,
                modifier = Modifier.padding(top = 48.dp),
            )
        } else {
            filteredCards.chunked(2).forEachIndexed { i, rowCards ->
                Row(
                    modifier = Modifier.fillMaxWidth().staggeredItemEntrance(i, filteredCards.chunked(2).isNotEmpty()).padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowCards.forEach { card ->
                        CardGridItem(
                            card = card,
                            onDownloadPdf = { viewModel.loadPdfUrl(card.id) },
                            onDelete = { cardToDelete = card },
                            onVerify = { cardToVerify = card },
                            isPdfLoading = state.isPdfLoading,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (rowCards.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    cardToDelete?.let { card ->
        VConfirmDialog(
            visible = true,
            title = appString(StringKeys.SCH_DELETE_ID_CARD),
            message = appString(StringKeys.SCH_DELETE_ID_CARD_CONFIRM, "name" to card.personName),
            confirmLabel = appString(StringKeys.SCH_DELETE),
            onConfirm = {
                viewModel.deleteCard(card.id)
                cardToDelete = null
            },
            onDismiss = { cardToDelete = null },
            icon = Icons.Filled.Close,
        )
    }

    cardToVerify?.let { card ->
        IdCardVerifyDialog(
            card = card,
            onDismiss = { cardToVerify = null },
        )
    }
}

@Composable
private fun CardGridItem(
    card: IdCardDto,
    onDownloadPdf: () -> Unit,
    onDelete: () -> Unit,
    onVerify: () -> Unit,
    isPdfLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
        val status = remember(card.validTill) { cardStatus(card.validTill) }

    VCard(
        modifier = modifier.padding(vertical = 4.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(54f / 86f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(VColors.cream),
                contentAlignment = Alignment.Center,
            ) {
                card.digitalCardUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = card.personName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } ?: run {
                    // Fallback: card-like layout with real QR image from server
                    val base = AppConfig.schoolBaseUrl.trimEnd('/')
                    val qrImgUrl = "$base/api/v1/id-card/${card.id}/qr.png"
                    Column(
                        modifier = Modifier.fillMaxSize().padding(6.dp),
                    ) {
                        // Mini header band
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(VColors.violet),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = appString(StringKeys.SCH_ID_CARD),
                                style = VTypography.caption.copy(color = Color.White).copy(fontSize = 7.sp),
                                modifier = Modifier.padding(horizontal = 4.dp),
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        // Body: photo + info
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Box(
                                    Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(VColors.violet.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(20.dp))
                                }
                                // Real QR code from server endpoint
                                AsyncImage(
                                    model = qrImgUrl,
                                    contentDescription = appString(StringKeys.SCH_QR_CODE),
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color.White),
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text(
                                    text = card.personName,
                                    style = VTypography.caption.copy(color = VColors.ink).copy(fontWeight = FontWeight.Bold),
                                    maxLines = 2,
                                )
                                Text(
                                    text = card.personType.replaceFirstChar { it.uppercase() },
                                    style = VTypography.caption.copy(color = VColors.violet).copy(fontSize = 8.sp),
                                )
                                Text(
                                    text = "#${card.personId.takeLast(8)}",
                                    style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 7.sp),
                                    maxLines = 1,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        // Mini footer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(VColors.violet),
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                ) {
                    VBadge(
                        text = appString(status.labelKey),
                        tone = status.tone,
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD32F2F))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDelete,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = appString(StringKeys.SCH_DELETE),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = card.personName,
                    style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink),
                    maxLines = 1,
                )
                Text(
                    text = card.personType.replaceFirstChar { it.uppercase() },
                    style = VTypography.caption.copy(color = VColors.ink2),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    card.pdfUrl?.let {
                        VButton(
                            text = appString(StringKeys.SCH_PDF),
                            onClick = onDownloadPdf,
                            variant = VButtonVariant.Secondary,
                            size = VButtonSize.Sm,
                            enabled = !isPdfLoading,
                            loading = isPdfLoading,
                        )
                    }
                    VButton(
                        text = appString(StringKeys.SCH_VERIFY),
                        onClick = { onVerify() },
                        variant = VButtonVariant.Secondary,
                        size = VButtonSize.Sm,
                    )
                }
            }
        }
    }
}

@Composable
private fun IdCardVerifyDialog(
    card: IdCardDto,
    onDismiss: () -> Unit,
) {
    val base = AppConfig.schoolBaseUrl.trimEnd('/')
    val qrImgUrl = "$base/api/v1/id-card/${card.id}/qr.png"

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = appString(StringKeys.SCH_ID_CARD),
                style = VTypography.h2.copy(fontSize = 18.sp),
                color = VColors.ink,
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                card.digitalCardUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = card.personName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(54f / 86f)
                            .clip(RoundedCornerShape(12.dp)),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                AsyncImage(
                    model = qrImgUrl,
                    contentDescription = appString(StringKeys.SCH_QR_CODE),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = card.personName,
                    style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = VColors.ink,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                Text(
                    text = card.personType.replaceFirstChar { it.uppercase() },
                    style = VTypography.caption,
                    color = VColors.ink2,
                )
                card.validTill?.let { vt ->
                    Text(
                        text = "Valid till: $vt",
                        style = VTypography.caption,
                        color = VColors.ink3,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Scan QR to verify profile",
                    style = VTypography.caption.copy(fontSize = 11.sp),
                    color = VColors.ink3,
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Close", color = VColors.violet)
            }
        },
    )
}

private data class CardStatus(val labelKey: String, val tone: VBadgeTone)

private fun cardStatus(validTill: String?): CardStatus {
    if (validTill == null) return CardStatus(StringKeys.SCH_NO_EXPIRY, VBadgeTone.Neutral)
    return try {
        val today = com.littlebridge.enrollplus.util.todayIso()
        val cmp = validTill.compareTo(today)
        when {
            cmp < 0 -> CardStatus(StringKeys.SCH_EXPIRED, VBadgeTone.Danger)
            cmp == 0 -> CardStatus(StringKeys.SCH_EXPIRING, VBadgeTone.Warning)
            else -> {
                val parts = validTill.split("-")
                val tParts = today.split("-")
                if (parts.size == 3 && tParts.size == 3) {
                    val expiryApprox = parts[0].toInt() * 365 + parts[1].toInt() * 30 + parts[2].toInt()
                    val todayApprox = tParts[0].toInt() * 365 + tParts[1].toInt() * 30 + tParts[2].toInt()
                    if (expiryApprox - todayApprox < 30) CardStatus(StringKeys.SCH_EXPIRING, VBadgeTone.Warning)
                    else CardStatus(StringKeys.SCH_VALID, VBadgeTone.Success)
                } else {
                    CardStatus(StringKeys.SCH_VALID, VBadgeTone.Success)
                }
            }
        }
    } catch (e: Exception) {
        CardStatus(StringKeys.SCH_VALID, VBadgeTone.Success)
    }
}

