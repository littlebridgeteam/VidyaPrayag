package com.littlebridge.enrollplus.ui.v2.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicInfoOBViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.BrandingInfoOBViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.InstitutionalBasicOBViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.LaunchInfoOBViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.OnboardingTeacherInput
import com.littlebridge.enrollplus.feature.admin.presentation.TeacherProvisioningOBViewModel
import com.littlebridge.enrollplus.ui.components.FilterChip
import com.littlebridge.enrollplus.ui.components.VBackHeader
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VButtonVariant
import com.littlebridge.enrollplus.ui.components.VInput
import com.littlebridge.enrollplus.ui.components.VProgressBar
import com.littlebridge.enrollplus.ui.components.VProgressBarSegments
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SchoolOnboardingScreenV2(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    resumeStep: String = "BASIC",
    basicVm: InstitutionalBasicOBViewModel = koinViewModel(),
    brandingVm: BrandingInfoOBViewModel = koinViewModel(),
    academicVm: AcademicInfoOBViewModel = koinViewModel(),
    launchVm: LaunchInfoOBViewModel = koinViewModel(),
    teacherProvisionVm: TeacherProvisioningOBViewModel = koinViewModel(),
) {
    val titles = listOf(
        appString(StringKeys.OB_T_IDENTITY),
        appString(StringKeys.OB_T_ACADEMIC),
        appString(StringKeys.OB_T_CLASSES),
        appString(StringKeys.OB_T_SUBJECTS),
        appString(StringKeys.OB_T_TEACHERS),
        appString(StringKeys.OB_T_STUDENTS),
    )

    val initialStep = remember(resumeStep) {
        when (resumeStep.uppercase()) {
            "BRANDING" -> 2
            "ACADEMIC" -> 3
            "REVIEW" -> 6
            else -> 1
        }
    }
    var step by remember { mutableIntStateOf(initialStep) }

    var legalName by remember { mutableStateOf("") }
    var shortName by remember { mutableStateOf("") }
    var affiliation by remember { mutableStateOf("") }
    var board by remember { mutableStateOf("CBSE") }
    var schoolType by remember { mutableStateOf("Private Unaided") }
    var principalName by remember { mutableStateOf("") }
    var principalMobile by remember { mutableStateOf("") }

    val classesBuilt = remember {
        mutableStateListOf(
            OBClass("Class 9", mutableStateListOf("A", "B")),
            OBClass("Class 10", mutableStateListOf("A", "B")),
        )
    }
    val classCodes: List<String> = classesBuilt.flatMap { cl -> cl.sections.map { "${cl.name.removePrefix("Class ")}-$it" } }

    val subjects = remember {
        mutableStateListOf(
            OBSubject("s1", "Mathematics", "MAT001", "Core", mutableStateListOf()),
            OBSubject("s2", "Science", "SCI001", "Core", mutableStateListOf()),
            OBSubject("s3", "English", "ENG001", "Core", mutableStateListOf()),
            OBSubject("s4", "Hindi", "HIN001", "Language", mutableStateListOf()),
            OBSubject("s5", "Social Studies", "SOC001", "Core", mutableStateListOf()),
            OBSubject("s6", "Computer Apps", "COMP01", "Core", mutableStateListOf()),
        )
    }

    val teachers = remember { mutableStateListOf<OBTeacher>() }
    var newTeacherName by remember { mutableStateOf("") }
    var newTeacherEmail by remember { mutableStateOf("") }

    val basicSubmitting by basicVm.isSubmitting.collectAsState()
    val basicError by basicVm.errorMessage.collectAsState()
    val brandingSubmitting by brandingVm.isSubmitting.collectAsState()
    val brandingError by brandingVm.errorMessage.collectAsState()
    val academicSubmitting by academicVm.isSubmitting.collectAsState()
    val academicError by academicVm.errorMessage.collectAsState()
    val launchSubmitting by launchVm.isSubmitting.collectAsState()
    val launchError by launchVm.errorMessage.collectAsState()

    val isSubmitting = basicSubmitting || brandingSubmitting || academicSubmitting || launchSubmitting

    val currentError: String? = when (step) {
        1 -> basicError
        2 -> brandingError
        3, 4, 5 -> academicError
        else -> launchError
    }

    fun continueClicked() {
        when (step) {
            1 -> {
                basicVm.updateSchoolName(legalName)
                basicVm.updateBoard(board)
                basicVm.updateContact(principalMobile.replace(Regex("[^0-9]"), "").take(10))
                basicVm.submit(onSuccess = { step++ })
            }
            2 -> {
                brandingVm.submit(onSuccess = { step++ })
            }
            3, 4 -> {
                step++
            }
            5 -> {
                val builtClasses: List<Pair<String, List<String>>> =
                    classesBuilt.map { it.name to it.sections.toList() }
                val builtSubjects: List<Pair<String, String>> =
                    subjects.map { it.name to it.code }
                val teacherMap: Map<String, String> = buildMap {
                    teachers.forEach { t ->
                        t.assignments.forEach { (subjName, classCode) ->
                            put("$subjName|$classCode", t.name)
                        }
                    }
                }
                val toProvision = teachers
                    .filter { it.identifier.isNotBlank() }
                    .map { OnboardingTeacherInput(name = it.name, identifier = it.identifier) }

                teacherProvisionVm.provisionAll(toProvision) {
                    academicVm.applyBuiltStructure(builtClasses, builtSubjects, teacherMap)
                    academicVm.submit(onSuccess = { step++ })
                }
            }
            6 -> {
                launchVm.submit(onSuccess = { step++ })
            }
        }
    }

    if (step > 6) {
        val launchState by launchVm.state.collectAsState()
        val provisionState by teacherProvisionVm.state.collectAsState()
        val resolvedName = launchState.schoolName
            .takeIf { it.isNotBlank() && it != "—" }
            ?: legalName.takeIf { it.isNotBlank() }
            ?: appString(StringKeys.OB_CM_YOUR_SCHOOL)
        CompletionScreen(
            schoolName = resolvedName,
            provisionedTeachers = provisionState.results,
            onComplete = onComplete,
        )
        return
    }

    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .imePadding(),
    ) {
        VBackHeader(onBack = onBack)

        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
            Text(appString(StringKeys.OB_ONBOARDING), style = VTypography.caption, color = VColors.ink3)
            Spacer(Modifier.height(8.dp))
            VProgressBarSegments(total = 6, current = step)
            Spacer(Modifier.height(16.dp))
            Text(titles[step - 1], style = VTypography.h2, color = VColors.ink)
            Text(
                appString(StringKeys.OB_STEP_OF, "step" to step, "total" to 6),
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }

        AnimatedContent(
            targetState = step,
            transitionSpec = {
                val forward = targetState > initialState
                val dur = 280
                val enter = slideInHorizontally(
                    animationSpec = tween(dur),
                    initialOffsetX = { if (forward) it / 4 else -it / 4 },
                ) + fadeIn(tween(dur))
                val exit = slideOutHorizontally(
                    animationSpec = tween(dur),
                    targetOffsetX = { if (forward) -it / 4 else it / 4 },
                ) + fadeOut(tween(dur))
                enter togetherWith exit
            },
            label = "onboardingStep",
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { current ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                    when (current) {
                        1 -> IdentityStep(
                            legalName = legalName, onLegalNameChange = { legalName = it },
                            shortName = shortName, onShortNameChange = { shortName = it },
                            affiliation = affiliation, onAffiliationChange = { affiliation = it },
                            board = board, onBoardChange = { board = it },
                            schoolType = schoolType, onSchoolTypeChange = { schoolType = it },
                            principal = principalName, onPrincipalChange = { principalName = it },
                            principalMobile = principalMobile, onPrincipalMobileChange = { principalMobile = it },
                        )
                        2 -> AcademicYearStep()
                        3 -> ClassesStep(classesBuilt)
                        4 -> SubjectsStep(subjects, classCodes)
                        5 -> TeachersStep(
                            teachers, subjects, classCodes,
                            newTeacherName = newTeacherName,
                            onNewTeacherNameChange = { newTeacherName = it },
                            newTeacherEmail = newTeacherEmail,
                            onNewTeacherEmailChange = { newTeacherEmail = it },
                            onAddTeacher = {
                                val nm = newTeacherName.trim()
                                val em = newTeacherEmail.trim()
                                if (nm.isNotBlank()) {
                                    teachers.add(
                                        OBTeacher(
                                            id = "t${teachers.size + 1}",
                                            name = nm,
                                            identifier = em,
                                            mobile = "",
                                            username = em,
                                            assignments = mutableStateListOf(),
                                        )
                                    )
                                    newTeacherName = ""
                                    newTeacherEmail = ""
                                }
                            },
                        )
                        else -> StudentsStep()
                    }
                    val errMsg = currentError
                    if (!errMsg.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(errMsg, style = VTypography.caption, color = VColors.error)
                    }
                    Spacer(Modifier.height(8.dp))
                }
        }

        Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.lineSoft))
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (step > 1) {
                VButton(
                    text = appString(StringKeys.OB_BACK),
                    onClick = { if (!isSubmitting) step-- },
                    variant = VButtonVariant.Outline,
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(0.4f),
                )
            }
            VButton(
                text = if (step < 6) appString(StringKeys.OB_CONTINUE) else appString(StringKeys.OB_FINISH),
                onClick = { continueClicked() },
                loading = isSubmitting,
                enabled = !isSubmitting,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CreamCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(VColors.surfaceCard, VShapes.lg)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
    ) { content() }
}

@Composable
private fun MiniBadge(text: String, color: Color, bg: Color) {
    Text(
        text = text,
        style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
        color = color,
        modifier = Modifier.background(bg, VShapes.full).padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun SimpleAvatar(name: String, size: androidx.compose.ui.unit.Dp) {
    val initials = name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
    Box(
        modifier = Modifier.size(size).background(VColors.violetSoft, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, style = VTypography.label, color = VColors.violet)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IdentityStep(
    legalName: String, onLegalNameChange: (String) -> Unit,
    shortName: String, onShortNameChange: (String) -> Unit,
    affiliation: String, onAffiliationChange: (String) -> Unit,
    board: String, onBoardChange: (String) -> Unit,
    schoolType: String, onSchoolTypeChange: (String) -> Unit,
    principal: String, onPrincipalChange: (String) -> Unit,
    principalMobile: String, onPrincipalMobileChange: (String) -> Unit,
) {
    VInput(legalName, onLegalNameChange, label = appString(StringKeys.OB_ID_LEGAL_NAME), placeholder = appString(StringKeys.OB_ID_LEGAL_PH), modifier = Modifier.fillMaxWidth())
    VInput(shortName, onShortNameChange, label = appString(StringKeys.OB_ID_SHORT_NAME), placeholder = appString(StringKeys.OB_ID_SHORT_PH), modifier = Modifier.fillMaxWidth())
    VInput(affiliation, onAffiliationChange, label = appString(StringKeys.OB_ID_AFFIL), placeholder = appString(StringKeys.OB_ID_AFFIL_PH), modifier = Modifier.fillMaxWidth())

    Text(appString(StringKeys.OB_ID_BOARD), style = VTypography.label, color = VColors.ink3)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("CBSE", "ICSE", "UP State", "Other").forEach { b ->
            FilterChip(label = b, selected = board == b, onClick = { onBoardChange(b) })
        }
    }
    Text(appString(StringKeys.OB_ID_SCHOOL_TYPE), style = VTypography.label, color = VColors.ink3)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Government", "Private Aided", "Private Unaided", "Central").forEach { t ->
            FilterChip(label = t, selected = schoolType == t, onClick = { onSchoolTypeChange(t) })
        }
    }
    VInput(principal, onPrincipalChange, label = appString(StringKeys.OB_ID_PRINCIPAL), placeholder = appString(StringKeys.OB_ID_PRINCIPAL_PH), modifier = Modifier.fillMaxWidth())
    VInput(principalMobile, onPrincipalMobileChange, label = appString(StringKeys.OB_ID_PRINCIPAL_MOB), placeholder = appString(StringKeys.OB_ID_PRINCIPAL_MOB_PH), keyboardType = KeyboardType.Phone, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun AcademicYearStep() {
    var year by remember { mutableStateOf("2025-26") }
    var workingDays by remember { mutableStateOf("Mon–Sat") }
    var starts by remember { mutableStateOf("") }
    var ends by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var periods by remember { mutableStateOf("") }

    Text(appString(StringKeys.OB_AY_CURRENT), style = VTypography.label, color = VColors.ink3)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("2025-26", "2026-27").forEach { y -> FilterChip(label = y, selected = year == y, onClick = { year = y }) }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        VInput(starts, { starts = it }, label = appString(StringKeys.OB_AY_STARTS), placeholder = appString(StringKeys.OB_AY_STARTS), modifier = Modifier.weight(1f))
        VInput(ends, { ends = it }, label = appString(StringKeys.OB_AY_ENDS), placeholder = appString(StringKeys.OB_AY_ENDS), modifier = Modifier.weight(1f))
    }
    Text(appString(StringKeys.OB_AY_WORKING_DAYS), style = VTypography.label, color = VColors.ink3)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(label = "Mon–Fri", selected = workingDays == "Mon–Fri", onClick = { workingDays = "Mon–Fri" })
        FilterChip(label = "Mon–Sat", selected = workingDays == "Mon–Sat", onClick = { workingDays = "Mon–Sat" })
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        VInput(startTime, { startTime = it }, label = appString(StringKeys.OB_AY_START_TIME), placeholder = "08:00 AM", modifier = Modifier.weight(1f))
        VInput(endTime, { endTime = it }, label = appString(StringKeys.OB_AY_END_TIME), placeholder = "02:00 PM", modifier = Modifier.weight(1f))
    }
    VInput(periods, { periods = it }, label = appString(StringKeys.OB_AY_PERIODS), placeholder = appString(StringKeys.OB_AY_PERIODS_PH), keyboardType = KeyboardType.Number, modifier = Modifier.fillMaxWidth())
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClassesStep(classesBuilt: MutableList<OBClass>) {
    var newClassName by remember { mutableStateOf("") }

    CreamCard {
        Text(appString(StringKeys.OB_CL_TIP), style = VTypography.label, color = VColors.ink3)
        Text(appString(StringKeys.OB_CL_TIP_BODY), style = VTypography.caption, color = VColors.ink2, modifier = Modifier.padding(top = 4.dp))
    }
    classesBuilt.forEachIndexed { idx, cl ->
        CreamCard {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(cl.name, style = VTypography.body.copy(fontWeight = FontWeight.Bold), color = VColors.ink, modifier = Modifier.weight(1f))
                Text(appString(StringKeys.OB_CL_SECTIONS, "count" to cl.sections.size), style = VTypography.caption, color = VColors.ink3)
            }
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("A", "B", "C", "D", "E", "F").forEach { s ->
                    val on = cl.sections.contains(s)
                    FilterChip(label = s, selected = on, onClick = {
                        if (on) cl.sections.remove(s) else { cl.sections.add(s); cl.sections.sort() }
                    })
                }
            }
        }
    }
    CreamCard {
        Text(appString(StringKeys.OB_CL_ADD_MANUAL), style = VTypography.label, color = VColors.ink3)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
            VInput(newClassName, { newClassName = it }, placeholder = appString(StringKeys.OB_CL_ADD_PH), modifier = Modifier.weight(1f))
            VButton(
                text = appString(StringKeys.OB_CL_ADD_BTN),
                onClick = {
                    if (newClassName.isNotBlank()) {
                        classesBuilt.add(OBClass(newClassName.trim(), mutableStateListOf("A")))
                        newClassName = ""
                    }
                },
                enabled = newClassName.isNotBlank(),
                modifier = Modifier.weight(0.4f),
            )
        }
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("Class 11", "Class 12", "Nursery", "LKG", "UKG").forEach { q ->
                FilterChip(label = "+ $q", selected = false, onClick = { classesBuilt.add(OBClass(q, mutableStateListOf("A"))) })
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubjectsStep(subjects: MutableList<OBSubject>, classCodes: List<String>) {
    CreamCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(appString(StringKeys.OB_SJ_OFFERED), style = VTypography.label, color = VColors.ink3)
                Text(appString(StringKeys.OB_SJ_TAP_HINT), style = VTypography.caption, color = VColors.ink3, modifier = Modifier.padding(top = 2.dp))
            }
            Text(
                appString(StringKeys.OB_SJ_APPLY_ALL),
                style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                color = VColors.violet,
                modifier = Modifier.padding(start = 8.dp).clickable {
                    subjects.forEach { s -> s.classes.clear(); s.classes.addAll(classCodes) }
                },
            )
        }
    }
    subjects.forEach { s ->
        CreamCard {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(s.name, style = VTypography.body.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                    Text("${s.code} · ${s.type}", style = VTypography.caption, color = VColors.ink3)
                }
                MiniBadge(
                    text = if (s.classes.isEmpty()) appString(StringKeys.OB_SJ_NO_CLASSES) else "${s.classes.size} / ${classCodes.size}",
                    color = if (s.classes.isEmpty()) VColors.gold else VColors.success,
                    bg = if (s.classes.isEmpty()) VColors.goldSoft else VColors.successSoft,
                )
            }
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                classCodes.forEach { cc ->
                    val on = s.classes.contains(cc)
                    FilterChip(label = cc, selected = on, onClick = { if (on) s.classes.remove(cc) else s.classes.add(cc) })
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TeachersStep(
    teachers: MutableList<OBTeacher>,
    subjects: List<OBSubject>,
    classCodes: List<String>,
    newTeacherName: String,
    onNewTeacherNameChange: (String) -> Unit,
    newTeacherEmail: String,
    onNewTeacherEmailChange: (String) -> Unit,
    onAddTeacher: () -> Unit,
) {
    CreamCard {
        Text(appString(StringKeys.OB_TC_ADD), style = VTypography.label, color = VColors.ink3)
        Text(appString(StringKeys.OB_TC_ADD_DESC), style = VTypography.caption, color = VColors.ink3, modifier = Modifier.padding(top = 2.dp))
        Spacer(Modifier.height(8.dp))
        VInput(newTeacherName, onNewTeacherNameChange, label = appString(StringKeys.OB_TC_FULL_NAME), placeholder = appString(StringKeys.OB_TC_FULL_NAME_PH), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
            VInput(newTeacherEmail, onNewTeacherEmailChange, label = appString(StringKeys.OB_TC_WORK_EMAIL), placeholder = appString(StringKeys.OB_TC_WORK_EMAIL_PH), keyboardType = KeyboardType.Email, modifier = Modifier.weight(1f))
            VButton(
                text = appString(StringKeys.OB_CL_ADD_BTN),
                onClick = onAddTeacher,
                enabled = newTeacherName.isNotBlank(),
                modifier = Modifier.weight(0.4f),
            )
        }
    }

    if (teachers.isEmpty()) {
        CreamCard {
            Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(28.dp))
                Spacer(Modifier.height(4.dp))
                Text(appString(StringKeys.OB_TC_NONE_YET), style = VTypography.body.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                Text(appString(StringKeys.OB_TC_NONE_DESC), style = VTypography.caption, color = VColors.ink3, textAlign = TextAlign.Center)
            }
        }
        return
    }

    val allSlots = subjects.flatMap { s -> s.classes.map { s.name to it } }
    val assignedSlots = teachers.flatMap { it.assignments }
    val coveredCount = allSlots.count { slot -> assignedSlots.any { it.first == slot.first && it.second == slot.second } }
    val coverage = if (allSlots.isNotEmpty()) (coveredCount * 100 / allSlots.size) else 0

    CreamCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(appString(StringKeys.OB_TC_COVERAGE), style = VTypography.label, color = VColors.ink3)
                Text(appString(StringKeys.OB_TC_COVERAGE_OF, "covered" to coveredCount, "total" to allSlots.size), style = VTypography.caption, color = VColors.ink3, modifier = Modifier.padding(top = 2.dp))
            }
            Text(
                "$coverage%",
                style = VTypography.h3.copy(fontWeight = FontWeight.ExtraBold),
                color = if (coverage == 100) VColors.success else if (coverage > 50) VColors.violet else VColors.gold,
            )
        }
        Spacer(Modifier.height(8.dp))
        VProgressBar(progress = coverage.toFloat() / 100f, modifier = Modifier.fillMaxWidth())
        if (coverage < 100 && allSlots.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(appString(StringKeys.OB_TC_UNASSIGNED, "count" to (allSlots.size - coveredCount)), style = VTypography.caption, color = VColors.gold)
        }
    }

    teachers.forEach { t ->
        CreamCard {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SimpleAvatar(name = t.name, size = 40.dp)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(t.name, style = VTypography.body.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                    val meta = listOf(t.username, t.mobile).filter { it.isNotBlank() }.joinToString(" · ")
                    if (meta.isNotBlank()) {
                        Text(meta, style = VTypography.caption, color = VColors.ink3)
                    }
                }
                MiniBadge(text = appString(StringKeys.OB_TC_SLOTS, "count" to t.assignments.size), color = VColors.violet, bg = VColors.violetSoft)
            }
            Spacer(Modifier.height(8.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(VShapes.sm)
                    .border(1.dp, VColors.lineSoft, VShapes.sm),
            ) {
                Row(Modifier.fillMaxWidth().background(VColors.creamDeep), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "SUBJECT",
                        style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 0.05.em),
                        color = VColors.ink3,
                        modifier = Modifier.width(110.dp).padding(horizontal = 10.dp, vertical = 8.dp),
                    )
                    classCodes.forEach { cc ->
                        Text(
                            cc,
                            style = VTypography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = VColors.ink3,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp, vertical = 8.dp),
                        )
                    }
                }
                subjects.filter { it.classes.isNotEmpty() }.forEachIndexed { rowI, s ->
                    if (rowI > 0) {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.lineSoft))
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(s.name, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink, modifier = Modifier.width(110.dp).padding(horizontal = 10.dp, vertical = 8.dp))
                        classCodes.forEach { cc ->
                            val inSubject = s.classes.contains(cc)
                            val mine = t.assignments.any { it.first == s.name && it.second == cc }
                            val takenByOther = !mine && teachers.any { other -> other.id != t.id && other.assignments.any { it.first == s.name && it.second == cc } }
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                MatrixCell(
                                    label = if (!inSubject) "" else if (mine) "✓" else if (takenByOther) "—" else "+",
                                    inSubject = inSubject,
                                    mine = mine,
                                    takenByOther = takenByOther,
                                    enabled = inSubject && !takenByOther,
                                    onClick = {
                                        if (mine) t.assignments.removeAll { it.first == s.name && it.second == cc }
                                        else if (inSubject && !takenByOther) t.assignments.add(s.name to cc)
                                    },
                                )
                            }
                        }
                    }
                }
            }
            if (t.assignments.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    t.assignments.forEach { a ->
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .background(VColors.violet.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(
                                "${a.first.take(4)}·${a.second}",
                                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                                color = VColors.violet,
                            )
                        }
                    }
                }
            }
        }
    }

    VButton(
        text = appString(StringKeys.OB_TC_IMPORT_CSV),
        onClick = {},
        variant = VButtonVariant.Secondary,
        icon = Icons.Filled.CloudUpload,
    )
}

@Composable
private fun MatrixCell(label: String, inSubject: Boolean, mine: Boolean, takenByOther: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val available = inSubject && !mine && !takenByOther
    val bg = when {
        !inSubject -> Color.Transparent
        mine -> VColors.violet
        takenByOther -> VColors.creamDeep
        else -> VColors.violet.copy(alpha = 0.08f)
    }
    val fg = when {
        mine -> Color.White
        takenByOther -> VColors.ink3
        else -> VColors.violet
    }
    var cell = Modifier
        .padding(4.dp)
        .height(32.dp)
        .fillMaxWidth()
        .clip(VShapes.sm)
        .background(bg)
    if (available) {
        cell = cell.dashedBorder(VColors.violet.copy(alpha = 0.35f), strokeWidth = 1.dp, cornerRadius = 6.dp)
    }
    if (enabled) cell = cell.clickable(onClick = onClick)
    Box(
        cell.then(if (!inSubject) Modifier.alpha(0.25f) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (label.isNotEmpty()) {
            Text(label, style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp), color = fg)
        }
    }
}

@Composable
private fun StudentsStep() {
    CreamCard {
        Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(appString(StringKeys.OB_ST_DROP_CSV), style = VTypography.body.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
            Text(appString(StringKeys.OB_ST_OR_BROWSE), style = VTypography.caption, color = VColors.ink3)
            Spacer(Modifier.height(16.dp))
            VButton(text = appString(StringKeys.OB_ST_DOWNLOAD), onClick = {}, variant = VButtonVariant.Secondary)
        }
    }
    CreamCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(appString(StringKeys.OB_ST_NONE_YET), style = VTypography.body.copy(fontWeight = FontWeight.Bold), color = VColors.ink, modifier = Modifier.weight(1f))
            MiniBadge(text = appString(StringKeys.OB_ST_OPTIONAL), color = VColors.ink3, bg = VColors.surfaceTint)
        }
        Spacer(Modifier.height(8.dp))
        Text(appString(StringKeys.OB_ST_OPTIONAL_DESC), style = VTypography.caption, color = VColors.ink3)
    }
}

@Composable
private fun CompletionScreen(
    schoolName: String,
    provisionedTeachers: List<com.littlebridge.enrollplus.feature.admin.presentation.ProvisionedTeacher> = emptyList(),
    onComplete: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(VColors.cream)
            .verticalScroll(rememberScrollState()),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(VColors.violet),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 64.dp, bottom = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier
                        .size(96.dp)
                        .clip(VShapes.xl)
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.30f), VShapes.xl),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                }
                Spacer(Modifier.height(28.dp))
                Text(
                    appString(StringKeys.OB_CM_ALL_SET),
                    style = VTypography.h1.copy(fontSize = 30.sp, letterSpacing = (-0.02).em),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    appString(StringKeys.OB_CM_IS_LIVE, "school" to schoolName),
                    style = VTypography.body,
                    color = Color.White.copy(alpha = 0.88f),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OnboardingCompletedCard()
            val created = provisionedTeachers.filter { it.created && !it.initialPassword.isNullOrBlank() }
            if (created.isNotEmpty()) {
                Text(appString(StringKeys.OB_CM_TEACHER_LOGINS), style = VTypography.label, color = VColors.ink3)
                CreamCard {
                    Text(appString(StringKeys.OB_CM_SHARE_OTP), style = VTypography.caption, color = VColors.ink3)
                    created.forEach { t ->
                        Spacer(Modifier.height(8.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.lineSoft))
                        Spacer(Modifier.height(8.dp))
                        Text(t.name, style = VTypography.body.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                        Text(t.identifier, style = VTypography.caption, color = VColors.ink3)
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(appString(StringKeys.OB_CM_PASSWORD), style = VTypography.caption, color = VColors.ink3)
                            Text(
                                t.initialPassword.orEmpty(),
                                style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                                color = VColors.violet,
                            )
                        }
                    }
                }
            }
            val failed = provisionedTeachers.filter { !it.created }
            if (failed.isNotEmpty()) {
                CreamCard {
                    Text(appString(StringKeys.OB_CM_COULDNT_CREATE), style = VTypography.body.copy(fontWeight = FontWeight.Bold), color = VColors.gold)
                    failed.forEach { t ->
                        Text(
                            appString(StringKeys.OB_CM_ADD_LATER, "name" to t.name, "id" to t.identifier, "msg" to (t.message ?: "failed")),
                            style = VTypography.caption,
                            color = VColors.ink3,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            VButton(
                text = appString(StringKeys.OB_CM_OPEN_DASH),
                onClick = onComplete,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
            )
            Text(
                appString(StringKeys.OB_CM_EDIT_LATER),
                style = VTypography.caption,
                color = VColors.ink3,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

private class OBClass(val name: String, val sections: androidx.compose.runtime.snapshots.SnapshotStateList<String>)
private class OBSubject(
    val id: String,
    val name: String,
    val code: String,
    val type: String,
    val classes: androidx.compose.runtime.snapshots.SnapshotStateList<String>,
)
private class OBTeacher(
    val id: String,
    val name: String,
    val identifier: String,
    val mobile: String,
    val username: String,
    val assignments: androidx.compose.runtime.snapshots.SnapshotStateList<Pair<String, String>>,
)

private fun Modifier.dashedBorder(color: Color, strokeWidth: androidx.compose.ui.unit.Dp, cornerRadius: androidx.compose.ui.unit.Dp): Modifier =
    this.drawBehind {
        val sw = strokeWidth.toPx()
        val r = cornerRadius.toPx()
        val dash = PathEffect.dashPathEffect(floatArrayOf(sw * 3f, sw * 3f), 0f)
        drawRoundRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(sw / 2f, sw / 2f),
            size = Size(size.width - sw, size.height - sw),
            cornerRadius = CornerRadius(r, r),
            style = Stroke(width = sw, pathEffect = dash),
        )
    }

@Composable
fun OnboardingCompletedCard(
    modifier: Modifier = Modifier,
    onContinue: () -> Unit = {},
) {
    var animateSuccess by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (animateSuccess) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "success-scale",
    )

    val alpha by animateFloatAsState(
        targetValue = if (animateSuccess) 1f else 0f,
        animationSpec = tween(500),
        label = "success-alpha",
    )

    LaunchedEffect(Unit) {
        animateSuccess = true
    }

    CreamCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .clip(CircleShape)
                    .background(VColors.successSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = VColors.success,
                    modifier = Modifier.size(36.dp),
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = appString(StringKeys.OB_CM_READY),
                style = VTypography.h3,
                color = VColors.ink,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = appString(StringKeys.OB_CM_PROFILE_DONE),
                style = VTypography.body,
                color = VColors.ink3,
                textAlign = TextAlign.Center,
            )
        }
    }
}
