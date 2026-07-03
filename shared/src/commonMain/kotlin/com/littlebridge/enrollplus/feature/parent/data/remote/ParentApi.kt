package com.littlebridge.enrollplus.feature.parent.data.remote

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.network.safeApiCall
import com.littlebridge.enrollplus.feature.parent.domain.model.*
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizSubmitRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizSubmitResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ParentApi(
    private val client: HttpClient,
    private val baseUrl: String
) {
    private fun getUrl(path: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return "$base$cleanPath"
    }

    suspend fun getDashboard(token: String): NetworkResult<ParentDashboardResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/dashboard"))
        }
    }

    suspend fun getTrackProgress(token: String): NetworkResult<TrackProgressResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/track-progress"))
        }
    }

    suspend fun getFees(token: String, childId: String? = null): NetworkResult<FeeResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/fees")) {
                // RA-S05: scope the fee stats to the active child when one is selected.
                childId?.takeIf { it.isNotBlank() }?.let { parameter("child_id", it) }
            }
        }
    }

    suspend fun getScholarships(token: String): NetworkResult<ScholarshipsResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/scholarships"))
        }
    }

    suspend fun getAnnouncements(token: String): NetworkResult<ParentAnnouncementsResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/announcements"))
        }
    }

    // RA-42/46: point at the role-aware spine endpoint so admin/teacher tokens
    // get their OWN notifications (the old /parent/notifications returned empty
    // for non-parents). Response shape is identical (notifications + unread_count).
    suspend fun getNotifications(token: String): NetworkResult<ParentNotificationsResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/notifications"))
        }
    }

    /** RA-46: persist a single mark-read on the server. */
    suspend fun markNotificationRead(token: String, id: String): NetworkResult<Unit> {
        return safeApiCall {
            client.patch(getUrl("api/v1/notifications/$id/read"))
        }
    }

    /** RA-46: persist mark-all-read on the server. */
    suspend fun markAllNotificationsRead(token: String): NetworkResult<Unit> {
        return safeApiCall {
            client.post(getUrl("api/v1/notifications/read-all"))
        }
    }

    // ── RA-43 / RA-56: child-scoped academic reads ───────────────────────────
    // childId is a server-issued UUID (no encoding hazard); the school+child
    // ownership check lives server-side in requireOwnedChild().

    suspend fun getChildAttendance(token: String, childId: String): NetworkResult<ParentAttendanceResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/child/$childId/attendance"))
        }
    }

    suspend fun getChildMarks(token: String, childId: String): NetworkResult<ParentMarksResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/child/$childId/marks"))
        }
    }

    suspend fun getChildSyllabus(token: String, childId: String): NetworkResult<ParentSyllabusResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/child/$childId/syllabus"))
        }
    }

    suspend fun getChildTimetable(token: String, childId: String): NetworkResult<ParentTimetableResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/child/$childId/timetable"))
        }
    }

    // ── RA-44: parent leave workflow ─────────────────────────────────────────

    /** List the parent's own submitted leave requests. */
    suspend fun getLeaveRequests(token: String): NetworkResult<ParentLeaveListResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/leave"))
        }
    }

    /** Apply for leave on behalf of an owned child (routes to the class teacher). */
    suspend fun applyLeave(token: String, request: CreateParentLeaveRequest): NetworkResult<ParentLeaveCreateResponse> {
        return safeApiCall {
            client.post(getUrl("api/v1/parent/leave")) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    suspend fun searchSchools(token: String, query: String): NetworkResult<SchoolSearchResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/schools/search")) {
                url { parameters.append("q", query) }
            }
        }
    }

    suspend fun linkChild(token: String, request: LinkChildRequest): NetworkResult<LinkChildResponse> {
        return safeApiCall {
            client.post(getUrl("api/v1/parent/link-child")) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    // ---------------- RA-51: parent ↔ teacher/admin messaging ----------------

    suspend fun getMessageThreads(token: String): NetworkResult<ParentMessageThreadsResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/messages/threads"))
        }
    }

    suspend fun getThreadMessages(token: String, threadId: String): NetworkResult<ParentThreadMessagesResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/messages/threads/$threadId/messages"))
        }
    }

    /** Read Receipts Phase 1: POST /api/v1/parent/messages/threads/{id}/read */
    suspend fun markThreadRead(token: String, threadId: String): NetworkResult<ApiResponse<Unit>> {
        return safeApiCall {
            client.post(getUrl("api/v1/parent/messages/threads/$threadId/read"))
        }
    }

    /** Read Receipts Phase 2: GET /api/v1/parent/messages/unread-count */
    suspend fun getUnreadCount(token: String): NetworkResult<ParentUnreadCountDto> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/messages/unread-count"))
        }
    }

    suspend fun sendMessage(token: String, request: ParentSendMessageRequest): NetworkResult<ParentSendMessageResponse> {
        return safeApiCall {
            client.post(getUrl("api/v1/parent/messages")) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    // RA-S07: people the parent can start a conversation with (child's class teacher(s) + admin desk).
    suspend fun getMessageRecipients(token: String): NetworkResult<ParentRecipientsResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/messages/recipients"))
        }
    }

    // ── Parent Pulse (PARENT_PULSE_SPEC.md — weekly AI digest) ───────────────

    suspend fun getLatestPulse(token: String, childId: String): NetworkResult<PulseResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/pulse/latest/$childId"))
        }
    }

    suspend fun getPulseHistory(token: String, childId: String, weeks: Int = 12): NetworkResult<PulseHistoryResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/pulse/history/$childId")) {
                url { parameters.append("weeks", weeks.toString()) }
            }
        }
    }

    // ── Agentic Syllabus — daily summary, syllabus-v2, quiz (Phase 2+) ────────

    /** Daily summary: what was taught today across all subjects. */
    suspend fun getDailySummary(token: String, childId: String, date: String? = null): NetworkResult<ParentDailySummaryResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/child/$childId/daily-summary")) {
                date?.takeIf { it.isNotBlank() }?.let { parameter("date", it) }
            }
        }
    }

    /** Syllabus v2: typed curriculum units with coverage per subject. */
    suspend fun getSyllabusV2(token: String, childId: String): NetworkResult<ParentSyllabusV2Response> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/child/$childId/syllabus-v2"))
        }
    }

    /** Quiz list: pending quizzes for the child. */
    suspend fun getQuizList(token: String, childId: String): NetworkResult<ParentQuizListResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/child/$childId/quizzes"))
        }
    }

    /** Quiz detail: questions for a quiz (correct answers NOT included). */
    suspend fun getQuizDetail(token: String, quizId: String): NetworkResult<ParentQuizDetailResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/quiz/$quizId"))
        }
    }

    /** Submit quiz answers → get result with correct answers + explanations. */
    suspend fun submitQuiz(token: String, request: QuizSubmitRequest): NetworkResult<QuizSubmitResponse> {
        return safeApiCall {
            client.post(getUrl("api/v1/parent/quiz/submit")) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    /** Quiz leaderboard: per-quiz ranking of all students who attempted it. */
    suspend fun getQuizLeaderboard(token: String, childId: String, quizId: String): NetworkResult<QuizLeaderboardResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/child/$childId/quiz/$quizId/leaderboard"))
        }
    }

    /** Fetch past quiz results for a submitted quiz. */
    suspend fun getQuizResult(token: String, childId: String, quizId: String): NetworkResult<QuizSubmitResponse> {
        return safeApiCall {
            client.get(getUrl("api/v1/parent/child/$childId/quiz/$quizId/result"))
        }
    }
}
