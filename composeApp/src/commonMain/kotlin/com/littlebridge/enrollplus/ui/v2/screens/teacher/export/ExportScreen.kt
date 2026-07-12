package com.littlebridge.enrollplus.ui.v2.screens.teacher.export

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.export.domain.model.ExportTypeDto
import com.littlebridge.enrollplus.feature.export.presentation.ExportViewModel
import com.littlebridge.enrollplus.platform.rememberShareHelper
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

/**
 * ExportScreen — full-screen overlay that lets teachers and admins generate
 * branded PDF / CSV exports of school data (student roster, attendance, marks,
 * fee records, etc.).
 *
 * Data flow: ExportViewModel → ExportRepository → ExportApi → backend
 * /api/v1/school/export/types (GET) and /api/v1/school/export (POST).
 */
@Composable
fun ExportScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ExportViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val shareHelper = rememberShareHelper()

    LaunchedEffect(Unit) {
        viewModel.loadExportTypes()
    }

    Column(modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        VBackHeader(title = "Export Reports", onBack = onBack)

        VStateHost(
            loading = state.isLoading,
            error = if (state.exportTypes.isEmpty()) state.errorMessage else null,
            isEmpty = state.exportTypes.isEmpty() && !state.isLoading && state.errorMessage == null,
            emptyTitle = "No exports available",
            emptyBody = "Export types will appear here once configured.",
            emptyIcon = VIcons.FileText,
            onRetry = { viewModel.loadExportTypes() },
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Generate branded PDF or CSV reports for your school data. Select a report type and format below.",
                    style = VTypography.body,
                    color = VColors.ink2,
                )

                if (state.isGenerating) {
                    GeneratingBanner()
                }

                state.errorMessage?.let { msg ->
                    if (msg.isNotBlank() && state.exportTypes.isNotEmpty()) {
                        ErrorBanner(text = msg, onDismiss = { viewModel.clearMessages() })
                    }
                }

                state.infoMessage?.let { msg ->
                    if (msg.isNotBlank()) {
                        InfoBanner(text = msg)
                    }
                }

                state.exportTypes.forEach { exportType ->
                    ExportTypeCard(
                        exportType = exportType,
                        isGenerating = state.isGenerating,
                        onGenerate = { format ->
                            viewModel.clearMessages()
                            viewModel.generateExport(
                                type = exportType.type,
                                format = format,
                            )
                        },
                    )
                }

                state.downloadUrl?.let { url ->
                    DownloadResultCard(
                        fileName = state.fileName,
                        url = url,
                        onShare = { shareHelper.shareText(url, "Export download link") },
                    )
                }
            }
        }
    }
}

@Composable
private fun ExportTypeCard(
    exportType: ExportTypeDto,
    isGenerating: Boolean,
    onGenerate: (String) -> Unit,
) {
    VCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exportType.label,
                        style = VTypography.h2.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                    )
                    Text(
                        text = exportType.category,
                        style = VTypography.caption,
                        color = VColors.ink3,
                    )
                }
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                        .background(VColors.violetSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = iconForExport(exportType.icon),
                        contentDescription = null,
                        tint = VColors.violet,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                exportType.formats.forEach { format ->
                    val isThisGenerating = isGenerating
                    VButton(
                        text = format.uppercase(),
                        onClick = { onGenerate(format) },
                        variant = if (format == "pdf") VButtonVariant.Primary else VButtonVariant.Secondary,
                        size = VButtonSize.Sm,
                        enabled = !isThisGenerating,
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadResultCard(
    fileName: String?,
    url: String,
    onShare: () -> Unit,
) {
    VCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .background(VColors.mintSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = VIcons.CheckCircle,
                        contentDescription = null,
                        tint = VColors.mint,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Export Ready",
                        style = VTypography.h2.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                    )
                    Text(
                        text = fileName ?: "Your file is ready to download.",
                        style = VTypography.caption,
                        color = VColors.ink3,
                    )
                }
            }

            VButton(
                text = "Share Download Link",
                onClick = onShare,
                variant = VButtonVariant.Primary,
                size = VButtonSize.Md,
                full = true,
            )
        }
    }
}

@Composable
private fun InfoBanner(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(VShapes.md)
            .background(VColors.goldSoft)
            .border(1.dp, VColors.gold.copy(alpha = 0.3f), VShapes.md)
            .padding(16.dp),
    ) {
        Text(
            text = text,
            style = VTypography.bodySmall,
            color = VColors.ink,
        )
    }
}

private fun iconForExport(iconKey: String) = when (iconKey) {
    "roster", "students" -> VIcons.Users
    "attendance" -> VIcons.ListChecks
    "marks", "grades" -> VIcons.GraduationCap
    "fees" -> VIcons.Wallet
    "staff" -> VIcons.Users
    "homework" -> VIcons.FileText
    "admissions" -> VIcons.ClipboardList
    "leave" -> VIcons.Calendar
    "transport" -> VIcons.MapPin
    "health" -> VIcons.Heart
    "alumni" -> VIcons.Academic
    "events" -> VIcons.Calendar
    else -> VIcons.FileText
}

@Composable
private fun GeneratingBanner() {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(VShapes.md)
            .background(VColors.violetSoft)
            .border(1.dp, VColors.violet.copy(alpha = 0.3f), VShapes.md)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = VColors.violet,
            )
            Text(
                text = "Generating export... Please wait.",
                style = VTypography.bodySmall,
                color = VColors.violet,
            )
        }
    }
}

@Composable
private fun ErrorBanner(text: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(VShapes.md)
            .background(VColors.errorSoft)
            .border(1.dp, VColors.error.copy(alpha = 0.3f), VShapes.md)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                imageVector = VIcons.Close,
                contentDescription = null,
                tint = VColors.error,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = text,
                style = VTypography.bodySmall,
                color = VColors.error,
                modifier = Modifier.weight(1f),
            )
            val ix = remember { MutableInteractionSource() }
            Text(
                text = "Dismiss",
                style = VTypography.label.copy(fontWeight = FontWeight.Bold),
                color = VColors.error,
                modifier = Modifier.clip(VShapes.sm)
                    .clickable(interactionSource = ix, indication = null) { onDismiss() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}
