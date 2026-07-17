/*
 * File: ComboService.kt
 * Module: feature/gamification
 *
 * Tracks consecutive activity streaks and computes combo multipliers.
 * Combo types: HOMEWORK, ATTENDANCE, STUDY, READING
 *
 * Rules (per spec §11):
 *   - Combo resets on break (missed day, late submission, absent)
 *   - Combo is per-type (homework combo doesn't affect attendance XP)
 *   - Combo multiplier applied AFTER all other multipliers
 *   - Silent — no notifications on combo increase or break
 *
 * Spec ref: GAMIFICATION_SYSTEM_SPEC.md §11
 */
package com.littlebridge.enrollplus.feature.gamification

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.GameStudentCombosTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

@Serializable
data class ComboDto(
    val comboType: String,
    val streakCount: Int,
    val multiplier: Float,
    val isActive: Boolean
)

@Serializable
data class ComboStatusDto(
    val combos: List<ComboDto>,
    val bestStreak: Int,
    val activeMultiplier: Float
)

object ComboService {

    // Combo thresholds and multipliers per type
    private val comboRules = mapOf(
        "HOMEWORK" to ComboRule(threshold = 3, multiplier = 1.5f),
        "STUDY" to ComboRule(threshold = 3, multiplier = 1.5f),
        "READING" to ComboRule(threshold = 3, multiplier = 1.5f),
        "ATTENDANCE" to ComboRule(threshold = 5, multiplier = 1.1f) // attendance has tiers
    )

    // Attendance tiered multipliers
    private val attendanceTiers = listOf(
        5 to 1.1f,
        10 to 1.25f,
        20 to 1.5f
    )

    private data class ComboRule(val threshold: Int, val multiplier: Float)

    /**
     * Records a combo event for a student. Increments streak if consecutive,
     * resets to 1 if broken. Returns the current multiplier after this event.
     */
    suspend fun recordComboEvent(
        studentId: UUID,
        schoolId: UUID,
        comboType: String,
        eventDate: LocalDate = LocalDate.now()
    ): Float = dbQuery {
        val existing = GameStudentCombosTable.selectAll()
            .where {
                (GameStudentCombosTable.studentId eq studentId) and
                (GameStudentCombosTable.comboType eq comboType)
            }
            .firstOrNull()

        val now = Instant.now()
        val today = eventDate.atStartOfDay(ZoneId.systemDefault()).toInstant()

        if (existing == null) {
            // First ever event of this type
            GameStudentCombosTable.insert {
                it[GameStudentCombosTable.studentId] = studentId
                it[GameStudentCombosTable.schoolId] = schoolId
                it[GameStudentCombosTable.comboType] = comboType
                it[GameStudentCombosTable.streakCount] = 1
                it[GameStudentCombosTable.lastEventAt] = now
                it[GameStudentCombosTable.isActive] = true
                it[GameStudentCombosTable.createdAt] = now
                it[GameStudentCombosTable.updatedAt] = now
            }
            return@dbQuery getMultiplierForType(comboType, 1)
        }

        val currentStreak = existing[GameStudentCombosTable.streakCount]
        val lastEvent = existing[GameStudentCombosTable.lastEventAt]
        val lastEventDate = lastEvent.atZone(ZoneId.systemDefault()).toLocalDate()

        val newStreak = when {
            // Same day — don't increment, keep current
            lastEventDate == eventDate -> currentStreak
            // Consecutive day — increment
            lastEventDate.plusDays(1) == eventDate -> currentStreak + 1
            // Broken streak — reset to 1
            else -> 1
        }

        GameStudentCombosTable.update({
            (GameStudentCombosTable.studentId eq studentId) and
            (GameStudentCombosTable.comboType eq comboType)
        }) {
            it[GameStudentCombosTable.streakCount] = newStreak
            it[GameStudentCombosTable.lastEventAt] = now
            it[GameStudentCombosTable.updatedAt] = now
        }

        getMultiplierForType(comboType, newStreak)
    }

    /**
     * Resets a combo streak to 0 (e.g. late submission, absence).
     */
    suspend fun resetCombo(studentId: UUID, comboType: String): Boolean = dbQuery {
        GameStudentCombosTable.update({
            (GameStudentCombosTable.studentId eq studentId) and
            (GameStudentCombosTable.comboType eq comboType)
        }) {
            it[GameStudentCombosTable.streakCount] = 0
            it[GameStudentCombosTable.isActive] = false
            it[GameStudentCombosTable.updatedAt] = Instant.now()
        } > 0
    }

    /**
     * Gets the current combo multiplier for a student and combo type.
     */
    suspend fun getComboMultiplier(studentId: UUID, comboType: String): Float = dbQuery {
        val row = GameStudentCombosTable.selectAll()
            .where {
                (GameStudentCombosTable.studentId eq studentId) and
                (GameStudentCombosTable.comboType eq comboType)
            }
            .firstOrNull() ?: return@dbQuery 1.0f

        getMultiplierForType(comboType, row[GameStudentCombosTable.streakCount])
    }

    /**
     * Gets all combo statuses for a student.
     */
    suspend fun getStudentCombos(studentId: UUID): ComboStatusDto = dbQuery {
        val rows = GameStudentCombosTable.selectAll()
            .where { GameStudentCombosTable.studentId eq studentId }
            .toList()

        val combos = rows.map { row ->
            val type = row[GameStudentCombosTable.comboType]
            val streak = row[GameStudentCombosTable.streakCount]
            ComboDto(
                comboType = type,
                streakCount = streak,
                multiplier = getMultiplierForType(type, streak),
                isActive = row[GameStudentCombosTable.isActive]
            )
        }

        val bestStreak = combos.maxOfOrNull { it.streakCount } ?: 0
        val activeMult = combos.filter { it.multiplier > 1.0f }.maxOfOrNull { it.multiplier } ?: 1.0f

        ComboStatusDto(combos = combos, bestStreak = bestStreak, activeMultiplier = activeMult)
    }

    /**
     * Computes the multiplier for a given combo type and streak count.
     */
    private fun getMultiplierForType(comboType: String, streak: Int): Float {
        if (streak <= 0) return 1.0f

        return when (comboType) {
            "ATTENDANCE" -> {
                // Tiered: 5=x1.1, 10=x1.25, 20=x1.5
                attendanceTiers.filter { streak >= it.first }
                    .maxOfOrNull { it.second } ?: 1.0f
            }
            else -> {
                // Threshold-based: reach threshold → multiplier applies
                val rule = comboRules[comboType] ?: return 1.0f
                if (streak >= rule.threshold) rule.multiplier else 1.0f
            }
        }
    }
}
