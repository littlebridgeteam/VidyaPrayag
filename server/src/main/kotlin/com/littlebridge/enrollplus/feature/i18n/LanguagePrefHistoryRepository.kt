/*
 * File: LanguagePrefHistoryRepository.kt
 * Module: feature.i18n
 *
 * Append-only audit trail for language preference changes.
 * Every PATCH /api/v1/user/language-pref records a row here.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §8.4
 */
package com.littlebridge.enrollplus.feature.i18n

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.LanguagePrefHistoryTable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Serializable
data class LanguagePrefHistoryEntry(
    @SerialName("old_lang") val oldLang: String?,
    @SerialName("new_lang") val newLang: String,
    @SerialName("changed_at") val changedAt: String,
    val source: String,
)

object LanguagePrefHistoryRepository {

    suspend fun record(
        userId: UUID,
        schoolId: UUID?,
        oldLang: String?,
        newLang: String,
        source: String = "app",
    ) {
        dbQuery {
            LanguagePrefHistoryTable.insert {
                it[LanguagePrefHistoryTable.userId] = userId
                it[LanguagePrefHistoryTable.schoolId] = schoolId
                it[LanguagePrefHistoryTable.oldLang] = oldLang
                it[LanguagePrefHistoryTable.newLang] = newLang
                it[LanguagePrefHistoryTable.changedAt] = Instant.now()
                it[LanguagePrefHistoryTable.changeSource] = source
            }
        }
    }

    suspend fun getUserHistory(userId: UUID, limit: Int = 50): List<LanguagePrefHistoryEntry> {
        return dbQuery {
            LanguagePrefHistoryTable
                .select { LanguagePrefHistoryTable.userId eq userId }
                .orderBy(LanguagePrefHistoryTable.changedAt, SortOrder.DESC)
                .limit(limit)
                .map {
                    LanguagePrefHistoryEntry(
                        oldLang = it[LanguagePrefHistoryTable.oldLang],
                        newLang = it[LanguagePrefHistoryTable.newLang],
                        changedAt = it[LanguagePrefHistoryTable.changedAt].toString(),
                        source = it[LanguagePrefHistoryTable.changeSource],
                    )
                }
        }
    }

    suspend fun getSchoolSwitchCount(schoolId: UUID, days: Int = 7): Int {
        val cutoff = Instant.now().minus(days.toLong(), ChronoUnit.DAYS)
        return dbQuery {
            LanguagePrefHistoryTable
                .select {
                    (LanguagePrefHistoryTable.schoolId eq schoolId) and
                        (LanguagePrefHistoryTable.changedAt greater cutoff) and
                        (LanguagePrefHistoryTable.changeSource neq "migration")
                }
                .count().toInt()
        }
    }
}
