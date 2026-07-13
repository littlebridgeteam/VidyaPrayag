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
        } catch (e: Exception) {
            log.warn("Failed to parse NCERT chapters JSON for classLevel=$nc subject=$ns; returning empty list", e)
            emptyList()
        }
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
        val allData = NcertReferenceData.DATA + NcertReferenceData2.DATA + NcertReferenceData3.DATA + NcertReferenceData4.DATA
        if (count == 0L) {
            log.info("Seeding NCERT syllabus reference data ({} entries)...", allData.size)
            val now = Instant.now()
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
        } else {
            // Table already has rows — check for and insert any missing entries
            val existing = dbQuery {
                NcertSyllabusReferenceTable.selectAll()
                    .map { it[NcertSyllabusReferenceTable.classLevel] to it[NcertSyllabusReferenceTable.subjectName] }
            }.toSet()
            val missing = allData.filter { (it.classLevel to it.subjectName) !in existing }
            if (missing.isNotEmpty()) {
                log.info("Adding {} missing NCERT reference entries...", missing.size)
                val now = Instant.now()
                for (s in missing) {
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
            }
        }
        seeded = true
    }

    fun normalizeClassLevel(input: String): String {
        val cleaned = input.trim().replace(Regex("^(Class|Grade)\\s*", RegexOption.IGNORE_CASE), "").trim()
        // Strip ordinal suffixes: "11th" → "11", "3rd" → "3"
        val digits = cleaned.replace(Regex("(st|nd|rd|th)$", RegexOption.IGNORE_CASE), "").trim()
        if (digits.all { it.isDigit() } && digits.isNotEmpty()) return "Class $digits"
        // Roman numerals: XI → 11, XII → 12, etc.
        val romanMap = mapOf("I" to 1, "II" to 2, "III" to 3, "IV" to 4, "V" to 5, "VI" to 6, "VII" to 7, "VIII" to 8, "IX" to 9, "X" to 10, "XI" to 11, "XII" to 12)
        val romanNum = romanMap[cleaned.uppercase()]
        if (romanNum != null) return "Class $romanNum"
        return input.trim()
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
            "biology", "bio" -> "Biology"
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
