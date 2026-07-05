package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformFeatureFilesTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.io.File
import java.time.Instant
import java.util.UUID
import kotlinx.serialization.Serializable

/**
 * Git-based change tracking service.
 *
 * Uses `git log` to track file changes and link them to features.
 * For each file linked to a feature, fetches the last commit info (SHA, message, author, date).
 */
object GitChangeTrackingService {

    @Serializable
    data class ScanResult(
        val updated: Int,
        val errors: List<String>,
    )

    data class GitInfo(
        val commitSha: String,
        val commitMsg: String,
        val commitAuthor: String,
        val commitDate: Instant,
    )

    /**
     * Refresh git info for all tracked feature files.
     */
    suspend fun refreshAll(): ScanResult {
        val errors = mutableListOf<String>()
        var updated = 0

        val files = dbQuery {
            PlatformFeatureFilesTable.selectAll().map { row ->
                row[PlatformFeatureFilesTable.id].value to row[PlatformFeatureFilesTable.filePath]
            }
        }

        for ((id, filePath) in files) {
            try {
                val gitInfo = getGitInfo(filePath)
                if (gitInfo != null) {
                    dbQuery {
                        PlatformFeatureFilesTable.update(
                            where = { PlatformFeatureFilesTable.id eq id }
                        ) {
                            it[PlatformFeatureFilesTable.lastCommitSha] = gitInfo.commitSha
                            it[PlatformFeatureFilesTable.lastCommitMsg] = gitInfo.commitMsg
                            it[PlatformFeatureFilesTable.lastCommitAuthor] = gitInfo.commitAuthor
                            it[PlatformFeatureFilesTable.lastModifiedAt] = gitInfo.commitDate
                        }
                    }
                    updated++
                }
            } catch (e: Exception) {
                errors.add("Git info for $filePath: ${e.message}")
            }
        }

        return ScanResult(updated, errors)
    }

    /**
     * Get last commit info for a file using git log.
     */
    fun getGitInfo(filePath: String): GitInfo? {
        val file = File(filePath)
        if (!file.exists()) return null

        val gitDir = findGitRoot(file) ?: return null

        try {
            // Get last commit SHA
            val sha = runGitCommand(gitDir, listOf("log", "-1", "--format=%H", "--", filePath))?.trim()
                ?: return null

            // Get commit message (subject only)
            val msg = runGitCommand(gitDir, listOf("log", "-1", "--format=%s", "--", filePath))?.trim()
                ?: ""

            // Get author
            val author = runGitCommand(gitDir, listOf("log", "-1", "--format=%an", "--", filePath))?.trim()
                ?: ""

            // Get date (ISO format)
            val dateStr = runGitCommand(gitDir, listOf("log", "-1", "--format=%cI", "--", filePath))?.trim()
                ?: return null
            val date = Instant.parse(dateStr)

            return GitInfo(sha.take(12), msg, author, date)
        } catch (e: Exception) {
            return null
        }
    }

    private fun findGitRoot(file: File): File? {
        var dir = if (file.isDirectory) file else file.parentFile
        while (dir != null) {
            if (File(dir, ".git").exists()) return dir
            dir = dir.parentFile
        }
        return null
    }

    private fun runGitCommand(workingDir: File, args: List<String>): String? {
        return try {
            val process = ProcessBuilder(listOf("git") + args)
                .directory(workingDir)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            if (process.exitValue() == 0) output else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Link a file to a feature, fetching git info immediately.
     */
    suspend fun linkFile(featureId: UUID, filePath: String, fileType: String): UUID = dbQuery {
        val gitInfo = getGitInfo(filePath)
        PlatformFeatureFilesTable.insert {
            it[PlatformFeatureFilesTable.featureId] = featureId
            it[PlatformFeatureFilesTable.filePath] = filePath
            it[PlatformFeatureFilesTable.fileType] = fileType
            it[PlatformFeatureFilesTable.lastModifiedAt] = gitInfo?.commitDate
            it[PlatformFeatureFilesTable.lastCommitSha] = gitInfo?.commitSha
            it[PlatformFeatureFilesTable.lastCommitMsg] = gitInfo?.commitMsg
            it[PlatformFeatureFilesTable.lastCommitAuthor] = gitInfo?.commitAuthor
        }[PlatformFeatureFilesTable.id].value
    }

    suspend fun unlinkFile(fileId: UUID): Boolean = dbQuery {
        PlatformFeatureFilesTable.deleteWhere { PlatformFeatureFilesTable.id eq fileId } > 0
    }

    suspend fun listFiles(featureId: UUID): List<FeatureFileDto> = dbQuery {
        PlatformFeatureFilesTable.selectAll()
            .where { PlatformFeatureFilesTable.featureId eq featureId }
            .orderBy(PlatformFeatureFilesTable.filePath, SortOrder.ASC)
            .map { row ->
                FeatureFileDto(
                    id = row[PlatformFeatureFilesTable.id].value.toString(),
                    feature_id = featureId.toString(),
                    file_path = row[PlatformFeatureFilesTable.filePath],
                    file_type = row[PlatformFeatureFilesTable.fileType],
                    last_modified_at = row[PlatformFeatureFilesTable.lastModifiedAt]?.toString(),
                    last_commit_sha = row[PlatformFeatureFilesTable.lastCommitSha],
                    last_commit_msg = row[PlatformFeatureFilesTable.lastCommitMsg],
                    last_commit_author = row[PlatformFeatureFilesTable.lastCommitAuthor],
                )
            }
    }
}
