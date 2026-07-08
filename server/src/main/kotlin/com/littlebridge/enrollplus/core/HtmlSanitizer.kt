package com.littlebridge.enrollplus.core

object HtmlSanitizer {
    private val ALLOWED_TAGS = setOf("b", "i", "u", "br", "p", "strong", "em", "ul", "ol", "li")

    fun sanitize(input: String): String {
        if (input.isBlank()) return input
        val sb = StringBuilder(input.length)
        var i = 0
        while (i < input.length) {
            val c = input[i]
            when (c) {
                '<' -> {
                    val close = input.indexOf('>', i)
                    if (close == -1) {
                        sb.append("&lt;")
                        i++
                    } else {
                        val tagContent = input.substring(i + 1, close).trim()
                        val isClosing = tagContent.startsWith("/")
                        val tagName = if (isClosing) tagContent.substring(1) else tagContent
                        val baseTag = tagName.split(Regex("[\\s/>]"))[0].lowercase()
                        if (ALLOWED_TAGS.contains(baseTag)) {
                            sb.append('<').append(if (isClosing) "/" else "").append(baseTag).append('>')
                        } else {
                            sb.append("&lt;").append(tagContent).append("&gt;")
                        }
                        i = close + 1
                    }
                }
                '&' -> {
                    if (input.startsWith("&amp;", i) || input.startsWith("&lt;", i) ||
                        input.startsWith("&gt;", i) || input.startsWith("&quot;", i) ||
                        input.startsWith("&#", i)
                    ) {
                        sb.append(c)
                        i++
                    } else {
                        sb.append("&amp;")
                        i++
                    }
                }
                '>' -> { sb.append("&gt;"); i++ }
                '"' -> { sb.append("&quot;"); i++ }
                '\'' -> { sb.append("&#39;"); i++ }
                else -> { sb.append(c); i++ }
            }
        }
        return sb.toString()
    }
}
