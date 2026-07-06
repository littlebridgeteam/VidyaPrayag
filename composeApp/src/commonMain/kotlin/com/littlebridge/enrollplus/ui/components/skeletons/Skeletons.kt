package com.littlebridge.enrollplus.ui.components.skeletons

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.components.shimmer
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes

@Composable
private fun SkeletonBlock(
    modifier: Modifier = Modifier,
    cornerShape: androidx.compose.ui.graphics.Shape = VShapes.md,
) {
    Box(
        modifier = modifier
            .background(VColors.surfaceTint, cornerShape)
            .shimmer(),
    )
}

@Composable
fun SkeletonDashboard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        // Hero card block
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            cornerShape = VShapes.lg,
        )
        Spacer(Modifier.height(16.dp))

        // 3 card blocks
        repeat(3) {
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Timeline block
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
        )
        Spacer(Modifier.height(16.dp))

        // Grid blocks (2x2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(2) {
                SkeletonBlock(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(2) {
                SkeletonBlock(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                )
            }
        }
    }
}

@Composable
fun SkeletonList(rows: Int = 5) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        repeat(rows) {
            SkeletonListRow()
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun SkeletonListRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.white, VShapes.md)
            .shadow(1.dp, VShapes.md)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBlock(
            modifier = Modifier.size(34.dp),
            cornerShape = VShapes.sm,
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp),
            )
            Spacer(Modifier.height(6.dp))
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(12.dp),
            )
        }
    }
}

@Composable
fun SkeletonFee() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        // Hero card
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            cornerShape = VShapes.lg,
        )
        Spacer(Modifier.height(16.dp))

        // 3 list rows
        repeat(3) {
            SkeletonListRow()
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun SkeletonAnnouncements() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        repeat(3) {
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun SkeletonCalendar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
        )
    }
}

@Composable
fun SkeletonProfile() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Avatar circle
        SkeletonBlock(
            modifier = Modifier.size(72.dp),
            cornerShape = CircleShape,
        )
        Spacer(Modifier.height(16.dp))

        // Name line
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(20.dp),
        )
        Spacer(Modifier.height(8.dp))

        // Subtitle line
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(14.dp),
        )
        Spacer(Modifier.height(20.dp))

        // Stats grid (2x2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(2) {
                SkeletonBlock(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(2) {
                SkeletonBlock(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                )
            }
        }
    }
}
