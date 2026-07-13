/*
 * File: NotificationsRouting.kt
 * Module: feature.notifications
 *
 * Role-aware notification inbox (RA-41/42/46/50/42). Replaces the parent-only
 * synth endpoint that broke for admins/teachers (they hit /parent/notifications
 * with a non-parent token and always got empty).
 *
 *   GET   /api/v1/notifications          — real rows for jwt.sub (any role),
 *                                           merged with a parent-only synth
 *                                           bridge (announcements + DUE/OVERDUE
 *                                           fees) so existing parent value is
 *                                           preserved while real notifications
 *                                           accumulate.
 *   GET   /api/v1/notifications/summary  — unread count for the bell.
 *   PATCH /api/v1/notifications/{id}/read— mark one read (persisted — RA-46).
 *   POST  /api/v1/notifications/read-all — mark all read (persisted — RA-46).
 *
 * Everything is recipient-scoped (user_id = jwt.sub). Synthesised parent items
 * carry a stable id ("ann_…"/"fee_…") and are NOT persisted; only real
 * NotificationsTable rows are markable.
 */
package com.littlebridge.enrollplus.feature.notifications

import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.okMessage
import com.littlebridge.enrollplus.core.principalUserUuid
import com.littlebridge.enrollplus.db.AnnouncementsTable
import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.FeeRecordsTable
import com.littlebridge.enrollplus.db.NotificationsTable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.util.UUID

@Serializable
data class NotificationDto(
    val id: String,
    val category: String,
    val title: String,
    val body: String,
    val time: String,
    val unread: Boolean = true,
    @SerialName("deep_link") val deepLink: String? = null,
    @SerialName("ref_type") val refType: String? = null,
    @SerialName("ref_id") val refId: String? = null,
)

@Serializable
data class NotificationsDataDto(
    val notifications: List<NotificationDto>,
    @SerialName("unread_count") val unreadCount: Int
)

@Serializable
data class NotificationsSummaryDto(
    @SerialName("unread_count") val unreadCount: Int
)

fun Route.notificationsRouting() {
    authenticate("jwt") {
        route("/api/v1/notifications") {

            // -------- list (role-aware) --------
            get {
                val uid = call.principalUserUuid() ?: run {
                    call.respond(HttpStatusCode.Unauthorized); return@get
                }

                val data = dbQuery {
                    val items = mutableListOf<Pair<Instant, NotificationDto>>()

                    // 1) Real notification rows for this recipient (any role).
                    NotificationsTable.selectAll()
                        .where { (NotificationsTable.userId eq uid) and (NotificationsTable.archivedAt.isNull()) }
                        .orderBy(NotificationsTable.createdAt, SortOrder.DESC)
                        .limit(200)
                        .forEach { row ->
                            items += row[NotificationsTable.createdAt] to NotificationDto(
                                id = row[NotificationsTable.id].value.toString(),
                                category = row[NotificationsTable.category],
                                title = row[NotificationsTable.title],
                                body = row[NotificationsTable.body],
                                time = row[NotificationsTable.createdAt].toString(),
                                unread = !row[NotificationsTable.isRead],
                                deepLink = row[NotificationsTable.deepLink],
                                refType = row[NotificationsTable.refType],
                                refId = row[NotificationsTable.refId],
                            )
                        }

                    // 2) Parent-only synth BRIDGE (announcements + outstanding
                    //    fees). Kept so a parent who has no real rows yet still
                    //    sees actionable items; non-parents get only real rows.
                    //    Synth items the user has dismissed (via Clear All) are
                    //    tracked as tombstone rows with archivedAt set and filtered out.
                    val dismissedKeys = NotificationsTable.selectAll()
                        .where { (NotificationsTable.userId eq uid) and (NotificationsTable.archivedAt.isNotNull()) }
                        .mapNotNull { row ->
                            val rt = row[NotificationsTable.refType]
                            val ri = row[NotificationsTable.refId]
                            if (rt != null && ri != null) rt to ri else null
                        }
                        .toSet()

                    val role = AppUsersTable.selectAll()
                        .where { AppUsersTable.id eq uid }
                        .singleOrNull()?.get(AppUsersTable.role) ?: "parent"

                    if (role == "parent") {
                        val schoolIds = ChildrenTable.selectAll()
                            .where { (ChildrenTable.parentId eq uid) and (ChildrenTable.isActive eq true) }
                            .mapNotNull { it[ChildrenTable.schoolId] }
                            .distinct()

                        if (schoolIds.isNotEmpty()) {
                            val schoolFilter = schoolIds
                                .map { sid -> AnnouncementsTable.schoolId eq sid }
                                .reduce { acc, op -> acc or op }
                            AnnouncementsTable.selectAll()
                                .where { schoolFilter }
                                .orderBy(AnnouncementsTable.createdAt, SortOrder.DESC)
                                .forEach { row ->
                                    val annId = row[AnnouncementsTable.id].value.toString()
                                    if ("announcement" to annId in dismissedKeys) return@forEach
                                    items += row[AnnouncementsTable.createdAt] to NotificationDto(
                                        id = "ann_" + annId,
                                        category = "announcement",
                                        title = row[AnnouncementsTable.title],
                                        body = row[AnnouncementsTable.description],
                                        time = row[AnnouncementsTable.date],
                                        unread = true,
                                        deepLink = "/parent/announcements/" + row[AnnouncementsTable.id].value.toString(),
                                        refType = "announcement",
                                        refId = row[AnnouncementsTable.id].value.toString(),
                                    )
                                }
                        }

                        FeeRecordsTable.selectAll()
                            .where {
                                (FeeRecordsTable.parentId eq uid) and
                                    ((FeeRecordsTable.status eq "DUE") or (FeeRecordsTable.status eq "OVERDUE"))
                            }
                            .orderBy(FeeRecordsTable.updatedAt, SortOrder.DESC)
                            .forEach { row ->
                                val feeId = row[FeeRecordsTable.id].value.toString()
                                if ("fee_record" to feeId in dismissedKeys) return@forEach
                                val overdue = row[FeeRecordsTable.status].equals("OVERDUE", ignoreCase = true)
                                val currency = row[FeeRecordsTable.currency]
                                val amount = row[FeeRecordsTable.amount]
                                val due = row[FeeRecordsTable.dueDate]
                                items += row[FeeRecordsTable.updatedAt] to NotificationDto(
                                    id = "fee_" + row[FeeRecordsTable.id].value.toString(),
                                    category = "fees",
                                    title = if (overdue) "Overdue: ${row[FeeRecordsTable.title]}" else "Fee due: ${row[FeeRecordsTable.title]}",
                                    body = buildString {
                                        append("$currency ")
                                        append(if (amount % 1.0 == 0.0) amount.toLong().toString() else amount.toString())
                                        if (!due.isNullOrBlank()) append(" • due $due")
                                    },
                                    time = due ?: "",
                                    unread = true,
                                    deepLink = "/parent/fees/" + row[FeeRecordsTable.id].value.toString(),
                                    refType = "fee_record",
                                    refId = row[FeeRecordsTable.id].value.toString(),
                                )
                            }
                    }

                    val ordered = items.sortedByDescending { it.first }.map { it.second }
                    NotificationsDataDto(
                        notifications = ordered,
                        unreadCount = ordered.count { it.unread },
                    )
                }
                call.ok(data, message = "Notifications fetched")
            }

            // -------- summary (unread count for the bell) --------
            get("/summary") {
                val uid = call.principalUserUuid() ?: run {
                    call.respond(HttpStatusCode.Unauthorized); return@get
                }
                val count = dbQuery {
                    var c = NotificationsTable.selectAll()
                        .where { (NotificationsTable.userId eq uid) and (NotificationsTable.isRead eq false) and (NotificationsTable.archivedAt.isNull()) }
                        .count().toInt()

                    // Bridge: include the parent synth unread items so the bell
                    // matches the list during the migration window.
                    val role = AppUsersTable.selectAll()
                        .where { AppUsersTable.id eq uid }
                        .singleOrNull()?.get(AppUsersTable.role) ?: "parent"
                    if (role == "parent") {
                        val dismissedKeys = NotificationsTable.selectAll()
                            .where { (NotificationsTable.userId eq uid) and (NotificationsTable.archivedAt.isNotNull()) }
                            .mapNotNull { row ->
                                val rt = row[NotificationsTable.refType]
                                val ri = row[NotificationsTable.refId]
                                if (rt != null && ri != null) rt to ri else null
                            }
                            .toSet()

                        val schoolIds = ChildrenTable.selectAll()
                            .where { (ChildrenTable.parentId eq uid) and (ChildrenTable.isActive eq true) }
                            .mapNotNull { it[ChildrenTable.schoolId] }
                            .distinct()
                        if (schoolIds.isNotEmpty()) {
                            val schoolFilter = schoolIds
                                .map { sid -> AnnouncementsTable.schoolId eq sid }
                                .reduce { acc, op -> acc or op }
                            c += AnnouncementsTable.selectAll().where { schoolFilter }
                                .map { row -> row[AnnouncementsTable.id].value.toString() }
                                .count { id -> ("announcement" to id) !in dismissedKeys }
                        }
                        c += FeeRecordsTable.selectAll()
                            .where {
                                (FeeRecordsTable.parentId eq uid) and
                                    ((FeeRecordsTable.status eq "DUE") or (FeeRecordsTable.status eq "OVERDUE"))
                            }
                            .map { row -> row[FeeRecordsTable.id].value.toString() }
                            .count { id -> ("fee_record" to id) !in dismissedKeys }
                    }
                    c
                }
                call.ok(NotificationsSummaryDto(unreadCount = count), message = "OK")
            }

            // -------- mark one read (RA-46) --------
            patch("/{id}/read") {
                val uid = call.principalUserUuid() ?: run {
                    call.respond(HttpStatusCode.Unauthorized); return@patch
                }
                val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.respond(HttpStatusCode.BadRequest); return@patch }
                val now = Instant.now()
                dbQuery {
                    NotificationsTable.update({
                        (NotificationsTable.id eq id) and (NotificationsTable.userId eq uid)
                    }) {
                        it[isRead] = true
                        it[readAt] = now
                    }
                }
                call.okMessage("Marked read")
            }

            // -------- mark all read (RA-46) --------
            post("/read-all") {
                val uid = call.principalUserUuid() ?: run {
                    call.respond(HttpStatusCode.Unauthorized); return@post
                }
                val now = Instant.now()
                dbQuery {
                    NotificationsTable.update({
                        (NotificationsTable.userId eq uid) and (NotificationsTable.isRead eq false)
                    }) {
                        it[isRead] = true
                        it[readAt] = now
                    }
                }
                call.okMessage("All marked read")
            }

            // -------- mark by ref (for push tap auto-read) --------
            post("/mark-by-ref") {
                val uid = call.principalUserUuid() ?: run {
                    call.respond(HttpStatusCode.Unauthorized); return@post
                }
                val refType = call.request.queryParameters["refType"]
                val refId = call.request.queryParameters["refId"]
                if (refType.isNullOrBlank() || refId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest); return@post
                }
                val now = Instant.now()
                dbQuery {
                    NotificationsTable.update({
                        (NotificationsTable.userId eq uid) and
                            (NotificationsTable.refType eq refType) and
                            (NotificationsTable.refId eq refId)
                    }) {
                        it[isRead] = true
                        it[readAt] = now
                    }
                }
                call.okMessage("Marked read by ref")
            }

            // -------- clear all read notifications --------
            delete("/clear-all") {
                val uid = call.principalUserUuid() ?: run {
                    call.respond(HttpStatusCode.Unauthorized); return@delete
                }
                dbQuery {
                    NotificationsTable.deleteWhere {
                        (NotificationsTable.userId eq uid) and (NotificationsTable.isRead eq true) and (NotificationsTable.archivedAt.isNull())
                    }
                }
                call.okMessage("Cleared read notifications")
            }

            // -------- clear every notification --------
            delete("/all") {
                val uid = call.principalUserUuid() ?: run {
                    call.respond(HttpStatusCode.Unauthorized); return@delete
                }
                val now = Instant.now()
                dbQuery {
                    // Delete ALL rows for this user (real notifications + old tombstones).
                    NotificationsTable.deleteWhere {
                        NotificationsTable.userId eq uid
                    }

                    // Insert tombstones for parent synth items so they don't reappear on next fetch.
                    val role = AppUsersTable.selectAll()
                        .where { AppUsersTable.id eq uid }
                        .singleOrNull()?.get(AppUsersTable.role) ?: "parent"

                    if (role == "parent") {
                        val schoolIds = ChildrenTable.selectAll()
                            .where { (ChildrenTable.parentId eq uid) and (ChildrenTable.isActive eq true) }
                            .mapNotNull { it[ChildrenTable.schoolId] }
                            .distinct()

                        if (schoolIds.isNotEmpty()) {
                            val schoolFilter = schoolIds
                                .map { sid -> AnnouncementsTable.schoolId eq sid }
                                .reduce { acc, op -> acc or op }
                            AnnouncementsTable.selectAll()
                                .where { schoolFilter }
                                .forEach { row ->
                                    NotificationsTable.insert {
                                        it[NotificationsTable.userId] = uid
                                        it[NotificationsTable.category] = "announcement"
                                        it[NotificationsTable.title] = "[dismissed]"
                                        it[NotificationsTable.body] = ""
                                        it[NotificationsTable.refType] = "announcement"
                                        it[NotificationsTable.refId] = row[AnnouncementsTable.id].value.toString()
                                        it[NotificationsTable.isRead] = true
                                        it[NotificationsTable.createdAt] = now
                                        it[NotificationsTable.archivedAt] = now
                                    }
                                }
                        }

                        FeeRecordsTable.selectAll()
                            .where {
                                (FeeRecordsTable.parentId eq uid) and
                                    ((FeeRecordsTable.status eq "DUE") or (FeeRecordsTable.status eq "OVERDUE"))
                            }
                            .forEach { row ->
                                NotificationsTable.insert {
                                    it[NotificationsTable.userId] = uid
                                    it[NotificationsTable.category] = "fees"
                                    it[NotificationsTable.title] = "[dismissed]"
                                    it[NotificationsTable.body] = ""
                                    it[NotificationsTable.refType] = "fee_record"
                                    it[NotificationsTable.refId] = row[FeeRecordsTable.id].value.toString()
                                    it[NotificationsTable.isRead] = true
                                    it[NotificationsTable.createdAt] = now
                                    it[NotificationsTable.archivedAt] = now
                                }
                            }
                    }
                }
                call.okMessage("Cleared all notifications")
            }

        }
    }
}
