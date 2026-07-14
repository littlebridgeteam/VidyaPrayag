package com.littlebridge.enrollplus

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.littlebridge.enrollplus.notification.NotificationManagerHelper
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel

class MainActivity : ComponentActivity() {
    private val contentReady = mutableStateOf(false)
    private val deepLink = mutableStateOf<String?>(null)
    private val pushRefType = mutableStateOf<String?>(null)
    private val pushRefId = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

        enableEdgeToEdge(
            statusBarStyle = if (isDarkMode) {
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )

            },
            navigationBarStyle = if (isDarkMode) {
                SystemBarStyle.dark(
                    android.graphics.Color.TRANSPARENT
                )
            } else {
                SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
            }
        )
        splashScreen.setKeepOnScreenCondition { !contentReady.value }

        // Microsoft Clarity — session recordings & heatmaps (Android-only)
        if (BuildConfig.CLARITY_ENABLED) {
            val clarityConfig = ClarityConfig(
                projectId = "xly829jv3t",
                logLevel = LogLevel.None
            )
            Clarity.initialize(applicationContext, clarityConfig)
        }

        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val iconView = try {
                splashScreenViewProvider.iconView
            } catch (_: Exception) {
                null
            }

            val targetView = iconView ?: splashScreenViewProvider.view
            val fade = ObjectAnimator.ofFloat(targetView, View.ALPHA, 1f, 0f)
            val scaleX = ObjectAnimator.ofFloat(targetView, View.SCALE_X, 1f, 1.12f)
            val scaleY = ObjectAnimator.ofFloat(targetView, View.SCALE_Y, 1f, 1.12f)

            listOf(fade, scaleX, scaleY).forEach {
                it.interpolator = AnticipateInterpolator()
                it.duration = 280L
            }

            fade.doOnEnd { splashScreenViewProvider.remove() }

            fade.start()
            scaleX.start()
            scaleY.start()
        }

        // Read deep link + ref info from notification intent (if launched from a push tap).
        deepLink.value = extractDeepLink(intent)
        pushRefType.value = extractRefExtra(intent, "refType")
        pushRefId.value = extractRefExtra(intent, "refId")

        setContent {
            App(
                onContentRendered = { contentReady.value = true },
                deepLink = deepLink.value,
                onDeepLinkConsumed = { deepLink.value = null },
                pushRefType = pushRefType.value,
                pushRefId = pushRefId.value,
                onPushRefConsumed = {
                    pushRefType.value = null
                    pushRefId.value = null
                },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        deepLink.value = extractDeepLink(intent)
        pushRefType.value = extractRefExtra(intent, "refType")
        pushRefId.value = extractRefExtra(intent, "refId")
    }

    private fun extractDeepLink(intent: Intent): String? {
        // 1. Notification tap from our local NotificationManagerHelper: raw path
        //    stored as EXTRA_DEEP_LINK with the EXTRA_FROM_PUSH flag.
        if (intent.getBooleanExtra(NotificationManagerHelper.EXTRA_FROM_PUSH, false)) {
            val dl = intent.getStringExtra(NotificationManagerHelper.EXTRA_DEEP_LINK)
            android.util.Log.d("MainActivity/DeepLink", "From push extra: $dl")
            return dl
        }
        // 2. FCM auto-rendered notification tap: when the app is backgrounded, FCM
        //    auto-renders the notification from the notification block (onMessageReceived
        //    is NOT called). The data payload is delivered as intent extras when the
        //    user taps the notification. The "deepLink" key matches the server's
        //    data payload key (KEY_DEEP_LINK in VidyaPrayagFirebaseMessagingService).
        val fcmDeepLink = intent.getStringExtra("deepLink")
        if (!fcmDeepLink.isNullOrBlank()) {
            android.util.Log.d("MainActivity/DeepLink", "From FCM data extra: $fcmDeepLink")
            return fcmDeepLink
        }
        // 3. External deep link: vidyaprayag://app/<path> URI from the intent-filter.
        val data = intent.data
        if (data != null && data.scheme == NotificationManagerHelper.DEEP_LINK_SCHEME && data.host == "app") {
            val path = data.path
            val result = if (!path.isNullOrBlank()) path else null
            android.util.Log.d("MainActivity/DeepLink", "From URI: $result")
            return result
        }
        android.util.Log.d("MainActivity/DeepLink", "No deep link found in intent")
        return null
    }

    private fun extractRefExtra(intent: Intent, key: String): String? {
        // From our local NotificationManagerHelper PendingIntent.
        if (intent.getBooleanExtra(NotificationManagerHelper.EXTRA_FROM_PUSH, false)) {
            val v = intent.getStringExtra(key)
            if (!v.isNullOrBlank()) return v
        }
        // From FCM auto-rendered notification: data payload delivered as intent extras.
        val fcmVal = intent.getStringExtra(key)
        return fcmVal?.takeIf { it.isNotBlank() }
    }
}
