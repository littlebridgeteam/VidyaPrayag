package com.littlebridge.enrollplus.util

import platform.Foundation.NSUUID
import platform.Foundation.timeIntervalSince1970

actual fun currentTimeMillis(): Long = (platform.Foundation.NSDate().timeIntervalSince1970() * 1000).toLong()

actual fun randomUUID(): String = NSUUID().UUIDString()
