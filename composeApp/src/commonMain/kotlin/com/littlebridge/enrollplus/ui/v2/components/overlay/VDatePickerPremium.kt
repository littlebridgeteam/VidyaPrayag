package com.littlebridge.enrollplus.ui.v2.components.overlay

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.buttons.VTextButton
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import com.littlebridge.enrollplus.util.MONTH_LONG
import com.littlebridge.enrollplus.util.WEEKDAY_SHORT
import com.littlebridge.enrollplus.util.dayOfWeek
import com.littlebridge.enrollplus.util.daysInMonth
import com.littlebridge.enrollplus.util.isoOf
import com.littlebridge.enrollplus.util.parseIsoDate
import com.littlebridge.enrollplus.util.todayIso

/**
 * Premium date picker — read-only field that opens a calendar dialog.
 *
 * Uses M3 Expressive tokens: [VColors] for all colors, [VShapes] for corner radii,
 * [VTypography] for text styles, [pressScale] for press interactions.
 *
 * ISO "YYYY-MM-DD" string API matches the rest of the app.
 */
@Composable
fun VDatePickerPremium(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Select date",
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    var open by remember { mutableStateOf(false) }

    Column(modifier) {
        if (label != null) {
            Text(
                text = label,
                style = VTypography.FormLabelPortal.copy(color = VColors.OnSurfaceVariant),
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        val interaction = remember { MutableInteractionSource() }
        Box(
            Modifier
                .fillMaxWidth()
                .clip(VShapes.Md)
                .background(VColors.SurfaceContainerLow)
                .border(
                    1.dp,
                    if (isError) VColors.Error else VColors.OutlineVariant,
                    VShapes.Md,
                )
                .pressScale(interaction, pressedScale = 0.98f)
                .clickable(interactionSource = interaction, enabled = enabled) { open = true }
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = if (enabled) VColors.Primary else VColors.Outline,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.size(12.dp))
                Text(
                    text = value.takeIf { it.isNotBlank() }?.let(::prettyDate) ?: placeholder,
                    style = if (value.isBlank()) VTypography.FormInput.copy(color = VColors.Outline)
                    else VTypography.FormInput.copy(color = VColors.OnSurface),
                )
            }
        }
    }

    if (open) {
        DatePickerDialogPremium(
            initialIso = value.takeIf { it.isNotBlank() } ?: todayIso(),
            onDismiss = { open = false },
            onPick = { iso -> onValueChange(iso); open = false },
        )
    }
}

@Composable
private fun DatePickerDialogPremium(
    initialIso: String,
    onDismiss: () -> Unit,
    onPick: (String) -> Unit,
) {
    val start = parseIsoDate(initialIso) ?: parseIsoDate(todayIso())!!
    var viewYear by remember { mutableStateOf(start.first) }
    var viewMonth by remember { mutableStateOf(start.second) }
    var selectedIso by remember { mutableStateOf(isoOf(start.first, start.second, start.third)) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(VShapes.TwoXl)
                .background(VColors.SurfaceContainerLowest)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Month header with prev / next nav
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val prevInteraction = remember { MutableInteractionSource() }
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(VShapes.Full)
                        .background(VColors.SurfaceContainer)
                        .pressScale(prevInteraction, pressedScale = 0.9f)
                        .clickable(interactionSource = prevInteraction, indication = null) {
                            if (viewMonth == 1) { viewMonth = 12; viewYear-- } else viewMonth--
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = VColors.OnSurface,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    "${MONTH_LONG[viewMonth - 1]} $viewYear",
                    style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
                )
                val nextInteraction = remember { MutableInteractionSource() }
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(VShapes.Full)
                        .background(VColors.SurfaceContainer)
                        .pressScale(nextInteraction, pressedScale = 0.9f)
                        .clickable(interactionSource = nextInteraction, indication = null) {
                            if (viewMonth == 12) { viewMonth = 1; viewYear++ } else viewMonth++
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = VColors.OnSurface,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Weekday header
            Row(Modifier.fillMaxWidth()) {
                WEEKDAY_SHORT.forEach { d ->
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            d,
                            style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant),
                        )
                    }
                }
            }

            // Day grid
            val firstDow = dayOfWeek(viewYear, viewMonth, 1)
            val total = daysInMonth(viewYear, viewMonth)
            val cells = firstDow + total
            val rows = (cells + 6) / 7
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                for (row in 0 until rows) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val day = cellIndex - firstDow + 1
                            Box(Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                                if (day in 1..total) {
                                    val iso = isoOf(viewYear, viewMonth, day)
                                    val isSelected = iso == selectedIso
                                    val isToday = iso == todayIso()
                                    val dayInteraction = remember { MutableInteractionSource() }
                                    Box(
                                        Modifier
                                            .size(38.dp)
                                            .clip(VShapes.Full)
                                            .background(if (isSelected) VColors.Primary else VColors.SurfaceContainerLow)
                                            .border(
                                                if (isToday && !isSelected) 1.dp else 0.dp,
                                                if (isToday && !isSelected) VColors.Primary else VColors.SurfaceContainerLow,
                                                VShapes.Full,
                                            )
                                            .pressScale(dayInteraction, pressedScale = 0.9f)
                                            .clickable(interactionSource = dayInteraction, indication = null) { selectedIso = iso },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            day.toString(),
                                            style = VTypography.FormInput.copy(
                                                color = if (isSelected) VColors.OnPrimary else VColors.OnSurface,
                                            ),
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            VPrimaryButton(text = "Select", onClick = { onPick(selectedIso) }, modifier = Modifier.fillMaxWidth())
            VTextButton(text = "Cancel", onClick = onDismiss, modifier = Modifier.fillMaxWidth())
        }
    }
}

private fun prettyDate(iso: String): String = com.littlebridge.enrollplus.util.formatDate(iso)
