package com.littlebridge.enrollplus.feature.ai

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.NcertSyllabusReferenceTable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.slf4j.LoggerFactory
import java.time.Instant

object NcertReferenceService {
    private val log = LoggerFactory.getLogger("NcertReferenceService")
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Serializable
    data class NcertSubtopic(val title: String)

    @Serializable
    data class NcertTopic(
        val title: String,
        val subtopics: List<NcertSubtopic> = emptyList(),
    )

    @Serializable
    data class NcertChapter(
        val title: String,
        val topics: List<NcertTopic> = emptyList(),
    )

    @Serializable
    data class NcertSyllabus(
        val classLevel: String,
        val subjectName: String,
        val chapters: List<NcertChapter>,
    )

    suspend fun getSyllabus(classLevel: String, subjectName: String): NcertSyllabus? {
        ensureSeeded()
        val nc = normalizeClassLevel(classLevel)
        val ns = normalizeSubjectName(subjectName)
        val row = dbQuery {
            NcertSyllabusReferenceTable.selectAll().where {
                (NcertSyllabusReferenceTable.classLevel eq nc) and
                    (NcertSyllabusReferenceTable.subjectName eq ns)
            }.singleOrNull()
        } ?: return null
        val chapters = try {
            json.decodeFromString<List<NcertChapter>>(row[NcertSyllabusReferenceTable.chaptersJson])
        } catch (e: Exception) { emptyList() }
        return NcertSyllabus(nc, ns, chapters)
    }

    suspend fun hasReference(classLevel: String, subjectName: String): Boolean {
        ensureSeeded()
        val nc = normalizeClassLevel(classLevel)
        val ns = normalizeSubjectName(subjectName)
        return dbQuery {
            NcertSyllabusReferenceTable.selectAll().where {
                (NcertSyllabusReferenceTable.classLevel eq nc) and
                    (NcertSyllabusReferenceTable.subjectName eq ns)
            }.count()
        } > 0
    }

    suspend fun listAvailable(): List<Pair<String, String>> {
        ensureSeeded()
        return dbQuery {
            NcertSyllabusReferenceTable.selectAll()
                .map { it[NcertSyllabusReferenceTable.classLevel] to it[NcertSyllabusReferenceTable.subjectName] }
        }
    }

    private var seeded = false

    private suspend fun ensureSeeded() {
        if (seeded) return
        val count = dbQuery { NcertSyllabusReferenceTable.selectAll().count() }
        if (count > 0) { seeded = true; return }
        log.info("Seeding NCERT syllabus reference data...")
        val now = Instant.now()
        val allData = NcertReferenceData.DATA + NcertReferenceData2.DATA + NcertReferenceData3.DATA + NcertReferenceData4.DATA
        for (s in allData) {
            val cj = json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(NcertChapter.serializer()), s.chapters)
            dbQuery {
                NcertSyllabusReferenceTable.insert {
                    it[NcertSyllabusReferenceTable.classLevel] = s.classLevel
                    it[NcertSyllabusReferenceTable.subjectName] = s.subjectName
                    it[NcertSyllabusReferenceTable.chaptersJson] = cj
                    it[NcertSyllabusReferenceTable.dataSource] = "NCERT"
                    it[NcertSyllabusReferenceTable.createdAt] = now
                    it[NcertSyllabusReferenceTable.updatedAt] = now
                }
            }
        }
        seeded = true
        log.info("Seeded {} NCERT reference entries", allData.size)
    }

    fun normalizeClassLevel(input: String): String {
        val digits = input.trim().replace(Regex("^(Class|Grade)\\s*", RegexOption.IGNORE_CASE), "").trim()
        return if (digits.all { it.isDigit() }) "Class $digits" else input.trim()
    }

    fun normalizeSubjectName(input: String): String {
        val l = input.trim().lowercase()
        return when (l) {
            "maths", "math", "mathematics" -> "Mathematics"
            "science" -> "Science"
            "social science", "social studies", "sst" -> "Social Science"
            "english" -> "English"
            "hindi" -> "Hindi"
            "sanskrit" -> "Sanskrit"
            "physics" -> "Physics"
            "chemistry" -> "Chemistry"
            "biology" -> "Biology"
            "computer science", "cs" -> "Computer Science"
            "economics" -> "Economics"
            "evs", "environmental studies", "environmental science" -> "EVS"
            "political science", "civics" -> "Political Science"
            "geography" -> "Geography"
            "history" -> "History"
            "business studies" -> "Business Studies"
            "accountancy", "accounting" -> "Accountancy"
            "psychology" -> "Psychology"
            "sociology" -> "Sociology"
            else -> input.trim()
        }
    }
}
