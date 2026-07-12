package com.littlebridge.enrollplus.core

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

/**
 * Swagger UI + OpenAPI spec endpoint.
 *
 * Serves:
 *   GET /api/v1/openapi.yaml  — raw OpenAPI 3.0 spec
 *   GET /api/v1/docs          — Swagger UI HTML (loads spec from CDN)
 *
 * The spec is maintained at docs/openapi.yaml and embedded as a resource.
 */
fun Route.openApiRouting() {
    get("/api/v1/openapi.yaml") {
        val spec = javaClass.classLoader
            ?.getResourceAsStream("openapi.yaml")
            ?.bufferedReader()
            ?.use { it.readText() }
        if (spec != null) {
            call.respondText(spec, ContentType.parse("application/yaml"))
        } else {
            call.respondText("OpenAPI spec not found", ContentType.Text.Plain, HttpStatusCode.NotFound)
        }
    }

    get("/api/v1/docs") {
        val html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Enroll+ API Docs</title>
                <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist@5.11.0/swagger-ui.css">
                <style>
                    body { margin: 0; }
                    .topbar { display: none; }
                </style>
            </head>
            <body>
                <div id="swagger-ui"></div>
                <script src="https://unpkg.com/swagger-ui-dist@5.11.0/swagger-ui-bundle.js"></script>
                <script>
                    window.onload = function() {
                        SwaggerUIBundle({
                            url: '/api/v1/openapi.yaml',
                            dom_id: '#swagger-ui',
                            deepLinking: true,
                            presets: [SwaggerUIBundle.presets.apis],
                            layout: 'BaseLayout'
                        });
                    };
                </script>
            </body>
            </html>
        """.trimIndent()
        call.respondText(html, ContentType.Text.Html)
    }
}
