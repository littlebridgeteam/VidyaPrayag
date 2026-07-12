/*
 * File: ParentEventRegistrationScreenV2.kt
 * Module: ui.v2.screens.parent
 *
 * Parent Event Registration screen — browse events, view detail with slots,
 * register, cancel, reschedule, and view my registrations.
 * Wired to ParentEventRegistrationViewModel.
 */
package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.event.domain.model.EventSlotDto
import com.littlebridge.enrollplus.feature.event.domain.model.ParentEventDto
import com.littlebridge.enrollplus.feature.event.domain.model.RegistrationDto
import com.littlebridge.enrollplus.feature.event.presentation.ParentEventRegistrationViewModel
import androidx.compose.material3.Icon
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.parent.PremiumOverlayHeader
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentEventRegistrationScreenV2(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentEventRegistrationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var selectedEventId by remember { mutableStateOf<String?>(null) }
    var showMyRegistrations by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding(),
    ) {
        PremiumOverlayHeader(
            title = if (selectedEventId != null) "Event Detail" else "Events",
            onBack = {
                if (selectedEventId != null) {
                    selectedEventId = null
                    viewModel.clearMessages()
                } else {
                    onBack()
                }
            },
        )

        if (state.infoMessage != null) {
            Text(
                text = state.infoMessage!!,
                style = VTypography.caption,
                color = VColors.success,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
            )
        }
        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                style = VTypography.caption,
                color = VColors.error,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
            )
        }

        if (selectedEventId != null && state.eventDetail != null) {
            EventDetailContent(
                eventDetail = state.eventDetail!!,
                isLoading = state.isRegistering || state.isCancelling || state.isRescheduling,
                onRegister = { slotId, attendeeCount ->
                    viewModel.register(
                        eventId = selectedEventId!!,
                        slotId = slotId,
                        studentId = null,
                        attendeeCount = attendeeCount,
                    )
                },
                onCancel = {
                    viewModel.cancelRegistration(selectedEventId!!)
                },
                onReschedule = { newSlotId ->
                    viewModel.reschedule(selectedEventId!!, newSlotId)
                },
            )
        } else if (showMyRegistrations) {
            MyRegistrationsContent(
                registrations = state.myRegistrations,
                isLoading = state.isLoading,
                onBackToList = {
                    showMyRegistrations = false
                    viewModel.loadEvents()
                },
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SegmentChip(
                    text = "Upcoming Events",
                    isSelected = !showMyRegistrations,
                    onClick = {
                        showMyRegistrations = false
                        viewModel.loadEvents()
                    },
                    modifier = Modifier.weight(1f),
                )
                SegmentChip(
                    text = "My Registrations",
                    isSelected = showMyRegistrations,
                    onClick = {
                        showMyRegistrations = true
                        viewModel.loadMyRegistrations()
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            VStateHost(
                loading = state.isLoading,
                error = state.errorMessage,
                isEmpty = state.events.isEmpty() && !state.isLoading,
                emptyTitle = "No upcoming events with registration",
                onRetry = { viewModel.loadEvents() },
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.events) { event ->
                        EventCard(
                            event = event,
                            onClick = {
                                selectedEventId = event.id
                                viewModel.loadEventDetail(event.id)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventDetailContent(
    eventDetail: com.littlebridge.enrollplus.feature.event.domain.model.ParentEventDetailResponse,
    isLoading: Boolean,
    onRegister: (String?, Int) -> Unit,
    onCancel: () -> Unit,
    onReschedule: (String) -> Unit,
) {
    val event = eventDetail.event
    var selectedSlotId by remember { mutableStateOf<String?>(null) }
    var attendeeCount by remember { mutableStateOf("1") }
    var showCancelDialog by remember { mutableStateOf(false) }

    VConfirmDialog(
        visible = showCancelDialog,
        title = appString(StringKeys.PE_CANCEL_REGISTRATION),
        message = appString(StringKeys.PE_CANCEL_REGISTRATION_MSG, "title" to event.title),
        confirmLabel = appString(StringKeys.PE_YES_CANCEL),
        cancelLabel = appString(StringKeys.PL_KEEP),
        onConfirm = {
            showCancelDialog = false
            onCancel()
        },
        onDismiss = { showCancelDialog = false },
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(VShapes.xl)
                    .background(VColors.surfaceCard)
                    .border(1.dp, VColors.line, VShapes.xl)
                    .padding(20.dp),
            ) {
                Text(event.title, style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp), color = VColors.ink)
                Spacer(Modifier.height(4.dp))
                Text(event.startDate, style = VTypography.caption, color = VColors.ink2)
                if (event.venue != null) {
                    Text("Venue: ${event.venue}", style = VTypography.caption, color = VColors.ink3)
                }
                if (event.description.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(event.description, style = VTypography.caption, color = VColors.ink2, maxLines = 4, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.height(10.dp))
                if (event.registrationDeadline != null) {
                    Text("Register by: ${event.registrationDeadline}", style = VTypography.caption, color = VColors.ink3)
                }
                Spacer(Modifier.height(6.dp))
                if (event.myRegistrationStatus != null) {
                    PremiumBadge(text = "Registered: ${event.myRegistrationStatus}", bg = VColors.successSoft, fg = VColors.success)
                } else if (event.registrationEnabled || event.type == "PTM") {
                    PremiumBadge(text = "Registration open", bg = VColors.violetSoft, fg = VColors.violet)
                }
                if (event.conflictingEventTitle != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "⚠ Conflicts with: ${event.conflictingEventTitle}",
                        style = VTypography.caption,
                        color = VColors.gold,
                    )
                }
            }
        }

        if (eventDetail.slots.isNotEmpty()) {
            item {
                Text(
                    text = "Select a time slot",
                    style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                    color = VColors.ink,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
            items(eventDetail.slots) { slot ->
                SlotCard(
                    slot = slot,
                    isSelected = selectedSlotId == slot.id,
                    onClick = { selectedSlotId = slot.id },
                )
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            if (event.myRegistrationStatus != null) {
                if (eventDetail.slots.isNotEmpty()) {
                    PremiumButton(
                        text = "Reschedule",
                        onClick = { selectedSlotId?.let { onReschedule(it) } },
                        enabled = selectedSlotId != null && !isLoading,
                        isLoading = isLoading,
                        isPrimary = false,
                    )
                }
                Spacer(Modifier.height(8.dp))
                PremiumButton(
                    text = "Cancel Registration",
                    onClick = { showCancelDialog = true },
                    enabled = !isLoading,
                    isLoading = isLoading,
                    isPrimary = false,
                    isDanger = true,
                )
            } else {
                if (eventDetail.slots.isEmpty()) {
                    PremiumInput(
                        value = attendeeCount,
                        onValueChange = { attendeeCount = it },
                        label = "Number of attendees",
                        placeholder = "1",
                        keyboardType = KeyboardType.Number,
                    )
                    Spacer(Modifier.height(8.dp))
                }
                val canRegister = if (eventDetail.slots.isNotEmpty()) {
                    selectedSlotId != null && !isLoading
                } else {
                    !isLoading
                }
                PremiumButton(
                    text = "Register",
                    onClick = { onRegister(selectedSlotId, attendeeCount.toIntOrNull()?.coerceAtLeast(1) ?: 1) },
                    enabled = canRegister,
                    isLoading = isLoading,
                    isPrimary = true,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SlotCard(
    slot: EventSlotDto,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(if (isSelected) VColors.violetSoft else VColors.surfaceCard)
            .border(1.dp, if (isSelected) VColors.violet else VColors.line, VShapes.lg)
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "${slot.startTime} - ${slot.endTime}",
                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
            )
            Text(
                text = "${slot.bookedCount}/${slot.capacity} booked",
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }
        if (slot.isFull) {
            PremiumBadge(text = "Full", bg = VColors.errorSoft, fg = VColors.error)
        } else if (slot.myRegistration) {
            PremiumBadge(text = "Your slot", bg = VColors.successSoft, fg = VColors.success)
        } else if (isSelected) {
            PremiumBadge(text = "Selected", bg = VColors.violetSoft, fg = VColors.violet)
        }
    }
}

@Composable
private fun MyRegistrationsContent(
    registrations: List<RegistrationDto>,
    isLoading: Boolean,
    onBackToList: () -> Unit,
) {
    VStateHost(
        loading = isLoading,
        error = null,
        isEmpty = registrations.isEmpty() && !isLoading,
        emptyTitle = "No registrations yet",
        onRetry = onBackToList,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(registrations) { reg ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(VShapes.lg)
                        .background(VColors.surfaceCard)
                        .border(1.dp, VColors.line, VShapes.lg)
                        .padding(20.dp),
                ) {
                    Text(reg.eventTitle, style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp), color = VColors.ink)
                    Spacer(Modifier.height(4.dp))
                    Text(reg.eventDate, style = VTypography.caption, color = VColors.ink2)
                    if (reg.slotStartTime != null) {
                        Text(
                            text = "Slot: ${reg.slotStartTime} - ${reg.slotEndTime}",
                            style = VTypography.caption,
                            color = VColors.ink3,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    val (bg, fg) = when (reg.status) {
                        "REGISTERED" -> VColors.successSoft to VColors.success
                        "CHECKED_IN" -> VColors.violetSoft to VColors.violet
                        "CANCELLED" -> VColors.errorSoft to VColors.error
                        else -> VColors.creamDeep to VColors.ink2
                    }
                    PremiumBadge(text = reg.status, bg = bg, fg = fg)
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    event: ParentEventDto,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            Modifier.size(44.dp)
                .clip(VShapes.md)
                .background(VColors.violetSoft),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                VIcons.Calendar,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(event.title, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
            Spacer(Modifier.height(2.dp))
            Text(event.startDate, style = VTypography.caption, color = VColors.ink2)
            if (event.venue != null) {
                Text("Venue: ${event.venue}", style = VTypography.caption, color = VColors.ink3)
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (event.myRegistrationStatus != null) {
                    PremiumBadge(text = event.myRegistrationStatus!!, bg = VColors.successSoft, fg = VColors.success)
                } else if (event.registrationEnabled || event.type == "PTM") {
                    PremiumBadge(text = "Registration open", bg = VColors.violetSoft, fg = VColors.violet)
                }
            }
        }
        Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun PremiumBadge(text: String, bg: Color, fg: Color) {
    Box(
        Modifier
            .clip(VShapes.full)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text,
            style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
            color = fg,
        )
    }
}

@Composable
private fun SegmentChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(VShapes.lg)
            .background(if (isSelected) VColors.violet else VColors.surfaceCard)
            .border(1.dp, if (isSelected) VColors.violet else VColors.line, VShapes.lg)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = if (isSelected) VColors.white else VColors.ink2,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isPrimary: Boolean = true,
    isDanger: Boolean = false,
) {
    val bg = when {
        isDanger -> VColors.error
        isPrimary -> VColors.violet
        else -> VColors.surfaceCard
    }
    val fg = if (isPrimary || isDanger) VColors.white else VColors.ink
    val border = if (!isPrimary && !isDanger) VColors.line else null
    Box(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(if (enabled) bg else bg.copy(alpha = 0.5f))
            .let { m -> border?.let { m.border(1.dp, it, VShapes.lg) } ?: m }
            .clickable(enabled = enabled && !isLoading) { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(
                color = fg,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
            )
        } else {
            Text(text, style = VTypography.body.copy(fontWeight = FontWeight.Bold), color = fg)
        }
    }
}

@Composable
private fun PremiumInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column {
        Text(
            label,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink2,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder ?: "", style = VTypography.caption, color = VColors.ink3) },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth().clip(VShapes.lg),
            shape = VShapes.lg,
            singleLine = true,
        )
    }
}
