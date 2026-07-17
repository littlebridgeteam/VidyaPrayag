package com.littlebridge.enrollplus.ui.v2.screens.tutor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.tutor.domain.model.SafetyFlagDto
import com.littlebridge.enrollplus.feature.tutor.presentation.TeacherSafetyFlagsViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.VErrorState
import com.littlebridge.enrollplus.ui.v2.screens.VLoadingState
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.teacher.VtC
import com.littlebridge.enrollplus.ui.v2.screens.teacher.VtT
import com.littlebridge.enrollplus.ui.v2.screens.teacher.coloredV
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherSafetyFlagsScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TeacherSafetyFlagsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val c = VtC

    LaunchedEffect(Unit) {
        viewModel.loadFlags()
    }

    Box(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(c.background)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            VBackHeader(title = "Safety Flags", onBack = onBack)

            when {
                state.isLoading -> VLoadingState()
                state.error != null -> VErrorState(
                    message = state.error ?: "",
                    onRetry = { viewModel.loadFlags() },
                )
                state.flags.isEmpty() -> VEmptyState(
                    title = "No safety flags",
                    body = "Flagged tutor sessions will appear here.",
                    icon = VIcons.CheckCircle,
                )
                else -> FlagsContent(state.flags)
            }
        }
    }
}

@Composable
private fun FlagsContent(flags: List<SafetyFlagDto>) {
    val c = VtC
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                "Flagged Sessions (${flags.size})",
                style = VtT.h3.coloredV(c.ink),
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        items(flags) { flag ->
            FlagCard(flag)
        }
    }
}

@Composable
private fun FlagCard(flag: SafetyFlagDto) {
    val c = VtC
    VCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(VColors.coral.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("!", color = VColors.coral, fontWeight = FontWeight.Bold)
            }
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    flag.childName,
                    style = VtT.body.coloredV(c.ink),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Flag: ${flag.safetyFlag ?: "unknown"}",
                    style = VtT.caption.coloredV(VColors.coral),
                )
                Text(
                    "Date: ${flag.createdAt.take(16)}",
                    style = VtT.caption.coloredV(c.ink3),
                )
            }
        }
    }
}
