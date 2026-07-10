package com.littlebridge.enrollplus.feature.parent.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.parent.data.remote.ParentApi
import com.littlebridge.enrollplus.feature.parent.domain.model.*
import com.littlebridge.enrollplus.feature.parent.domain.repository.ParentRepository
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizSubmitRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizSubmitResponse

class ParentRepositoryImpl(
    private val api: ParentApi,
    private val cache: CacheManager,
) : ParentRepository {
    override suspend fun getDashboard(token: String): NetworkResult<ParentDashboardResponse> =
        cacheFirstNetworkResult(cache, "parent_dashboard", ParentDashboardResponse.serializer()) { api.getDashboard(token) }

    override suspend fun getTrackProgress(token: String): NetworkResult<TrackProgressResponse> =
        cacheFirstNetworkResult(cache, "parent_track_progress", TrackProgressResponse.serializer()) { api.getTrackProgress(token) }

    override suspend fun getFees(token: String, childId: String?): NetworkResult<FeeResponse> =
        cacheFirstNetworkResult(cache, "parent_fees_${childId ?: "all"}", FeeResponse.serializer()) { api.getFees(token, childId) }

    override suspend fun getScholarships(token: String): NetworkResult<ScholarshipsResponse> =
        cacheFirstNetworkResult(cache, "parent_scholarships", ScholarshipsResponse.serializer()) { api.getScholarships(token) }

    override suspend fun getAnnouncements(token: String): NetworkResult<ParentAnnouncementsResponse> =
        cacheFirstNetworkResult(cache, "parent_announcements", ParentAnnouncementsResponse.serializer()) { api.getAnnouncements(token) }

    override suspend fun getNotifications(token: String): NetworkResult<ParentNotificationsResponse> =
        cacheFirstNetworkResult(cache, "parent_notifications", ParentNotificationsResponse.serializer()) { api.getNotifications(token) }

    override suspend fun markNotificationRead(token: String, id: String): NetworkResult<Unit> {
        val result = api.markNotificationRead(token, id)
        cache.delete("parent_notifications")
        return result
    }

    override suspend fun markAllNotificationsRead(token: String): NetworkResult<Unit> {
        val result = api.markAllNotificationsRead(token)
        cache.delete("parent_notifications")
        return result
    }

    override suspend fun markNotificationByRef(token: String, refType: String, refId: String): NetworkResult<Unit> {
        val result = api.markNotificationByRef(token, refType, refId)
        cache.delete("parent_notifications")
        return result
    }

    override suspend fun clearReadNotifications(token: String): NetworkResult<Unit> {
        val result = api.clearReadNotifications(token)
        cache.delete("parent_notifications")
        return result
    }

    override suspend fun clearAllNotifications(token: String): NetworkResult<Unit> {
        val result = api.clearAllNotifications(token)
        cache.delete("parent_notifications")
        return result
    }

    override suspend fun getChildAttendance(token: String, childId: String): NetworkResult<ParentAttendanceResponse> =
        cacheFirstNetworkResult(cache, "parent_attendance_$childId", ParentAttendanceResponse.serializer()) { api.getChildAttendance(token, childId) }

    override suspend fun getChildMarks(token: String, childId: String): NetworkResult<ParentMarksResponse> =
        cacheFirstNetworkResult(cache, "parent_marks_$childId", ParentMarksResponse.serializer()) { api.getChildMarks(token, childId) }

    override suspend fun getChildSyllabus(token: String, childId: String): NetworkResult<ParentSyllabusResponse> =
        cacheFirstNetworkResult(cache, "parent_syllabus_$childId", ParentSyllabusResponse.serializer()) { api.getChildSyllabus(token, childId) }

    override suspend fun getChildTimetable(token: String, childId: String): NetworkResult<ParentTimetableResponse> =
        cacheFirstNetworkResult(cache, "parent_timetable_$childId", ParentTimetableResponse.serializer()) { api.getChildTimetable(token, childId) }

    override suspend fun searchSchools(token: String, query: String): NetworkResult<SchoolSearchResponse> {
        return api.searchSchools(token, query)
    }

    override suspend fun linkChild(token: String, request: LinkChildRequest): NetworkResult<LinkChildResponse> {
        return api.linkChild(token, request)
    }

    override suspend fun getLeaveRequests(token: String): NetworkResult<ParentLeaveListResponse> =
        cacheFirstNetworkResult(cache, "parent_leave_requests", ParentLeaveListResponse.serializer()) { api.getLeaveRequests(token) }

    override suspend fun applyLeave(token: String, request: CreateParentLeaveRequest): NetworkResult<ParentLeaveCreateResponse> {
        return api.applyLeave(token, request)
    }

    override suspend fun getMessageThreads(token: String): NetworkResult<ParentMessageThreadsResponse> =
        cacheFirstNetworkResult(cache, "parent_message_threads", ParentMessageThreadsResponse.serializer()) { api.getMessageThreads(token) }

    override suspend fun getThreadMessages(token: String, threadId: String): NetworkResult<ParentThreadMessagesResponse> =
        cacheFirstNetworkResult(cache, "parent_thread_messages_$threadId", ParentThreadMessagesResponse.serializer()) { api.getThreadMessages(token, threadId) }

    override suspend fun markThreadRead(token: String, threadId: String): NetworkResult<Unit> {
        return when (val result = api.markThreadRead(token, threadId)) {
            is NetworkResult.Success -> {
                val envelope = result.data
                if (!envelope.success) NetworkResult.Error(envelope.message.ifBlank { "Failed to mark thread as read" })
                else NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> NetworkResult.Error(result.message, result.code)
            is NetworkResult.ConnectionError -> NetworkResult.ConnectionError
        }
    }

    override suspend fun getUnreadCount(token: String): NetworkResult<Int> {
        return when (val result = api.getUnreadCount(token)) {
            is NetworkResult.Success -> {
                val dto = result.data
                if (!dto.success) NetworkResult.Error("Failed to fetch unread count")
                else NetworkResult.Success(dto.data?.unreadCount ?: 0)
            }
            is NetworkResult.Error -> NetworkResult.Error(result.message, result.code)
            is NetworkResult.ConnectionError -> NetworkResult.ConnectionError
        }
    }

    override suspend fun sendMessage(token: String, request: ParentSendMessageRequest): NetworkResult<ParentSendMessageResponse> {
        return api.sendMessage(token, request)
    }

    override suspend fun getMessageRecipients(token: String): NetworkResult<ParentRecipientsResponse> =
        cacheFirstNetworkResult(cache, "parent_message_recipients", ParentRecipientsResponse.serializer()) { api.getMessageRecipients(token) }

    override suspend fun getLatestPulse(token: String, childId: String): NetworkResult<PulseResponse> =
        cacheFirstNetworkResult(cache, "parent_pulse_latest_$childId", PulseResponse.serializer()) { api.getLatestPulse(token, childId) }

    override suspend fun getPulseHistory(token: String, childId: String, weeks: Int): NetworkResult<PulseHistoryResponse> =
        cacheFirstNetworkResult(cache, "parent_pulse_history_${childId}_$weeks", PulseHistoryResponse.serializer()) { api.getPulseHistory(token, childId, weeks) }

    // ── Agentic Syllabus — daily summary, syllabus-v2, quiz ───────────────────
    override suspend fun getDailySummary(token: String, childId: String, date: String?): NetworkResult<ParentDailySummaryResponse> =
        cacheFirstNetworkResult(cache, "parent_daily_summary_${childId}_${date ?: "today"}", ParentDailySummaryResponse.serializer()) { api.getDailySummary(token, childId, date) }

    override suspend fun getSyllabusV2(token: String, childId: String): NetworkResult<ParentSyllabusV2Response> =
        cacheFirstNetworkResult(cache, "parent_syllabus_v2_$childId", ParentSyllabusV2Response.serializer()) { api.getSyllabusV2(token, childId) }

    override suspend fun getQuizList(token: String, childId: String): NetworkResult<ParentQuizListResponse> =
        cacheFirstNetworkResult(cache, "parent_quiz_list_$childId", ParentQuizListResponse.serializer()) { api.getQuizList(token, childId) }

    override suspend fun getQuizDetail(token: String, quizId: String): NetworkResult<ParentQuizDetailResponse> =
        cacheFirstNetworkResult(cache, "parent_quiz_detail_$quizId", ParentQuizDetailResponse.serializer()) { api.getQuizDetail(token, quizId) }

    override suspend fun submitQuiz(token: String, childId: String, request: QuizSubmitRequest): NetworkResult<QuizSubmitResponse> {
        val result = api.submitQuiz(token, childId, request)
        if (result is NetworkResult.Success) {
            // Invalidate quiz list cache so the submitted quiz shows "SUBMITTED" status on next load
            cache.delete("parent_quiz_list_$childId")
        }
        return result
    }

    override suspend fun getQuizLeaderboard(token: String, childId: String, quizId: String): NetworkResult<QuizLeaderboardResponse> =
        cacheFirstNetworkResult(cache, "parent_quiz_leaderboard_${childId}_$quizId", QuizLeaderboardResponse.serializer()) { api.getQuizLeaderboard(token, childId, quizId) }

    override suspend fun getQuizResult(token: String, childId: String, quizId: String): NetworkResult<QuizSubmitResponse> =
        cacheFirstNetworkResult(cache, "parent_quiz_result_${childId}_$quizId", QuizSubmitResponse.serializer()) { api.getQuizResult(token, childId, quizId) }
}
