package com.littlebridge.enrollplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes

@Composable
fun VTimePicker(
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialHour: Int = 9,
    initialMinute: Int = 0,
) {
    var hour by remember { mutableIntStateOf(initialHour.coerceIn(1, 12)) }
    var minute by remember { mutableIntStateOf(initialMinute.coerceIn(0, 59)) }
    var isAm by remember { mutableStateOf(initialHour < 12) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .background(VColors.white, VShapes.lg)
            .shadow(4.dp, VShapes.lg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Time display
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = hour.toString().padStart(2, '0'),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VColors.violet,
            )
            Text(
                text = ":",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VColors.ink3,
            )
            Text(
                text = minute.toString().padStart(2, '0'),
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VColors.violet,
            )
        }

        Spacer(Modifier.height(20.dp))

        // Hour and minute selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Hour column
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hour", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VColors.ink3)
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(VColors.surfaceTint, CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { hour = if (hour == 1) 12 else hour - 1 },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
                    }
                    Text(
                        text = hour.toString().padStart(2, '0'),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VColors.ink,
                        modifier = Modifier.width(36.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(VColors.surfaceTint, CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { hour = if (hour == 12) 1 else hour + 1 },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
                    }
                }
            }

            // Minute column
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Minute", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VColors.ink3)
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(VColors.surfaceTint, CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { minute = if (minute == 0) 59 else minute - 1 },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
                    }
                    Text(
                        text = minute.toString().padStart(2, '0'),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VColors.ink,
                        modifier = Modifier.width(36.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(VColors.surfaceTint, CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { minute = if (minute == 59) 0 else minute + 1 },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // AM/PM toggle
        Row(
            modifier = Modifier
                .background(VColors.surfaceTint, VShapes.full)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOf("AM" to true, "PM" to false).forEach { (label, isAmValue) ->
                Box(
                    modifier = Modifier
                        .background(
                            if (isAm == isAmValue) VColors.violet else androidx.compose.ui.graphics.Color.Transparent,
                            VShapes.full,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { isAm = isAmValue }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (isAm == isAmValue) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = if (isAm == isAmValue) VColors.white else VColors.ink3,
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Confirm button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(VColors.violet, VShapes.md)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    val hour24 = if (isAm) hour % 12 else (hour % 12) + 12
                    onTimeSelected(hour24, minute)
                    onDismiss()
                }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Done",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = VColors.white,
            )
        }
    }
}
