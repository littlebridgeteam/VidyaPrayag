package com.littlebridge.enrollplus.core

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.CallFailed
import io.ktor.server.application.hooks.ResponseSent
import io.ktor.server.request.header
import io.ktor.util.AttributeKey
import org.slf4j.MDC
import java.util.UUID

val RequestIdKey = AttributeKey<String>("RequestId")

const val REQUEST_ID_HEADER = "X-Request-ID"

fun ApplicationCall.requestId(): String =
    attributes[RequestIdKey]

val RequestIdPlugin = createApplicationPlugin("RequestId") {
    onCall { call ->
        val id = call.request.header(REQUEST_ID_HEADER)?.takeIf { it.isNotBlank() }
            ?: UUID.randomUUID().toString()
        call.attributes.put(RequestIdKey, id)
        MDC.put("requestId", id)
        call.response.headers.append(REQUEST_ID_HEADER, id)
    }

    on(ResponseSent) { call ->
        MDC.remove("requestId")
    }

    on(CallFailed) { call, _ ->
        MDC.remove("requestId")
    }
}
