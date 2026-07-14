package com.littlebridge.enrollplus.feature.teacher.data.repository



import com.littlebridge.enrollplus.core.cache.CacheManager

import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult

import com.littlebridge.enrollplus.core.model.ApiResponse

import com.littlebridge.enrollplus.core.network.NetworkResult

import com.littlebridge.enrollplus.feature.admin.domain.model.ChangeRequestListResponse

import com.littlebridge.enrollplus.feature.admin.domain.model.CreateChangeRequestRequest

import com.littlebridge.enrollplus.feature.admin.domain.model.TimetableChangeRequestDto

import com.littlebridge.enrollplus.feature.teacher.data.remote.TeacherApi

import com.littlebridge.enrollplus.feature.teacher.domain.model.*

import com.littlebridge.enrollplus.feature.teacher.domain.repository.TeacherRepository



class TeacherRepositoryImpl(

    private val api: TeacherApi,

    private val cache: CacheManager,

) : TeacherRepository {

    // T-601 (DELETE-don't-patch): getHome override removed — Today tab (getDay/getWeek)

    // replaces the legacy Home tab (Doc 04 §4).



    override suspend fun listClassesV2(token: String): NetworkResult<TeacherClassesV2Response> =

        cacheFirstNetworkResult(cache, "teacher_classes", TeacherClassesV2Response.serializer()) { api.listClassesV2(token) }



    override suspend fun getClassDetailV2(token: String, assignmentId: String): NetworkResult<ClassDetailResponse> =

        cacheFirstNetworkResult(cache, "teacher_class_detail_$assignmentId", ClassDetailResponse.serializer()) { api.getClassDetailV2(token, assignmentId) }



    override suspend fun getStudentProfileV2(token: String, studentId: String): NetworkResult<StudentProfileResponse> =

        cacheFirstNetworkResult(cache, "teacher_student_profile_$studentId", StudentProfileResponse.serializer()) { api.getStudentProfileV2(token, studentId) }



    override suspend fun getDay(token: String, date: String?): NetworkResult<ResolvedDayResponse> =

        cacheFirstNetworkResult(cache, "teacher_day_${date ?: "today"}", ResolvedDayResponse.serializer()) { api.getDay(token, date) }



    override suspend fun getWeek(token: String, date: String?): NetworkResult<ResolvedWeekResponse> =

        cacheFirstNetworkResult(cache, "teacher_week_${date ?: "this"}", ResolvedWeekResponse.serializer()) { api.getWeek(token, date) }



    override suspend fun loadAttendance(token: String, assignmentId: String, date: String?): NetworkResult<AttendanceLoadResponse> =

        cacheFirstNetworkResult(cache, "teacher_attendance_${assignmentId}_${date ?: "today"}", AttendanceLoadResponse.serializer()) { api.loadAttendance(token, assignmentId, date) }



    // T-406: legacy getHomework override removed (listHomework replaces it).



    // T-402: typed, assignment-scoped syllabus (Doc 08 §1.2/§3).

    override suspend fun loadSyllabus(token: String, assignmentId: String): NetworkResult<SyllabusLoadResponse> =

        cacheFirstNetworkResult(cache, "teacher_syllabus_$assignmentId", SyllabusLoadResponse.serializer()) { api.loadSyllabus(token, assignmentId) }



    override suspend fun createSyllabusUnit(token: String, request: CreateSyllabusUnitRequest): NetworkResult<SyllabusUnitMutationResponse> =

        api.createSyllabusUnit(token, request)



    override suspend fun updateSyllabusUnit(token: String, assignmentId: String, unitId: String, request: UpdateSyllabusUnitRequest): NetworkResult<SyllabusUnitMutationResponse> =

        api.updateSyllabusUnit(token, assignmentId, unitId, request)



    override suspend fun toggleSyllabusProgress(token: String, request: ToggleSyllabusProgressRequest): NetworkResult<SyllabusUnitMutationResponse> =

        api.toggleSyllabusProgress(token, request)



    override suspend fun getProfile(token: String): NetworkResult<TeacherProfileResponse> =

        cacheFirstNetworkResult(cache, "teacher_profile", TeacherProfileResponse.serializer()) { api.getProfile(token) }



    // T-302/T-303/T-304/T-305: Gradebook lifecycle (Doc 07 §2/§5/§6).

    override suspend fun listAssessments(token: String, assignmentId: String, status: String?): NetworkResult<AssessmentListResponse> =

        cacheFirstNetworkResult(cache, "teacher_assessments_${assignmentId}_${status ?: "all"}", AssessmentListResponse.serializer()) { api.listAssessments(token, assignmentId, status) }



    override suspend fun createAssessmentV2(token: String, request: CreateAssessmentRequestV2): NetworkResult<AssessmentCreateResponse> =

        api.createAssessmentV2(token, request)



    override suspend fun getAssessmentMarks(token: String, assessmentId: String): NetworkResult<MarksLoadResponse> =

        cacheFirstNetworkResult(cache, "teacher_assessment_marks_$assessmentId", MarksLoadResponse.serializer()) { api.getAssessmentMarks(token, assessmentId) }



    override suspend fun saveAssessmentMarks(token: String, assessmentId: String, request: MarksSaveRequest): NetworkResult<MarksSaveResponse> =

        api.saveAssessmentMarks(token, assessmentId, request)



    override suspend fun publishAssessment(token: String, assessmentId: String): NetworkResult<PublishResponse> =

        api.publishAssessment(token, assessmentId)



    override suspend fun unpublishAssessment(token: String, assessmentId: String): NetworkResult<PublishResponse> =

        api.unpublishAssessment(token, assessmentId)



    override suspend fun importMarksOcr(token: String, assessmentId: String, request: MarksImportOcrRequest): NetworkResult<MarksImportResponse> =

        api.importMarksOcr(token, assessmentId, request)



    override suspend fun importMarksText(token: String, assessmentId: String, request: MarksImportTextRequest): NetworkResult<MarksImportResponse> =

        api.importMarksText(token, assessmentId, request)



    override suspend fun getAssessmentHistory(token: String, assignmentId: String): NetworkResult<AssessmentHistoryResponse> =

        cacheFirstNetworkResult(cache, "teacher_assessment_history_$assignmentId", AssessmentHistoryResponse.serializer()) { api.getAssessmentHistory(token, assignmentId) }



    override suspend fun getCheckInStatus(token: String, date: String?): NetworkResult<CheckInStatusResponse> =

        cacheFirstNetworkResult(cache, "teacher_checkin_${date ?: "today"}", CheckInStatusResponse.serializer()) { api.getCheckInStatus(token, date) }



    override suspend fun checkIn(token: String, request: TeacherCheckInRequest): NetworkResult<CheckInStatusResponse> =

        api.checkIn(token, request)



    override suspend fun getObligations(token: String): NetworkResult<TeacherObligationsResponse> =

        cacheFirstNetworkResult(cache, "teacher_obligations", TeacherObligationsResponse.serializer()) { api.getObligations(token) }



    override suspend fun saveAttendance(token: String, request: AttendanceSaveRequest): NetworkResult<AttendanceSaveResponse> =

        api.saveAttendance(token, request)



    // T-406: legacy createHomework override removed (assignHomework replaces it).



    // T-405/T-406: typed homework lifecycle.

    override suspend fun listHomework(token: String, assignmentId: String): NetworkResult<HomeworkListResponse> =

        cacheFirstNetworkResult(cache, "teacher_homework_$assignmentId", HomeworkListResponse.serializer()) { api.listHomework(token, assignmentId) }



    override suspend fun assignHomework(token: String, request: AssignHomeworkRequest): NetworkResult<AssignHomeworkResponse> =

        api.assignHomework(token, request)



    override suspend fun getHomeworkBoard(token: String, homeworkId: String, assignmentId: String): NetworkResult<HomeworkBoardResponse> =

        cacheFirstNetworkResult(cache, "teacher_homework_board_${homeworkId}_$assignmentId", HomeworkBoardResponse.serializer()) { api.getHomeworkBoard(token, homeworkId, assignmentId) }



    override suspend fun grantHomeworkExtension(token: String, homeworkId: String, request: GrantExtensionRequest): NetworkResult<HomeworkMutationResponse> =

        api.grantHomeworkExtension(token, homeworkId, request)



    override suspend fun reviewHomeworkSubmission(token: String, homeworkId: String, studentId: String, request: ReviewSubmissionRequest): NetworkResult<HomeworkMutationResponse> =

        api.reviewHomeworkSubmission(token, homeworkId, studentId, request)



    override suspend fun closeHomework(token: String, homeworkId: String, assignmentId: String): NetworkResult<HomeworkMutationResponse> =

        api.closeHomework(token, homeworkId, assignmentId)



    override suspend fun getLeaveRequests(token: String, status: String?): NetworkResult<TeacherLeaveListResponse> =

        cacheFirstNetworkResult(cache, "teacher_leave_requests_${status ?: "all"}", TeacherLeaveListResponse.serializer()) { api.getLeaveRequests(token, status) }



    override suspend fun decideLeaveRequest(token: String, id: String, request: TeacherLeaveDecisionRequest): NetworkResult<ApiResponse<Unit>> =

        api.decideLeaveRequest(token, id, request)



    // T-602a: the teacher's OWN leave (apply + status list).

    override suspend fun getMyLeave(token: String, status: String?): NetworkResult<TeacherSelfLeaveListResponse> =

        cacheFirstNetworkResult(cache, "teacher_my_leave_${status ?: "all"}", TeacherSelfLeaveListResponse.serializer()) { api.getMyLeave(token, status) }



    override suspend fun applyMyLeave(token: String, request: CreateTeacherLeaveRequest): NetworkResult<TeacherSelfLeaveResponse> =

        api.applyMyLeave(token, request)



    override suspend fun broadcastToClass(token: String, request: TeacherClassBroadcastRequest): NetworkResult<TeacherClassBroadcastResponse> =

        api.broadcastToClass(token, request)



    // Read Receipts: teacher 1:1 messaging.

    override suspend fun getMessageThreads(token: String): NetworkResult<TeacherMessageThreadsResponse> =

        cacheFirstNetworkResult(cache, "teacher_message_threads", TeacherMessageThreadsResponse.serializer()) { api.getMessageThreads(token) }



    override suspend fun getThreadMessages(token: String, threadId: String): NetworkResult<TeacherThreadMessagesResponse> =

        cacheFirstNetworkResult(cache, "teacher_thread_messages_$threadId", TeacherThreadMessagesResponse.serializer()) { api.getThreadMessages(token, threadId) }



    override suspend fun markThreadRead(token: String, threadId: String): NetworkResult<Unit> =

        when (val r = api.markThreadRead(token, threadId)) {

            is NetworkResult.Success -> {

                val envelope = r.data

                if (!envelope.success) NetworkResult.Error(envelope.message.ifBlank { "Failed to mark thread as read" })

                else NetworkResult.Success(Unit)

            }

            is NetworkResult.Error -> NetworkResult.Error(r.message, r.code)

            is NetworkResult.ConnectionError -> NetworkResult.ConnectionError

        }



    override suspend fun getUnreadCount(token: String): NetworkResult<Int> =

        when (val r = api.getUnreadCount(token)) {

            is NetworkResult.Success -> {

                val dto = r.data

                if (!dto.success) NetworkResult.Error("Failed to fetch unread count")

                else NetworkResult.Success(dto.data?.unreadCount ?: 0)

            }

            is NetworkResult.Error -> NetworkResult.Error(r.message, r.code)

            is NetworkResult.ConnectionError -> NetworkResult.ConnectionError

        }



    override suspend fun sendMessage(token: String, request: TeacherSendMessageRequest): NetworkResult<TeacherSendMessageResponse> =

        api.sendMessage(token, request)



    override suspend fun uploadMessageAttachment(token: String, bytes: ByteArray, fileName: String, mimeType: String, attachmentType: String): NetworkResult<TeacherAttachmentUploadResponse> =

        api.uploadAttachment(token, bytes, fileName, mimeType, attachmentType)



    // Lesson Planning (LESSON_PLANNING_SPEC.md — P1-20)

    override suspend fun listLessonPlans(

        token: String, assignmentId: String, status: String?,

        from: String?, to: String?, unitId: String?,

    ): NetworkResult<LessonPlanListResponse> =

        cacheFirstNetworkResult(cache, "teacher_lesson_plans_${assignmentId}_${status ?: "all"}_${from ?: ""}_${to ?: ""}_${unitId ?: ""}", LessonPlanListResponse.serializer()) { api.listLessonPlans(token, assignmentId, status, from, to, unitId) }



    override suspend fun getLessonPlan(token: String, planId: String): NetworkResult<LessonPlanSingleResponse> =

        cacheFirstNetworkResult(cache, "teacher_lesson_plan_$planId", LessonPlanSingleResponse.serializer()) { api.getLessonPlan(token, planId) }



    override suspend fun createLessonPlan(token: String, request: CreateLessonPlanRequest): NetworkResult<LessonPlanSingleResponse> =

        api.createLessonPlan(token, request)



    override suspend fun updateLessonPlan(token: String, planId: String, request: UpdateLessonPlanRequest): NetworkResult<LessonPlanSingleResponse> =

        api.updateLessonPlan(token, planId, request)



    override suspend fun deleteLessonPlan(token: String, planId: String): NetworkResult<ApiResponse<Unit>> =

        api.deleteLessonPlan(token, planId)



    override suspend fun completeLessonPlan(token: String, planId: String): NetworkResult<LessonPlanSingleResponse> =

        api.completeLessonPlan(token, planId)



    override suspend fun skipLessonPlan(token: String, planId: String): NetworkResult<LessonPlanSingleResponse> =

        api.skipLessonPlan(token, planId)



    override suspend fun getLessonCalendar(token: String, assignmentId: String, month: String): NetworkResult<LessonCalendarResponse> =

        cacheFirstNetworkResult(cache, "teacher_lesson_calendar_${assignmentId}_$month", LessonCalendarResponse.serializer()) { api.getLessonCalendar(token, assignmentId, month) }



    override suspend fun listLessonTemplates(token: String, assignmentId: String): NetworkResult<LessonTemplateListResponse> =

        cacheFirstNetworkResult(cache, "teacher_lesson_templates_$assignmentId", LessonTemplateListResponse.serializer()) { api.listLessonTemplates(token, assignmentId) }



    override suspend fun saveLessonTemplate(token: String, request: SaveLessonTemplateRequest): NetworkResult<LessonTemplateDto> =

        api.saveLessonTemplate(token, request)



    override suspend fun deleteLessonTemplate(token: String, templateId: String): NetworkResult<ApiResponse<Unit>> =

        api.deleteLessonTemplate(token, templateId)



    override suspend fun instantiateLessonFromTemplate(token: String, templateId: String, request: InstantiateFromTemplateRequest): NetworkResult<LessonPlanSingleResponse> =

        api.instantiateLessonFromTemplate(token, templateId, request)



    override suspend fun getTimetableChangeRequests(token: String): NetworkResult<ChangeRequestListResponse> =

        cacheFirstNetworkResult(cache, "teacher_timetable_change_requests", ChangeRequestListResponse.serializer()) { api.getTimetableChangeRequests(token) }



    override suspend fun submitTimetableChangeRequest(token: String, request: CreateChangeRequestRequest): NetworkResult<ApiResponse<TimetableChangeRequestDto>> =

        api.submitTimetableChangeRequest(token, request)



    // ── Agentic Syllabus — parse, daily log, popup prefs, quiz, delete ────────

    override suspend fun parseSyllabus(token: String, request: SylParseRequest): NetworkResult<SylParseResponse> =

        api.parseSyllabus(token, request)



    override suspend fun confirmParsedSyllabus(token: String, request: SylParseConfirmRequest): NetworkResult<SylParseConfirmResponse> =

        api.confirmParsedSyllabus(token, request)



    override suspend fun createDailyLog(token: String, request: SylDailyLogRequest): NetworkResult<SylDailyLogResponse> =

        api.createDailyLog(token, request)



    override suspend fun listDailyLogs(token: String, assignmentId: String): NetworkResult<SylDailyLogListResponse> =

        cacheFirstNetworkResult(cache, "teacher_daily_logs_$assignmentId", SylDailyLogListResponse.serializer()) { api.listDailyLogs(token, assignmentId) }



    override suspend fun shouldShowDailyLogPopup(token: String): NetworkResult<SylShouldShowResponse> =

        api.shouldShowDailyLogPopup(token)



    override suspend fun setPopupPrefs(token: String, request: SylPopupPrefsRequest): NetworkResult<SylPopupPrefsResponse> =

        api.setPopupPrefs(token, request)



    override suspend fun getPopupPrefs(token: String): NetworkResult<SylPopupPrefsResponse> =

        cacheFirstNetworkResult(cache, "teacher_popup_prefs", SylPopupPrefsResponse.serializer()) { api.getPopupPrefs(token) }



    override suspend fun deleteSyllabusUnit(token: String, assignmentId: String, unitId: String): NetworkResult<SylDeleteUnitResponse> =

        api.deleteSyllabusUnit(token, assignmentId, unitId)



    override suspend fun generateQuiz(token: String, request: QuizGenerateRequest): NetworkResult<QuizGenerateResponse> =

        api.generateQuiz(token, request)



    override suspend fun publishQuiz(token: String, quizId: String): NetworkResult<QuizPublishResponse> =

        api.publishQuiz(token, quizId)



    override suspend fun listQuizzes(token: String, assignmentId: String): NetworkResult<QuizListResponse> =

        cacheFirstNetworkResult(cache, "teacher_quizzes_$assignmentId", QuizListResponse.serializer()) { api.listQuizzes(token, assignmentId) }



    override suspend fun getQuizResults(token: String, quizId: String): NetworkResult<QuizListResponse> =

        cacheFirstNetworkResult(cache, "teacher_quiz_results_$quizId", QuizListResponse.serializer()) { api.getQuizResults(token, quizId) }



    override suspend fun getQuizLeaderboard(token: String, quizId: String): NetworkResult<TeacherQuizLeaderboardResponse> =

        cacheFirstNetworkResult(cache, "teacher_quiz_leaderboard_$quizId", TeacherQuizLeaderboardResponse.serializer()) { api.getQuizLeaderboard(token, quizId) }



    override suspend fun updateQuizQuestion(token: String, quizId: String, questionId: String, request: QuizUpdateQuestionRequest): NetworkResult<QuizUpdateQuestionResponse> =

        api.updateQuizQuestion(token, quizId, questionId, request)



    override suspend fun addQuizQuestion(token: String, quizId: String, request: QuizUpdateQuestionRequest): NetworkResult<QuizUpdateQuestionResponse> =

        api.addQuizQuestion(token, quizId, request)



    override suspend fun regenerateQuiz(token: String, quizId: String): NetworkResult<QuizRegenerateResponse> =

        api.regenerateQuiz(token, quizId)



    // ── NCERT Auto-fill + Approval + Pace ───────────────────────────────────

    override suspend fun autoFillSyllabus(token: String, request: SylAutoFillRequest): NetworkResult<SylAutoFillResponse> =

        api.autoFillSyllabus(token, request)



    override suspend fun confirmAutoFillSyllabus(token: String, assignmentId: String, chapters: List<SylAutoFillChapter>): NetworkResult<SylParseConfirmResponse> =

        api.confirmAutoFillSyllabus(token, assignmentId, chapters)



    override suspend fun approveSyllabus(token: String, request: SylApproveRequest): NetworkResult<SylApproveResponse> =

        api.approveSyllabus(token, request)



    override suspend fun rejectSyllabus(token: String, request: SylApproveRequest): NetworkResult<SylApproveResponse> =

        api.rejectSyllabus(token, request)



    override suspend fun getPaceWarning(token: String, assignmentId: String): NetworkResult<ApiResponse<SylPaceWarning>> =

        cacheFirstNetworkResult(cache, "teacher_pace_warning_$assignmentId", ApiResponse.serializer(SylPaceWarning.serializer())) { api.getPaceWarning(token, assignmentId) }



    // ── Attendance Analytics ───────────────────────────────────────────────

    override suspend fun getAttendanceAnalytics(token: String, assignmentId: String, from: String?, to: String?): NetworkResult<AttendanceAnalyticsResponse> =

        api.getAttendanceAnalytics(token, assignmentId, from, to)



    override suspend fun getStudentAnalytics(token: String, assignmentId: String, studentId: String, from: String?, to: String?): NetworkResult<StudentAnalyticsResponse> =

        api.getStudentAnalytics(token, assignmentId, studentId, from, to)

}

