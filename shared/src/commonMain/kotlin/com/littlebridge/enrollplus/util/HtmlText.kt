package com.littlebridge.enrollplus.util

/**
 * Decodes common HTML entities that [HtmlSanitizer] on the server may encode.
 * This is a minimal, allocation-efficient decoder for the entities the sanitizer
 * produces: &amp; &#39; &quot; &lt; &gt;
 *
 * Call this on any server-provided text before displaying it in Compose.
 */
fun String.htmlDecode(): String =
    if (this.isEmpty()) this
    else this
        .replace("&#39;", "'")
        .replace("&quot;", "\"")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&amp;", "&")
