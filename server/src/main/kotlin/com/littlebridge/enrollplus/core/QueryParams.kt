/*
 * File: QueryParams.kt
 * Module: core
 * Purpose:
 *   Small, reusable helpers for reading common query-string parameters off a
 *   Ktor `ApplicationCall`. Before this, ~30 route handlers each open-coded the
 *   identical idiom
 *       call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
 *   (and its `page` / `coerceIn(...)` variants), which drifted in bounds and
 *   defaults across features. Centralising it keeps pagination parsing uniform.
 *
 * Provides:
 *   - call.intParam(name, default, min, max) → parse an Int query param w/ clamp
 *   - call.limitParam(default, max, min)     → the "limit" list-size param
 *   - call.pageParam(default)                → the 1-based "page" param
 *
 * Note: unlike feature.pews.core.PaginationParams (which reads the `page_size`
 * param and builds a PaginatedResponse), these helpers target the `limit`/`page`
 * convention used across the rest of the API.
 */
package com.littlebridge.enrollplus.core

import io.ktor.server.application.ApplicationCall

/**
 * Read an integer query parameter, falling back to [default] when it is absent
 * or non-numeric. When [max] is provided the result is clamped to [[min], [max]].
 */
fun ApplicationCall.intParam(
    name: String,
    default: Int,
    min: Int = 1,
    max: Int? = null,
): Int {
    val value = request.queryParameters[name]?.toIntOrNull() ?: default
    return if (max != null) value.coerceIn(min, max) else value
}

/**
 * The list-size `limit` query param. Pass [max] to cap the page size; [min]
 * defaults to 1 and is only applied when [max] is given.
 */
fun ApplicationCall.limitParam(
    default: Int,
    max: Int? = null,
    min: Int = 1,
): Int = intParam("limit", default = default, min = min, max = max)

/**
 * The 1-based `page` query param, clamped to be at least 1 so it can never
 * produce a negative SQL OFFSET.
 */
fun ApplicationCall.pageParam(default: Int = 1): Int =
    (request.queryParameters["page"]?.toIntOrNull() ?: default).coerceAtLeast(1)
