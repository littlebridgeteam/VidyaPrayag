package com.littlebridge.enrollplus.core.locale

actual fun icuFormat(pattern: String, locale: String, vararg args: Pair<String, Any?>): String {
    var result = pattern
    args.forEach { (k, v) ->
        result = result.replace("{$k}", v?.toString() ?: "")
    }
    return result
}
