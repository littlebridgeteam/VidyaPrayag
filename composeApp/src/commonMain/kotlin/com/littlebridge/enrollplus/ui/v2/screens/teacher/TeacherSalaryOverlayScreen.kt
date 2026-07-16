package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.domain.model.SalaryRecordDto
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherSalaryViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherSalaryOverlayScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeacherSalaryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) isRefreshing = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Salary & Payments", onBack = onBack)

        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.records.isEmpty() && !state.isLoading,
            emptyTitle = "No Salary Records",
            emptyBody = "Your salary history will appear here once the school admin sets up your salary.",
            emptyIcon = VIcons.Wallet,
            onRetry = { viewModel.load() },
            skeleton = { SkeletonList(rows = 4) },
        ) {
            VPullRefresh(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    viewModel.load()
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (state.records.isNotEmpty()) {
                        val totalEarned = state.records.sumOf { it.netAmount }
                        val totalReceived = state.records.filter { it.status == "PAID" }.sumOf { it.netAmount }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            VCard(modifier = Modifier.weight(1f)) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Total Earned", style = VTypography.caption, color = VColors.ink3)
                                    Text(
                                        "₹${"%,.0f".format(totalEarned)}",
                                        style = VTypography.body.copy(fontWeight = FontWeight.Bold),
                                        color = VColors.violet,
                                    )
                                }
                            }
                            VCard(modifier = Modifier.weight(1f)) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Total Received", style = VTypography.caption, color = VColors.ink3)
                                    Text(
                                        "₹${"%,.0f".format(totalReceived)}",
                                        style = VTypography.body.copy(fontWeight = FontWeight.Bold),
                                        color = VColors.success,
                                    )
                                }
                            }
                        }
                    }
                    state.records.forEach { record ->
                        SalaryRecordCard(record = record)
                    }
                }
            }
        }
    }
}

@Composable
private fun SalaryRecordCard(record: SalaryRecordDto) {
    VCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(record.month, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink)
                VBadge(
                    text = record.status,
                    tone = if (record.status == "PAID") VBadgeTone.Success else VBadgeTone.Warning,
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Base: ₹${"%,.0f".format(record.baseSalary)}", style = VTypography.caption, color = VColors.ink2)
                    Text("Allowances: ₹${"%,.0f".format(record.allowances)}", style = VTypography.caption, color = VColors.ink2)
                    Text("Deductions: ₹${"%,.0f".format(record.deductions)}", style = VTypography.caption, color = VColors.ink2)
                }
                Text(
                    "Net: ₹${"%,.0f".format(record.netAmount)}",
                    style = VTypography.body,
                    fontWeight = FontWeight.Bold,
                    color = VColors.violet,
                )
            }
            record.paidAt?.let {
                Spacer(Modifier.height(4.dp))
                Text("Paid on: ${it.substring(0, 10)}", style = VTypography.caption, color = VColors.success)
            }
            record.notes?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = VTypography.caption, color = VColors.ink2)
            }
        }
    }
}
