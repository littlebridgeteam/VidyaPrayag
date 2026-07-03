/*
 * File: TeacherQuizRouting.kt
 * Module: feature.teacher
 *
 * Syllabus Quiz System — AI-generated quizzes linked to syllabus assignments.
 * Supports MCQ, FILL_BLANK, TRUE_FALSE, and MATCH question types.
 * Teachers can select multiple units/subunits when creating quizzes.
 *
 * Endpoints (JWT, teacher):
 *   POST /api/v1/teacher/syllabus/quiz/generate          → AI-generate quiz from selected units
 *   GET  /api/v1/teacher/syllabus/quiz/list               → list quizzes for an assignment
 *   POST /api/v1/teacher/syllabus/quiz/{id}/publish       → publish a quiz (visible to parents)
 *   GET  /api/v1/teacher/syllabus/quiz/{id}/results       → quiz results (rankings + breakdown)
 */
package com.littlebridge.enrollplus.feature.teacher

import com.littlebridge.enrollplus.core.created
import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.requireOwnedAssignment
import com.littlebridge.enrollplus.core.requireTeacherContext
import com.littlebridge.enrollplus.db.CurriculumUnitsTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.SyllabusQuizzesTable
import com.littlebridge.enrollplus.db.SyllabusQuizQuestionsTable
import com.littlebridge.enrollplus.db.SyllabusQuizAnswersTable
import com.littlebridge.enrollplus.db.AnnouncementsTable
import com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable
import com.littlebridge.enrollplus.feature.ai.SyllabusAiService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import java.time.Instant
import java.util.UUID
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("TeacherQuizRouting")
private val quizJson = Json { ignoreUnknownKeys = true; isLenient = true }

// ── Server-side DTOs ───────────────────────────────────────────────────────

@Serializable
data class QuizGenerateReq(
    @SerialName("assignment_id") val assignmentId: String,
    @SerialName("unit_ids") val unitIds: List<String> = emptyList(),
    @SerialName("unit_id") val unitId: String = "",
    @SerialName("num_questions") val numQuestions: Int = 5,
    val difficulty: String = "MEDIUM",
    @SerialName("question_types") val questionTypes: List<String> = listOf("MCQ"),
)

@Serializable
data class MatchPairSer(
    val left: String = "",
    val right: String = "",
)

@Serializable
data class QuizQuestionSer(
    val id: String,
    val question: String,
    val options: List<String> = emptyList(),
    @SerialName("correct_index") val correctIndex: Int = 0,
    val explanation: String? = null,
    @SerialName("marks") val marks: Int = 1,
    @SerialName("question_type") val questionType: String = "MCQ",
    @SerialName("correct_answer") val correctAnswer: String = "",
    @SerialName("match_pairs") val matchPairs: List<MatchPairSer> = emptyList(),
)

@Serializable
data class QuizSer(
    val id: String,
    @SerialName("assignment_id") val assignmentId: String,
    @SerialName("unit_id") val unitId: String = "",
    @SerialName("unit_ids") val unitIds: List<String> = emptyList(),
    val title: String = "",
    val questions: List<QuizQuestionSer> = emptyList(),
    val status: String = "DRAFT",
    @SerialName("question_types") val questionTypes: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class QuizGenerateResp(
    val success: Boolean = true,
    val data: QuizSer? = null,
)

@Serializable
data class QuizListDataSer(
    val quizzes: List<QuizSer> = emptyList(),
)

@Serializable
data class QuizListResp(
    val success: Boolean = true,
    val data: QuizListDataSer = QuizListDataSer(),
)

@Serializable
data class QuizPublishResp(
    val success: Boolean = true,
    val data: QuizSer? = null,
)

@Serializable
data class QuizQuestionUpdateReq(
    val question: String,
    val options: List<String> = emptyList(),
    @SerialName("correct_answer") val correctAnswer: String = "",
    val explanation: String? = null,
    @SerialName("question_type") val questionType: String = "MCQ",
)

// ── Routing ────────────────────────────────────────────────────────────────

fun Route.teacherQuizRouting() {
    authenticate("jwt") {
        route("/api/v1/teacher/syllabus/quiz") {

            // POST /generate — AI-generate a quiz from selected units
            post("/generate") {
                val ctx = call.requireTeacherContext() ?: return@post
                val req = runCatching { call.receive<QuizGenerateReq>() }.getOrNull()
                if (req == null) {
                    call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@post
                }
                val asg = call.requireOwnedAssignment(ctx, req.assignmentId) ?: return@post

                // Collect all unit IDs (from unitIds list + legacy unitId field)
                val allUnitIds = (req.unitIds + if (req.unitId.isNotBlank()) listOf(req.unitId) else emptyList()).distinct()
                if (allUnitIds.isEmpty()) {
                    call.fail("At least one unit must be selected", HttpStatusCode.BadRequest, "NO_UNITS"); return@post
                }

                // Fetch unit titles for the AI prompt
                val unitTitles = dbQuery {
                    CurriculumUnitsTable.selectAll().where {
                        CurriculumUnitsTable.id inList allUnitIds.map { org.jetbrains.exposed.dao.id.EntityID(UUID.fromString(it), CurriculumUnitsTable) }
                    }.map { it[CurriculumUnitsTable.title] }
                }

                if (unitTitles.isEmpty()) {
                    call.fail("Selected units not found", HttpStatusCode.NotFound, "UNITS_NOT_FOUND"); return@post
                }

                val difficultyOffset = when (req.difficulty.uppercase()) {
                    "EASY" -> -5
                    "HARD" -> 5
                    else -> 0
                }

                // Generate quiz via AI
                val generatedQuestions = SyllabusAiService.generateQuiz(
                    topicTitles = unitTitles,
                    classLevel = asg.className,
                    subject = asg.subject,
                    questionTypes = req.questionTypes,
                    questionCount = req.numQuestions,
                    difficultyOffset = difficultyOffset,
                    schoolId = ctx.schoolId,
                )

                if (generatedQuestions == null || generatedQuestions.isEmpty()) {
                    call.fail("AI is currently unavailable. Please try again.", HttpStatusCode.ServiceUnavailable, "AI_UNAVAILABLE"); return@post
                }

                // Persist the quiz
                val quizId = UUID.randomUUID()
                val now = Instant.now()
                val title = "${asg.subject} Quiz — ${unitTitles.joinToString(", ").take(60)}"
                val unitIdsStr = allUnitIds.joinToString(",")
                val qTypesStr = req.questionTypes.joinToString(",")

                dbQuery {
                    SyllabusQuizzesTable.insert {
                        it[SyllabusQuizzesTable.id] = quizId
                        it[SyllabusQuizzesTable.schoolId] = ctx.schoolId
                        it[SyllabusQuizzesTable.assignmentId] = asg.assignmentId
                        it[SyllabusQuizzesTable.unitIds] = unitIdsStr
                        it[SyllabusQuizzesTable.title] = title
                        it[SyllabusQuizzesTable.questionTypes] = qTypesStr
                        it[SyllabusQuizzesTable.status] = "DRAFT"
                        it[SyllabusQuizzesTable.difficulty] = req.difficulty
                        it[SyllabusQuizzesTable.createdAt] = now
                    }

                    generatedQuestions.forEachIndexed { idx, q ->
                        val qId = UUID.randomUUID()
                        val optionsJson = quizJson.encodeToString(
                            ListSerializer(serializer<String>()), q.options
                        )
                        val matchPairsJson = "[]"
                        SyllabusQuizQuestionsTable.insert {
                            it[SyllabusQuizQuestionsTable.id] = qId
                            it[SyllabusQuizQuestionsTable.quizId] = quizId
                            it[SyllabusQuizQuestionsTable.questionType] = q.questionType
                            it[SyllabusQuizQuestionsTable.questionText] = q.questionText
                            it[SyllabusQuizQuestionsTable.optionsJson] = optionsJson
                            it[SyllabusQuizQuestionsTable.correctAnswer] = q.correctAnswer
                            it[SyllabusQuizQuestionsTable.explanation] = q.explanation
                            it[SyllabusQuizQuestionsTable.matchPairsJson] = matchPairsJson
                            it[SyllabusQuizQuestionsTable.position] = idx
                            it[SyllabusQuizQuestionsTable.createdAt] = now
                        }
                    }
                }

                // Build response
                val questions = generatedQuestions.mapIndexed { idx, q ->
                    QuizQuestionSer(
                        id = UUID.randomUUID().toString(),
                        question = q.questionText,
                        options = q.options,
                        correctIndex = q.options.indexOfFirst { it.startsWith(q.correctAnswer) }.takeIf { it >= 0 } ?: 0,
                        explanation = q.explanation,
                        questionType = q.questionType,
                        correctAnswer = q.correctAnswer,
                    )
                }

                call.created(QuizSer(
                    id = quizId.toString(),
                    assignmentId = req.assignmentId,
                    unitId = req.unitId,
                    unitIds = allUnitIds,
                    title = title,
                    questions = questions,
                    status = "DRAFT",
                    questionTypes = req.questionTypes,
                    createdAt = now.toString(),
                ))
            }

            // GET /list — list quizzes for an assignment
            get("/list") {
                val ctx = call.requireTeacherContext() ?: return@get
                val assignmentId = call.request.queryParameters["assignmentId"]
                    ?: call.request.queryParameters["assignment_id"]
                if (assignmentId.isNullOrBlank()) {
                    call.fail("assignmentId is required", HttpStatusCode.BadRequest, "MISSING_PARAM"); return@get
                }
                val asg = call.requireOwnedAssignment(ctx, assignmentId) ?: return@get

                val quizzes = dbQuery {
                    SyllabusQuizzesTable.selectAll().where {
                        SyllabusQuizzesTable.assignmentId eq asg.assignmentId
                    }.orderBy(SyllabusQuizzesTable.createdAt, SortOrder.DESC).toList()
                }

                val quizList = quizzes.map { qRow ->
                    val qId = qRow[SyllabusQuizzesTable.id].value
                    val questions = dbQuery {
                        SyllabusQuizQuestionsTable.selectAll().where {
                            SyllabusQuizQuestionsTable.quizId eq qId
                        }.orderBy(SyllabusQuizQuestionsTable.position, SortOrder.ASC).toList()
                    }
                    QuizSer(
                        id = qId.toString(),
                        assignmentId = asg.assignmentId.toString(),
                        unitId = "",
                        unitIds = qRow[SyllabusQuizzesTable.unitIds].split(",").filter { it.isNotBlank() },
                        title = qRow[SyllabusQuizzesTable.title],
                        questions = questions.map { qr ->
                            val opts = runCatching {
                                quizJson.decodeFromString(
                                    ListSerializer(serializer<String>()),
                                    qr[SyllabusQuizQuestionsTable.optionsJson]
                                )
                            }.getOrDefault(emptyList())
                            QuizQuestionSer(
                                id = qr[SyllabusQuizQuestionsTable.id].value.toString(),
                                question = qr[SyllabusQuizQuestionsTable.questionText],
                                options = opts,
                                correctIndex = 0,
                                explanation = qr[SyllabusQuizQuestionsTable.explanation],
                                questionType = qr[SyllabusQuizQuestionsTable.questionType],
                                correctAnswer = qr[SyllabusQuizQuestionsTable.correctAnswer],
                            )
                        },
                        status = qRow[SyllabusQuizzesTable.status],
                        questionTypes = qRow[SyllabusQuizzesTable.questionTypes].split(",").filter { it.isNotBlank() },
                        createdAt = qRow[SyllabusQuizzesTable.createdAt]?.toString(),
                    )
                }

                call.ok(QuizListDataSer(quizzes = quizList))
            }

            // POST /{id}/publish — publish a quiz
            post("/{id}/publish") {
                val ctx = call.requireTeacherContext() ?: return@post
                val quizIdStr = call.parameters["id"]
                if (quizIdStr.isNullOrBlank()) {
                    call.fail("Quiz ID is required", HttpStatusCode.BadRequest, "MISSING_PARAM"); return@post
                }
                val quizId = UUID.fromString(quizIdStr)

                val quizRow = dbQuery {
                    SyllabusQuizzesTable.selectAll().where {
                        (SyllabusQuizzesTable.id eq quizId) and
                            (SyllabusQuizzesTable.schoolId eq ctx.schoolId)
                    }.singleOrNull()
                }

                if (quizRow == null) {
                    call.fail("Quiz not found", HttpStatusCode.NotFound, "QUIZ_NOT_FOUND"); return@post
                }

                val asg = call.requireOwnedAssignment(ctx, quizRow[SyllabusQuizzesTable.assignmentId].toString()) ?: return@post

                val now = Instant.now()
                dbQuery {
                    SyllabusQuizzesTable.update({ SyllabusQuizzesTable.id eq quizId }) {
                        it[status] = "PUBLISHED"
                        it[publishedAt] = now
                    }
                }

                // Send announcement to parents about the new quiz
                val subjectName = dbQuery {
                    TeacherSubjectAssignmentsTable.selectAll().where {
                        TeacherSubjectAssignmentsTable.id eq quizRow[SyllabusQuizzesTable.assignmentId]
                    }.firstOrNull()?.get(TeacherSubjectAssignmentsTable.subject) ?: ""
                }
                val className = asg.className
                val quizTitle = quizRow[SyllabusQuizzesTable.title]
                dbQuery {
                    AnnouncementsTable.insert {
                        it[AnnouncementsTable.id] = UUID.randomUUID()
                        it[AnnouncementsTable.schoolId] = ctx.schoolId
                        it[AnnouncementsTable.eventId] = "quiz_${quizId}_published_${now.epochSecond}"
                        it[AnnouncementsTable.type] = "Special"
                        it[AnnouncementsTable.title] = "New Quiz: ${quizTitle.ifBlank { "Quiz" }}"
                        it[AnnouncementsTable.subTitle] = subjectName
                        it[AnnouncementsTable.description] = "A new quiz has been published for $subjectName${if (className.isNotBlank()) " - Class $className" else ""}. Check the Academics > Quizzes tab to attempt it."
                        it[AnnouncementsTable.date] = now.toString().take(10)
                        it[AnnouncementsTable.audienceType] = "CLASS"
                        it[AnnouncementsTable.audienceFilter] = """{"class_name":"$className","subject":"$subjectName"}"""
                        it[AnnouncementsTable.authorRole] = "teacher"
                        it[AnnouncementsTable.isCalendarOnly] = false
                        it[AnnouncementsTable.createdBy] = ctx.userId
                        it[AnnouncementsTable.createdAt] = now
                        it[AnnouncementsTable.updatedAt] = now
                    }
                }

                val questions = dbQuery {
                    SyllabusQuizQuestionsTable.selectAll().where {
                        SyllabusQuizQuestionsTable.quizId eq quizId
                    }.orderBy(SyllabusQuizQuestionsTable.position, SortOrder.ASC).toList()
                }

                call.ok(QuizSer(
                    id = quizId.toString(),
                    assignmentId = asg.assignmentId.toString(),
                    unitIds = quizRow[SyllabusQuizzesTable.unitIds].split(",").filter { it.isNotBlank() },
                    title = quizRow[SyllabusQuizzesTable.title],
                    questions = questions.map { qr ->
                        val opts = runCatching {
                            quizJson.decodeFromString(
                                ListSerializer(serializer<String>()),
                                qr[SyllabusQuizQuestionsTable.optionsJson]
                            )
                        }.getOrDefault(emptyList())
                        QuizQuestionSer(
                            id = qr[SyllabusQuizQuestionsTable.id].value.toString(),
                            question = qr[SyllabusQuizQuestionsTable.questionText],
                            options = opts,
                            questionType = qr[SyllabusQuizQuestionsTable.questionType],
                            correctAnswer = qr[SyllabusQuizQuestionsTable.correctAnswer],
                            explanation = qr[SyllabusQuizQuestionsTable.explanation],
                        )
                    },
                    status = "PUBLISHED",
                    questionTypes = quizRow[SyllabusQuizzesTable.questionTypes].split(",").filter { it.isNotBlank() },
                    createdAt = quizRow[SyllabusQuizzesTable.createdAt]?.toString(),
                ))
            }

            // PUT /{id}/question/{questionId} — update a single quiz question
            put("/{id}/question/{questionId}") {
                val ctx = call.requireTeacherContext() ?: return@put
                val quizIdStr = call.parameters["id"]
                val questionIdStr = call.parameters["questionId"]
                if (quizIdStr.isNullOrBlank() || questionIdStr.isNullOrBlank()) {
                    call.fail("Quiz ID and Question ID are required", HttpStatusCode.BadRequest, "MISSING_PARAM"); return@put
                }
                val quizId = UUID.fromString(quizIdStr)
                val questionId = UUID.fromString(questionIdStr)

                val req = runCatching { call.receive<QuizQuestionUpdateReq>() }.getOrNull()
                if (req == null) {
                    call.fail("Invalid request body", HttpStatusCode.BadRequest, "BAD_REQUEST"); return@put
                }

                val quizRow = dbQuery {
                    SyllabusQuizzesTable.selectAll().where {
                        (SyllabusQuizzesTable.id eq quizId) and
                            (SyllabusQuizzesTable.schoolId eq ctx.schoolId)
                    }.singleOrNull()
                }
                if (quizRow == null) {
                    call.fail("Quiz not found", HttpStatusCode.NotFound, "QUIZ_NOT_FOUND"); return@put
                }
                if (quizRow[SyllabusQuizzesTable.status] != "DRAFT") {
                    call.fail("Cannot edit a published quiz", HttpStatusCode.BadRequest, "QUIZ_NOT_DRAFT"); return@put
                }

                val optionsJson = quizJson.encodeToString(ListSerializer(serializer<String>()), req.options)
                dbQuery {
                    SyllabusQuizQuestionsTable.update({ SyllabusQuizQuestionsTable.id eq questionId }) {
                        it[SyllabusQuizQuestionsTable.questionText] = req.question
                        it[SyllabusQuizQuestionsTable.optionsJson] = optionsJson
                        it[SyllabusQuizQuestionsTable.correctAnswer] = req.correctAnswer
                        it[SyllabusQuizQuestionsTable.explanation] = req.explanation.orEmpty()
                    }
                }

                call.ok(QuizQuestionSer(
                    id = questionId.toString(),
                    question = req.question,
                    options = req.options,
                    correctIndex = 0,
                    explanation = req.explanation,
                    questionType = req.questionType,
                    correctAnswer = req.correctAnswer,
                ))
            }

            // POST /{id}/regenerate — regenerate all questions for an existing DRAFT quiz
            post("/{id}/regenerate") {
                val ctx = call.requireTeacherContext() ?: return@post
                val quizIdStr = call.parameters["id"]
                if (quizIdStr.isNullOrBlank()) {
                    call.fail("Quiz ID is required", HttpStatusCode.BadRequest, "MISSING_PARAM"); return@post
                }
                val quizId = UUID.fromString(quizIdStr)

                val quizRow = dbQuery {
                    SyllabusQuizzesTable.selectAll().where {
                        (SyllabusQuizzesTable.id eq quizId) and
                            (SyllabusQuizzesTable.schoolId eq ctx.schoolId)
                    }.singleOrNull()
                }
                if (quizRow == null) {
                    call.fail("Quiz not found", HttpStatusCode.NotFound, "QUIZ_NOT_FOUND"); return@post
                }
                if (quizRow[SyllabusQuizzesTable.status] != "DRAFT") {
                    call.fail("Cannot regenerate a published quiz", HttpStatusCode.BadRequest, "QUIZ_NOT_DRAFT"); return@post
                }

                val asg = call.requireOwnedAssignment(ctx, quizRow[SyllabusQuizzesTable.assignmentId].toString()) ?: return@post
                val unitIds = quizRow[SyllabusQuizzesTable.unitIds].split(",").filter { it.isNotBlank() }
                val qTypes = quizRow[SyllabusQuizzesTable.questionTypes].split(",").filter { it.isNotBlank() }
                val difficulty = quizRow[SyllabusQuizzesTable.difficulty]

                val unitTitles = dbQuery {
                    CurriculumUnitsTable.selectAll().where {
                        CurriculumUnitsTable.id inList unitIds.map { org.jetbrains.exposed.dao.id.EntityID(UUID.fromString(it), CurriculumUnitsTable) }
                    }.map { it[CurriculumUnitsTable.title] }
                }

                if (unitTitles.isEmpty()) {
                    call.fail("Selected units not found", HttpStatusCode.NotFound, "UNITS_NOT_FOUND"); return@post
                }

                val difficultyOffset = when (difficulty.uppercase()) {
                    "EASY" -> -5
                    "HARD" -> 5
                    else -> 0
                }

                val questionCount = dbQuery {
                    SyllabusQuizQuestionsTable.selectAll().where {
                        SyllabusQuizQuestionsTable.quizId eq quizId
                    }.count().toInt()
                }

                val generatedQuestions = SyllabusAiService.generateQuiz(
                    topicTitles = unitTitles,
                    classLevel = asg.className,
                    subject = asg.subject,
                    questionTypes = qTypes,
                    questionCount = questionCount,
                    difficultyOffset = difficultyOffset,
                    schoolId = ctx.schoolId,
                )

                if (generatedQuestions == null || generatedQuestions.isEmpty()) {
                    call.fail("AI is currently unavailable. Please try again.", HttpStatusCode.ServiceUnavailable, "AI_UNAVAILABLE"); return@post
                }

                val now = Instant.now()
                dbQuery {
                    SyllabusQuizQuestionsTable.deleteWhere { SyllabusQuizQuestionsTable.quizId eq quizId }
                    generatedQuestions.forEachIndexed { idx, q ->
                        val qId = UUID.randomUUID()
                        val optionsJson = quizJson.encodeToString(ListSerializer(serializer<String>()), q.options)
                        SyllabusQuizQuestionsTable.insert {
                            it[SyllabusQuizQuestionsTable.id] = qId
                            it[SyllabusQuizQuestionsTable.quizId] = quizId
                            it[SyllabusQuizQuestionsTable.questionType] = q.questionType
                            it[SyllabusQuizQuestionsTable.questionText] = q.questionText
                            it[SyllabusQuizQuestionsTable.optionsJson] = optionsJson
                            it[SyllabusQuizQuestionsTable.correctAnswer] = q.correctAnswer
                            it[SyllabusQuizQuestionsTable.explanation] = q.explanation
                            it[SyllabusQuizQuestionsTable.matchPairsJson] = "[]"
                            it[SyllabusQuizQuestionsTable.position] = idx
                            it[SyllabusQuizQuestionsTable.createdAt] = now
                        }
                    }
                }

                val questions = generatedQuestions.mapIndexed { idx, q ->
                    QuizQuestionSer(
                        id = UUID.randomUUID().toString(),
                        question = q.questionText,
                        options = q.options,
                        correctIndex = q.options.indexOfFirst { it.startsWith(q.correctAnswer) }.takeIf { it >= 0 } ?: 0,
                        explanation = q.explanation,
                        questionType = q.questionType,
                        correctAnswer = q.correctAnswer,
                    )
                }

                call.ok(QuizSer(
                    id = quizId.toString(),
                    assignmentId = asg.assignmentId.toString(),
                    unitIds = unitIds,
                    title = quizRow[SyllabusQuizzesTable.title],
                    questions = questions,
                    status = "DRAFT",
                    questionTypes = qTypes,
                    createdAt = quizRow[SyllabusQuizzesTable.createdAt]?.toString(),
                ))
            }

            // GET /{id}/results — quiz results
            get("/{id}/results") {
                val ctx = call.requireTeacherContext() ?: return@get
                val quizIdStr = call.parameters["id"]
                if (quizIdStr.isNullOrBlank()) {
                    call.fail("Quiz ID is required", HttpStatusCode.BadRequest, "MISSING_PARAM"); return@get
                }
                val quizId = UUID.fromString(quizIdStr)

                val quizRow = dbQuery {
                    SyllabusQuizzesTable.selectAll().where {
                        (SyllabusQuizzesTable.id eq quizId) and
                            (SyllabusQuizzesTable.schoolId eq ctx.schoolId)
                    }.singleOrNull()
                }

                if (quizRow == null) {
                    call.fail("Quiz not found", HttpStatusCode.NotFound, "QUIZ_NOT_FOUND"); return@get
                }

                // Return quiz with results summary
                val questions = dbQuery {
                    SyllabusQuizQuestionsTable.selectAll().where {
                        SyllabusQuizQuestionsTable.quizId eq quizId
                    }.orderBy(SyllabusQuizQuestionsTable.position, SortOrder.ASC).toList()
                }

                val answers = dbQuery {
                    SyllabusQuizAnswersTable.selectAll().where {
                        SyllabusQuizAnswersTable.quizId eq quizId
                    }.toList()
                }

                val totalStudents = answers.map { it[SyllabusQuizAnswersTable.studentId] }.distinct().size
                val correctCount = answers.count { it[SyllabusQuizAnswersTable.isCorrect] }

                call.ok(QuizListDataSer(quizzes = listOf(
                    QuizSer(
                        id = quizId.toString(),
                        assignmentId = quizRow[SyllabusQuizzesTable.assignmentId].toString(),
                        unitIds = quizRow[SyllabusQuizzesTable.unitIds].split(",").filter { it.isNotBlank() },
                        title = quizRow[SyllabusQuizzesTable.title],
                        questions = questions.map { qr ->
                            val opts = runCatching {
                                quizJson.decodeFromString(
                                    ListSerializer(serializer<String>()),
                                    qr[SyllabusQuizQuestionsTable.optionsJson]
                                )
                            }.getOrDefault(emptyList())
                            QuizQuestionSer(
                                id = qr[SyllabusQuizQuestionsTable.id].value.toString(),
                                question = qr[SyllabusQuizQuestionsTable.questionText],
                                options = opts,
                                questionType = qr[SyllabusQuizQuestionsTable.questionType],
                                correctAnswer = qr[SyllabusQuizQuestionsTable.correctAnswer],
                                explanation = qr[SyllabusQuizQuestionsTable.explanation],
                            )
                        },
                        status = quizRow[SyllabusQuizzesTable.status],
                        questionTypes = quizRow[SyllabusQuizzesTable.questionTypes].split(",").filter { it.isNotBlank() },
                        createdAt = quizRow[SyllabusQuizzesTable.createdAt]?.toString(),
                    )
                )))
            }
        }
    }
}
