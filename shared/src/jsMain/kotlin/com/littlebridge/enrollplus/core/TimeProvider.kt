package com.littlebridge.enrollplus.core

import kotlin.js.Date

actual fun currentTimeMillis(): Long = Date.now().toLong()
