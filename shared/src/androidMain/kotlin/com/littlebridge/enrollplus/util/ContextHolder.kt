package com.littlebridge.enrollplus.util

import android.content.Context

object ContextHolder {
    @Volatile
    var appContext: Context? = null
}
