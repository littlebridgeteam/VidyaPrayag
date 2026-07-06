package com.littlebridge.enrollplus.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
// Custom icons from TeacherIcons.kt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassSummaryDto
import com.littlebridge.enrollplus.presentation.TeacherViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes

@Composable
fun TeacherClassesTab(viewModel: TeacherViewModel) {
    val classesState by viewModel.classesState.collectAsState()
    val classes = (classesState as? UiState.Success)?.data?.data?.classes ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "My Classes",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp,
                color = VColors.ink,
            )
            Box(
                modifier = Modifier
                    .background(VColors.surfaceTint, VShapes.full)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "${classes.size} classes",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.ink2,
                )
            }
        }
        if (classes.isEmpty()) {
            Text(
                text = when (classesState) {
                    is UiState.Loading -> "Loading…"
                    is UiState.Error -> (classesState as UiState.Error).message
                    else -> "No classes assigned"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            )
        } else {
            classes.forEach { cls ->
                ClassCardItem(cls)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ClassCardItem(cls: TeacherClassSummaryDto) {
    val classLabel = "${cls.className}${if (cls.section.isNotBlank()) "-${cls.section}" else ""}"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {}
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = classLabel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    color = VColors.ink,
                )
                Text(
                    text = cls.subject,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink2,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Box(
                modifier = Modifier
                    .background(VColors.violetSoft, VShapes.full)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "${cls.studentCount} students",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.violetInk,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!cls.todayAttendanceMarked) {
                ClassMetaItem(TICheck, "Attendance due")
            } else {
                ClassMetaItem(TICheck, "Attendance done")
            }
            if (cls.atRiskCount > 0) {
                ClassMetaItem(TIAlert, "${cls.atRiskCount} at risk")
            }
            if (cls.isClassTeacher) {
                ClassMetaItem(TIUser, "Class teacher")
            }
        }
    }
}

@Composable
private fun ClassMetaItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VColors.ink3,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
        )
    }
}
