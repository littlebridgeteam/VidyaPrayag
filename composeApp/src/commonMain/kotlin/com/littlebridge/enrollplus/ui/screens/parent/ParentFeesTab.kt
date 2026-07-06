package com.littlebridge.enrollplus.ui.screens.parent

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.parent.domain.model.FeeAnnouncementDto
import com.littlebridge.enrollplus.presentation.ParentViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes

@Composable
fun ParentFeesTab(
    viewModel: ParentViewModel,
    onPayClick: () -> Unit,
) {
    val feesState by viewModel.feesState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.cream)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        when (val s = feesState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Loading fees...", color = VColors.ink3)
                }
            }
            is UiState.Error -> {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(s.message, color = VColors.coral)
                }
            }
            is UiState.Success -> {
                val data = s.data

                // Hero balance card — white with decorative circle
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .background(VColors.white, VShapes.lg)
                        .shadow(1.dp, VShapes.lg),
                ) {
                    // Decorative circle — coral top-right
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(120.dp)
                            .background(VColors.coralSoft.copy(alpha = 0.4f), CircleShape)
                            .offset(x = 30.dp, y = (-30).dp),
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                    ) {
                        Text(
                            "Outstanding Balance",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VColors.ink3,
                            letterSpacing = 1.sp,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            data.outstandingFees,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = VColors.ink,
                            letterSpacing = (-1).sp,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            if (data.overdueCount > 0) "${data.overdueCount} overdue payment(s)" else "Due by end of term",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = VColors.ink3,
                        )
                        Spacer(Modifier.height(16.dp))
                        // Pay button — violet filled with shadow
                        Row(
                            modifier = Modifier
                                .shadow(2.dp, VShapes.md, clip = false)
                                .background(VColors.violet, VShapes.md)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { onPayClick() }
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text("Pay Now", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.white)
                            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = VColors.white, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Fee announcements
                if (data.announcements.isNotEmpty()) {
                    Text(
                        "Fee Announcements",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VColors.ink3,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 10.dp),
                    )
                    data.announcements.forEach { ann ->
                        FeeAnnouncementCard(ann)
                        Spacer(Modifier.height(8.dp))
                    }
                }

                // Payment history
                Text(
                    "Payment History",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VColors.ink3,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 10.dp),
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .background(VColors.white, VShapes.md)
                        .shadow(1.dp, VShapes.md)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier.size(34.dp).background(VColors.mintSoft, VShapes.sm),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = VColors.success, modifier = Modifier.size(15.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Total Collected", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.ink, letterSpacing = (-0.2).sp)
                                Text(data.totalCollected, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VColors.ink3, modifier = Modifier.padding(top = 1.dp))
                            }
                            Text(
                                "${(data.collectionProgress * 100).toInt()}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = VColors.ink,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun FeeAnnouncementCard(ann: FeeAnnouncementDto) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .background(VColors.white, VShapes.md)
            .shadow(1.dp, VShapes.md)
            .padding(14.dp),
    ) {
        Box(
            modifier = Modifier.size(34.dp).background(VColors.coralSoft, VShapes.sm),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Campaign, contentDescription = null, tint = VColors.coral, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(ann.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.ink, letterSpacing = (-0.2).sp)
            Text(ann.description, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink2, lineHeight = 17.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
            Text(ann.time, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink3, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
