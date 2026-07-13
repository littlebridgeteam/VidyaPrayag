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
import com.littlebridge.enrollplus.util.AppConfig
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

@Composable
internal fun CardsTab(
    state: IdCardState,
    viewModel: IdCardViewModel,
) {
        var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf<String?>(null) }
    var cardToDelete by remember { mutableStateOf<IdCardDto?>(null) }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    val filteredCards = state.cards.filter { card ->
        (filterType == null || card.personType == filterType) &&
        (searchQuery.isBlank() || card.personName.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
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
            style = VTheme.type.caption.copy(color = VTheme.colors.ink3),
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
                            onVerify = { uriHandler.openUri(card.qrCodeData) },
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
}

@Composable
private fun CardGridItem(
    card: IdCardDto,
    onDownloadPdf: () -> Unit,
    onDelete: () -> Unit,
    onVerify: () -> Unit,
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
                    .background(VTheme.colors.cream),
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
                                .background(VTheme.colors.violet),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = appString(StringKeys.SCH_ID_CARD),
                                style = VTheme.type.caption.copy(color = Color.White).copy(fontSize = 7.sp),
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
                                        .background(VTheme.colors.violet.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Filled.Person, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(20.dp))
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
                                    style = VTheme.type.caption.copy(color = VTheme.colors.ink).copy(fontWeight = FontWeight.Bold),
                                    maxLines = 2,
                                )
                                Text(
                                    text = card.personType.replaceFirstChar { it.uppercase() },
                                    style = VTheme.type.caption.copy(color = VTheme.colors.violet).copy(fontSize = 8.sp),
                                )
                                Text(
                                    text = "#${card.personId.takeLast(8)}",
                                    style = VTheme.type.caption.copy(color = VTheme.colors.ink3).copy(fontSize = 7.sp),
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
                                .background(VTheme.colors.violet),
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
                        .background(VTheme.colors.error)
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
                    style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink),
                    maxLines = 1,
                )
                Text(
                    text = card.personType.replaceFirstChar { it.uppercase() },
                    style = VTheme.type.caption.copy(color = VTheme.colors.ink2),
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

