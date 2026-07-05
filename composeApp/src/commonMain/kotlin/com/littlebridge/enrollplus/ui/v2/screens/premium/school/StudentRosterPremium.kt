package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.form.VSearchField
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentRosterPremium(
    onBack: () -> Unit = {},
    onOpenStudent: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: StudentRosterViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var query by remember { mutableStateOf("") }

    val filtered = state.students.filter { s ->
        query.isBlank() || s.fullName.contains(query, ignoreCase = true) || s.className.contains(query, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Student Roster", onBack = onBack)

        Column(
            Modifier.padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VSearchField(value = query, onValueChange = { query = it }, placeholder = "Search students...")

            VStateHostPremium(
                loading = state.isLoading,
                error = state.error,
                isEmpty = filtered.isEmpty(),
                emptyTitle = if (state.students.isEmpty()) "No students yet" else "No matches",
                skeleton = { VShimmerListPremium(itemCount = 6) },
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filtered.forEach { s ->
                        VListTilePremium(
                            title = s.fullName,
                            subtitle = "${s.className} - ${s.section} | Roll: ${s.rollNumber}",
                            onClick = { onOpenStudent(s.id) },
                            leadingIcon = VIcons.Users,
                            trailingText = "${s.attendancePercent.toInt()}%",
                        )
                    }
                }
            }
        }
    }
}
