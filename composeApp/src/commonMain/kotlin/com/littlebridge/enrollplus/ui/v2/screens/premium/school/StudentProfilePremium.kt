package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.StudentProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VStatCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.components.overlay.VConfirmDialogPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentProfilePremium(
    studentId: String,
    onBack: () -> Unit = {},
    onRemoved: () -> Unit = onBack,
    onOpenHealth: ((String, String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: StudentProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(studentId) { viewModel.load(studentId) }
    LaunchedEffect(state.removed) { if (state.removed) onRemoved() }

    var showDelete by remember { mutableStateOf(false) }

    VStateHostPremium(
        loading = state.isLoading,
        error = state.error,
        isEmpty = false,
        modifier = modifier.fillMaxSize(),
        skeleton = {
            Column(
                Modifier.fillMaxSize().statusBarsPadding().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VShimmerListPremium(itemCount = 6)
            }
        },
    ) {
        val profile = state.profile ?: return@VStateHostPremium
        val s = profile.student

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VBackHeader(title = s.fullName, onBack = onBack)

            VGradientHeroPremium(
                title = s.fullName,
                subtitle = "${s.className} - ${s.section}",
                stats = listOf(
                    HeroStatPremium("${profile.attendanceRate}%", "Attendance"),
                    HeroStatPremium("${profile.teacherCount}", "Teachers"),
                    HeroStatPremium("${profile.parentCount}", "Parents"),
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Academic Overview", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                VStatCardPremium(value = "${profile.academicScore ?: 0}%", label = "Academic Score", onClick = {})
                VStatCardPremium(value = "${profile.subjectCount}", label = "Subjects", onClick = {})

                Text("Connections", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                profile.teachers.forEach { t ->
                    VListTilePremium(title = t.name, subtitle = t.subject, onClick = {}, leadingIcon = VIcons.GraduationCap)
                }
                profile.parents.forEach { p ->
                    VListTilePremium(title = p.name, subtitle = p.relation, onClick = {}, leadingIcon = VIcons.Users)
                }

                if (onOpenHealth != null) {
                    VListTilePremium(
                        title = "Health Records",
                        subtitle = "View medical profile",
                        onClick = { onOpenHealth(studentId, s.fullName) },
                        leadingIcon = VIcons.ShieldCheck,
                    )
                }

                VListTilePremium(
                    title = "Remove Student",
                    subtitle = "Soft-delete this student record",
                    onClick = { showDelete = true },
                    leadingIcon = VIcons.Close,
                )
            }
        }
    }

    VConfirmDialogPremium(
        visible = showDelete,
        title = "Remove Student",
        message = "Are you sure you want to remove ${state.profile?.student?.fullName ?: "this student"}? This is a soft-delete and can be undone.",
        confirmLabel = "Remove",
        onConfirm = { viewModel.remove(studentId); showDelete = false },
        onDismiss = { showDelete = false },
        isDestructive = true,
    )
}
