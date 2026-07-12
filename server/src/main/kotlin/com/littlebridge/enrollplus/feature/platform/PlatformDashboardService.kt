package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformFeaturesTable
import com.littlebridge.enrollplus.db.PlatformTestCasesTable
import com.littlebridge.enrollplus.db.PlatformBugsTable
import com.littlebridge.enrollplus.db.PlatformAuditLogTable
import com.littlebridge.enrollplus.db.PlatformDiscoveredApisTable
import com.littlebridge.enrollplus.db.AppUsersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant

object PlatformDashboardService {

    suspend fun health(): DashboardHealthDto = dbQuery {
        val totalFeatures = PlatformFeaturesTable.selectAll()
            .where { PlatformFeaturesTable.isArchived eq false }.count()
        val totalTestCases = PlatformTestCasesTable.selectAll().count()
        val totalBugs = PlatformBugsTable.selectAll().count()
        val openBugs = PlatformBugsTable.selectAll()
            .where { PlatformBugsTable.status notInList listOf("closed", "verified") }.count()

        val completedFeatures = PlatformFeaturesTable.selectAll()
            .where { (PlatformFeaturesTable.isArchived eq false) and (PlatformFeaturesTable.status eq "complete") }.count()
        val featureCompletion = if (totalFeatures > 0) completedFeatures.toDouble() / totalFeatures * 100 else 0.0

        val passedTests = PlatformTestCasesTable.selectAll().where { PlatformTestCasesTable.status eq "passed" }.count()
        val testingProgress = if (totalTestCases > 0) passedTests.toDouble() / totalTestCases * 100 else 0.0

        val criticalBugs = PlatformBugsTable.selectAll()
            .where { (PlatformBugsTable.severity eq "critical") and (PlatformBugsTable.status notInList listOf("closed", "verified")) }.count()
        val highBugs = PlatformBugsTable.selectAll()
            .where { (PlatformBugsTable.severity eq "major") and (PlatformBugsTable.status notInList listOf("closed", "verified")) }.count()
        val mediumBugs = PlatformBugsTable.selectAll()
            .where { (PlatformBugsTable.severity eq "normal") and (PlatformBugsTable.status notInList listOf("closed", "verified")) }.count()
        val bugHealth = maxOf(0.0, 100.0 - criticalBugs * 10 - highBugs * 3 - mediumBugs * 1)

        val releaseFeatures = PlatformFeaturesTable.selectAll()
            .where { (PlatformFeaturesTable.isArchived eq false) and (PlatformFeaturesTable.targetRelease.isNotNull()) }.count()
        val releaseReady = PlatformFeaturesTable.selectAll()
            .where { (PlatformFeaturesTable.isArchived eq false) and (PlatformFeaturesTable.targetRelease.isNotNull()) and (PlatformFeaturesTable.status eq "complete") }.count()
        val releaseReadiness = if (releaseFeatures > 0) releaseReady.toDouble() / releaseFeatures * 100 else 0.0

        val overallScore = featureCompletion * 0.35 + testingProgress * 0.30 + releaseReadiness * 0.20 + bugHealth * 0.15

        val bugDensity = if (totalFeatures > 0) {
            val ratio = openBugs.toDouble() / totalFeatures
            when { ratio < 0.5 -> "low"; ratio < 1.5 -> "medium"; else -> "high" }
        } else "low"

        val failedTests = PlatformTestCasesTable.selectAll().where { PlatformTestCasesTable.status eq "failed" }.count()
        val regressionRisk = if (totalTestCases > 0) {
            val ratio = failedTests.toDouble() / totalTestCases
            when { ratio < 0.05 -> "low"; ratio < 0.15 -> "medium"; else -> "high" }
        } else "low"

        DashboardHealthDto(
            overall_score = Math.round(overallScore * 100.0) / 100.0,
            feature_completion = Math.round(featureCompletion * 100.0) / 100.0,
            testing_progress = Math.round(testingProgress * 100.0) / 100.0,
            release_readiness = Math.round(releaseReadiness * 100.0) / 100.0,
            bug_health = Math.round(bugHealth * 100.0) / 100.0,
            bug_density = bugDensity,
            regression_risk = regressionRisk,
            total_features = totalFeatures.toInt(),
            total_test_cases = totalTestCases.toInt(),
            total_bugs = totalBugs.toInt(),
            open_bugs = openBugs.toInt(),
        )
    }

    suspend fun featuresByStatus(): List<ChartDatumDto> = dbQuery {
        PlatformFeaturesTable.slice(PlatformFeaturesTable.status, PlatformFeaturesTable.id.count())
            .selectAll()
            .where { PlatformFeaturesTable.isArchived eq false }
            .groupBy(PlatformFeaturesTable.status)
            .map { row ->
                ChartDatumDto(
                    label = row[PlatformFeaturesTable.status],
                    value = row[PlatformFeaturesTable.id.count()],
                )
            }
    }

    suspend fun featuresByPriority(): List<ChartDatumDto> = dbQuery {
        PlatformFeaturesTable.slice(PlatformFeaturesTable.priority, PlatformFeaturesTable.id.count())
            .selectAll()
            .where { PlatformFeaturesTable.isArchived eq false }
            .groupBy(PlatformFeaturesTable.priority)
            .map { row ->
                ChartDatumDto(
                    label = row[PlatformFeaturesTable.priority],
                    value = row[PlatformFeaturesTable.id.count()],
                )
            }
    }

    suspend fun featuresByTeam(): List<ChartDatumDto> = dbQuery {
        PlatformFeaturesTable.slice(PlatformFeaturesTable.team, PlatformFeaturesTable.id.count())
            .selectAll()
            .where { (PlatformFeaturesTable.isArchived eq false) and (PlatformFeaturesTable.team.isNotNull()) }
            .groupBy(PlatformFeaturesTable.team)
            .map { row ->
                ChartDatumDto(
                    label = row[PlatformFeaturesTable.team] ?: "Unassigned",
                    value = row[PlatformFeaturesTable.id.count()],
                )
            }
    }

    suspend fun testingProgress(): TestingProgressDto = dbQuery {
        TestingProgressDto(
            passed = PlatformTestCasesTable.selectAll().where { PlatformTestCasesTable.status eq "passed" }.count(),
            failed = PlatformTestCasesTable.selectAll().where { PlatformTestCasesTable.status eq "failed" }.count(),
            pending = PlatformTestCasesTable.selectAll().where { PlatformTestCasesTable.status eq "not_run" }.count(),
            blocked = PlatformTestCasesTable.selectAll().where { PlatformTestCasesTable.status eq "blocked" }.count(),
            need_retest = PlatformTestCasesTable.selectAll().where { PlatformTestCasesTable.status eq "need_retest" }.count(),
            in_progress = PlatformTestCasesTable.selectAll().where { PlatformTestCasesTable.status eq "in_progress" }.count(),
        )
    }

    suspend fun bugSummary(): BugSummaryBySeverityDto = dbQuery {
        BugSummaryBySeverityDto(
            critical = PlatformBugsTable.selectAll().where { (PlatformBugsTable.severity eq "critical") and (PlatformBugsTable.status notInList listOf("closed", "verified")) }.count(),
            major = PlatformBugsTable.selectAll().where { (PlatformBugsTable.severity eq "major") and (PlatformBugsTable.status notInList listOf("closed", "verified")) }.count(),
            normal = PlatformBugsTable.selectAll().where { (PlatformBugsTable.severity eq "normal") and (PlatformBugsTable.status notInList listOf("closed", "verified")) }.count(),
            minor = PlatformBugsTable.selectAll().where { (PlatformBugsTable.severity eq "minor") and (PlatformBugsTable.status notInList listOf("closed", "verified")) }.count(),
            cosmetic = PlatformBugsTable.selectAll().where { (PlatformBugsTable.severity eq "cosmetic") and (PlatformBugsTable.status notInList listOf("closed", "verified")) }.count(),
        )
    }

    suspend fun recentActivity(): List<RecentActivityDto> = dbQuery {
        PlatformAuditLogTable.selectAll()
            .orderBy(PlatformAuditLogTable.createdAt, SortOrder.DESC)
            .limit(50)
            .map { row ->
                val actorName = row[PlatformAuditLogTable.actorId]?.let { aid ->
                    AppUsersTable.selectAll().where { AppUsersTable.id eq aid }
                        .singleOrNull()?.get(AppUsersTable.fullName)
                }
                RecentActivityDto(
                    id = row[PlatformAuditLogTable.id].value.toString(),
                    actor_id = row[PlatformAuditLogTable.actorId]?.toString(),
                    actor_name = actorName,
                    action = row[PlatformAuditLogTable.action],
                    entity_type = row[PlatformAuditLogTable.entityType],
                    entity_id = row[PlatformAuditLogTable.entityId]?.toString(),
                    created_at = row[PlatformAuditLogTable.createdAt].toString(),
                )
            }
    }

    suspend fun upcomingReleases(): List<UpcomingReleaseDto> = dbQuery {
        PlatformFeaturesTable.selectAll()
            .where { (PlatformFeaturesTable.isArchived eq false) and (PlatformFeaturesTable.targetRelease.isNotNull()) }
            .orderBy(PlatformFeaturesTable.targetRelease, SortOrder.ASC)
            .map { it.toFeatureDto() }
            .groupBy { it.target_release ?: "Unspecified" }
            .map { (release, features) -> UpcomingReleaseDto(release, features) }
    }

    suspend fun riskIndicators(): RiskIndicatorDto = dbQuery {
        val blockedFeatures = PlatformFeaturesTable.selectAll()
            .where { (PlatformFeaturesTable.isArchived eq false) and (PlatformFeaturesTable.status eq "blocked") }.count()
        val criticalBugs = PlatformBugsTable.selectAll()
            .where { (PlatformBugsTable.severity eq "critical") and (PlatformBugsTable.status notInList listOf("closed", "verified")) }.count()
        val slaBreaches = PlatformBugsTable.selectAll()
            .where { (PlatformBugsTable.slaDueAt less Instant.now()) and (PlatformBugsTable.status notInList listOf("closed", "verified")) }.count()
        val highRiskFeatures = PlatformFeaturesTable.selectAll()
            .where { (PlatformFeaturesTable.isArchived eq false) and (PlatformFeaturesTable.riskLevel eq "high") }.count()
        val apisDown = PlatformDiscoveredApisTable.selectAll()
            .where { (PlatformDiscoveredApisTable.isAlive eq false) and PlatformDiscoveredApisTable.lastCheckedAt.isNotNull() }.count()
        RiskIndicatorDto(blockedFeatures, criticalBugs, slaBreaches, highRiskFeatures, apisDown)
    }
}
