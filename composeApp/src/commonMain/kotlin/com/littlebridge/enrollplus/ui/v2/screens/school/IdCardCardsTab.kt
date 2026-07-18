package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.littlebridge.enrollplus.feature.idcard.domain.model.IdCardDto
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardState
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardViewModel
import com.littlebridge.enrollplus.ui.v2.components.ShimmerBox
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.theme.cardPressScale
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
    val hasData = filteredCards.isNotEmpty()

    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = state.error != null,
            enter = expandVertically(tween(300)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(150)),
        ) {
            state.error?.let { errMsg ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp)).background(VColors.coral.copy(alpha = 0.08f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(errMsg, style = VTypography.caption.copy(color = VColors.coral, fontWeight = FontWeight.SemiBold, fontSize = 12.sp), modifier = Modifier.weight(1f), maxLines = 2)
                    Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(VColors.coral.copy(alpha = 0.12f))
                        .clickable { viewModel.clearMessages(); viewModel.loadCards() }.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("Retry", style = VTypography.caption.copy(color = VColors.coral, fontWeight = FontWeight.Bold, fontSize = 11.sp))
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = state.infoMessage != null,
            enter = expandVertically(tween(300)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(150)),
        ) {
            state.infoMessage?.let { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp)).background(VColors.mint.copy(alpha = 0.08f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(msg, style = VTypography.caption.copy(color = VColors.mint, fontWeight = FontWeight.SemiBold, fontSize = 12.sp), modifier = Modifier.weight(1f), maxLines = 2)
                    Box(modifier = Modifier.clip(RoundedCornerShape(50)).clickable { viewModel.clearMessages() }.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Dismiss", tint = VColors.mint, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        CardsSearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)) {
            val filters = listOf(null to appString(StringKeys.SCH_ALL), "student" to appString(StringKeys.SCH_STUDENTS), "teacher" to appString(StringKeys.SCH_TEACHERS), "staff" to appString(StringKeys.SCH_STAFF))
            filters.forEach { (type, label) ->
                val isActive = filterType == type
                val chipBg by animateColorAsState(targetValue = if (isActive) VColors.violet else VColors.surfaceCard, animationSpec = androidx.compose.animation.core.spring(stiffness = 300f), label = "chipBg")
                val chipText by animateColorAsState(targetValue = if (isActive) Color.White else VColors.ink2, animationSpec = androidx.compose.animation.core.spring(stiffness = 300f), label = "chipText")
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(chipBg).clickable { filterType = type }.padding(horizontal = 14.dp, vertical = 8.dp)) {
                    Text(label, style = VTypography.caption.copy(fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium, fontSize = 12.sp), color = chipText)
                }
            }
        }

        Text(
            text = appString(StringKeys.SCH_CARDS_COUNT, "filtered" to filteredCards.size.toString(), "total" to state.cards.size.toString()),
            style = VTypography.caption.copy(color = VColors.ink3, fontSize = 12.sp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )

        if (state.cards.isEmpty() && state.isLoading) {
            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(6) { ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 200.dp, shape = RoundedCornerShape(14.dp)) }
            }
        } else if (state.cards.isEmpty() && state.error != null) {
            VEmptyState(title = "Failed to load cards", body = state.error ?: "Unknown error", icon = Icons.Filled.School, modifier = Modifier.padding(top = 48.dp))
        } else if (filteredCards.isEmpty()) {
            VEmptyState(
                title = if (searchQuery.isNotBlank()) appString(StringKeys.SCH_NO_CARDS_MATCH, "query" to searchQuery) else appString(StringKeys.SCH_NO_CARDS_YET),
                body = if (searchQuery.isNotBlank()) appString(StringKeys.SCH_TRY_DIFFERENT_SEARCH) else appString(StringKeys.SCH_GO_TO_GENERATE),
                icon = Icons.Filled.School, modifier = Modifier.padding(top = 48.dp),
            )
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(filteredCards) { index, card ->
                    CardGridItem(
                        card = card, onDownloadPdf = { viewModel.loadPdfUrl(card.id) }, onDelete = { cardToDelete = card },
                        onVerify = { cardToVerify = card }, isPdfLoading = state.isPdfLoading,
                        modifier = Modifier.staggeredItemEntrance(index, hasData),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    cardToDelete?.let { card ->
        VConfirmDialog(visible = true, title = appString(StringKeys.SCH_DELETE_ID_CARD), message = appString(StringKeys.SCH_DELETE_ID_CARD_CONFIRM, "name" to card.personName),
            confirmLabel = appString(StringKeys.SCH_DELETE), onConfirm = { viewModel.deleteCard(card.id); cardToDelete = null }, onDismiss = { cardToDelete = null }, icon = Icons.Filled.Close)
    }
    cardToVerify?.let { card -> IdCardVerifyDialog(card = card, onDismiss = { cardToVerify = null }) }
}

@Composable
private fun CardsSearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query, onValueChange = onQueryChange,
        placeholder = { Text(appString(StringKeys.SCH_SEARCH_BY_NAME), style = VTypography.caption.copy(color = VColors.ink3, fontSize = 13.sp)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(18.dp)) },
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotBlank()) {
                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(VColors.line).clickable { onQueryChange("") }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Close, contentDescription = "Clear", tint = VColors.ink3, modifier = Modifier.size(12.dp))
                }
            }
        },
        modifier = modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = VColors.line, focusedBorderColor = VColors.violet, unfocusedContainerColor = VColors.surfaceCard, focusedContainerColor = VColors.surfaceCard, cursorColor = VColors.violet),
    )
}

@Composable
private fun CardGridItem(card: IdCardDto, onDownloadPdf: () -> Unit, onDelete: () -> Unit, onVerify: () -> Unit, isPdfLoading: Boolean = false, modifier: Modifier = Modifier) {
    val status = remember(card.validTill) { cardStatus(card.validTill) }
    val interactionSource = remember { MutableInteractionSource() }

    VCard(modifier = modifier.cardPressScale(interactionSource)) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(54f / 86f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)).background(VColors.cream),
                contentAlignment = Alignment.Center,
            ) {
                card.digitalCardUrl?.let { url ->
                    AsyncImage(model = url, contentDescription = card.personName, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } ?: run {
                    val base = AppConfig.schoolBaseUrl.trimEnd('/')
                    val qrImgUrl = "$base/api/v1/id-card/${card.id}/qr.png"
                    Column(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                        Box(modifier = Modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(4.dp)).background(VColors.violet), contentAlignment = Alignment.CenterStart) {
                            Text(appString(StringKeys.SCH_ID_CARD), style = VTypography.caption.copy(color = Color.White, fontSize = 7.sp), modifier = Modifier.padding(horizontal = 4.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(Modifier.size(32.dp).clip(RoundedCornerShape(4.dp)).background(VColors.violet.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(20.dp))
                                }
                                AsyncImage(model = qrImgUrl, contentDescription = appString(StringKeys.SCH_QR_CODE), contentScale = ContentScale.Fit, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(2.dp)).background(Color.White))
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(card.personName, style = VTypography.caption.copy(color = VColors.ink, fontWeight = FontWeight.Bold), maxLines = 2)
                                Text(card.personType.replaceFirstChar { it.uppercase() }, style = VTypography.caption.copy(color = VColors.violet, fontSize = 8.sp))
                                Text("#${card.personId.takeLast(8)}", style = VTypography.caption.copy(color = VColors.ink3, fontSize = 7.sp), maxLines = 1)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(4.dp)).background(VColors.violet))
                    }
                }
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)) { VBadge(text = appString(status.labelKey), tone = status.tone) }
                Box(modifier = Modifier.align(Alignment.TopStart).padding(6.dp).size(26.dp).clip(CircleShape)
                    .background(Color(0xFFD32F2F).copy(alpha = 0.9f))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDelete),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Close, contentDescription = appString(StringKeys.SCH_DELETE), tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(card.personName, style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VColors.ink), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(2.dp))
                Text(card.personType.replaceFirstChar { it.uppercase() }, style = VTypography.caption.copy(color = VColors.ink2, fontSize = 11.sp))
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    card.pdfUrl?.let { VButton(text = appString(StringKeys.SCH_PDF), onClick = onDownloadPdf, variant = VButtonVariant.Secondary, size = VButtonSize.Sm, enabled = !isPdfLoading, loading = isPdfLoading) }
                    VButton(text = appString(StringKeys.SCH_VERIFY), onClick = { onVerify() }, variant = VButtonVariant.Secondary, size = VButtonSize.Sm)
                }
            }
        }
    }
}

@Composable
private fun IdCardVerifyDialog(card: IdCardDto, onDismiss: () -> Unit) {
    val base = AppConfig.schoolBaseUrl.trimEnd('/')
    val qrImgUrl = "$base/api/v1/id-card/${card.id}/qr.png"

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss, shape = RoundedCornerShape(20.dp), containerColor = Color.White, title = null,
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(VColors.violet, VColors.violet.copy(alpha = 0.7f))))
                    .padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text(appString(StringKeys.SCH_ID_CARD), style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp), color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                card.digitalCardUrl?.let { url ->
                    AsyncImage(model = url, contentDescription = card.personName, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().aspectRatio(54f / 86f).clip(RoundedCornerShape(14.dp)))
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Box(modifier = Modifier.size(130.dp).clip(RoundedCornerShape(12.dp)).border(1.5.dp, VColors.line, RoundedCornerShape(12.dp)).background(Color.White).padding(8.dp), contentAlignment = Alignment.Center) {
                    AsyncImage(model = qrImgUrl, contentDescription = appString(StringKeys.SCH_QR_CODE), contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(card.personName, style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp), color = VColors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(card.personType.replaceFirstChar { it.uppercase() }, style = VTypography.caption.copy(color = VColors.violet, fontWeight = FontWeight.SemiBold, fontSize = 12.sp))
                card.validTill?.let { vt -> Spacer(modifier = Modifier.height(4.dp)); Text("Valid till: $vt", style = VTypography.caption.copy(color = VColors.ink3, fontSize = 11.sp)) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Scan QR to verify profile", style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium), color = VColors.ink3)
            }
        },
        confirmButton = { androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Close", color = VColors.violet, fontWeight = FontWeight.SemiBold) } },
    )
}

private data class CardStatus(val labelKey: String, val tone: com.littlebridge.enrollplus.ui.v2.components.VBadgeTone)

private fun cardStatus(validTill: String?): CardStatus {
    if (validTill == null) return CardStatus(StringKeys.SCH_NO_EXPIRY, com.littlebridge.enrollplus.ui.v2.components.VBadgeTone.Neutral)
    return try {
        val today = com.littlebridge.enrollplus.util.todayIso()
        val cmp = validTill.compareTo(today)
        when {
            cmp < 0 -> CardStatus(StringKeys.SCH_EXPIRED, com.littlebridge.enrollplus.ui.v2.components.VBadgeTone.Danger)
            cmp == 0 -> CardStatus(StringKeys.SCH_EXPIRING, com.littlebridge.enrollplus.ui.v2.components.VBadgeTone.Warning)
            else -> {
                val parts = validTill.split("-")
                val tParts = today.split("-")
                if (parts.size == 3 && tParts.size == 3) {
                    val expiryApprox = parts[0].toInt() * 365 + parts[1].toInt() * 30 + parts[2].toInt()
                    val todayApprox = tParts[0].toInt() * 365 + tParts[1].toInt() * 30 + tParts[2].toInt()
                    if (expiryApprox - todayApprox < 30) CardStatus(StringKeys.SCH_EXPIRING, com.littlebridge.enrollplus.ui.v2.components.VBadgeTone.Warning)
                    else CardStatus(StringKeys.SCH_VALID, com.littlebridge.enrollplus.ui.v2.components.VBadgeTone.Success)
                } else CardStatus(StringKeys.SCH_VALID, com.littlebridge.enrollplus.ui.v2.components.VBadgeTone.Success)
            }
        }
    } catch (_: Exception) { CardStatus(StringKeys.SCH_VALID, com.littlebridge.enrollplus.ui.v2.components.VBadgeTone.Success) }
}
