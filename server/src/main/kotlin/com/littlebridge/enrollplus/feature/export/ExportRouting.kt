package com.littlebridge.enrollplus.feature.export

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.requireSchoolOrTeacherContext
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.exportRouting() {
    val service = ExportService()

    authenticate("jwt") {
        route("/api/v1/school/export") {

            // GET available export types for the user's role
            get("/types") {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@get
                val types = service.getExportTypes(ctx.role)
                call.ok(types)
            }

            // POST generate an export
            post {
                val ctx = call.requireSchoolOrTeacherContext() ?: return@post
                val req = call.receive<ExportRequest>()

                if (req.type.isBlank()) {
                    call.fail("Export type is required", HttpStatusCode.BadRequest, "VALIDATION_ERROR")
                    return@post
                }
                if (req.format !in listOf("pdf", "csv")) {
                    call.fail("Format must be 'pdf' or 'csv'", HttpStatusCode.BadRequest, "INVALID_FORMAT")
                    return@post
                }

                try {
                    val result = service.generateExport(
                        schoolId = ctx.schoolId,
                        userId = ctx.userId,
                        role = ctx.role,
                        request = req,
                    )
                    if (result.downloadUrl == null) {
                        call.ok(result, result.message ?: "No data found")
                    } else {
                        call.ok(result, "Export generated successfully")
                    }
                } catch (e: SecurityException) {
                    call.fail(e.message ?: "Access denied", HttpStatusCode.Forbidden, "EXPORT_FORBIDDEN")
                } catch (e: IllegalArgumentException) {
                    call.fail(e.message ?: "Invalid request", HttpStatusCode.BadRequest, "VALIDATION_ERROR")
                } catch (e: Exception) {
                    call.fail("Export generation failed: ${e.message}", HttpStatusCode.InternalServerError, "EXPORT_FAILED")
                }
            }
        }
    }
}
