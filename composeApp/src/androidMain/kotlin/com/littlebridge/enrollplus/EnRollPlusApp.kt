package com.littlebridge.enrollplus

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.littlebridge.enrollplus.di.initKoin
import com.littlebridge.enrollplus.notification.NotificationManagerHelper
import com.littlebridge.enrollplus.util.AnalyticsTracker
import com.littlebridge.enrollplus.util.AppConfig
import com.littlebridge.enrollplus.util.ContextHolder
import org.koin.android.ext.koin.androidContext

class EnRollPlusApp : Application() {
    companion object {
        lateinit var instance: EnRollPlusApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ContextHolder.appContext = this
        NotificationManagerHelper.createAllChannels(this)

        // Initialize Firebase Crashlytics and enable logging.
        // Firebase auto-initializes from google-services.json; we just need to
        // explicitly enable collection (it's on by default in release builds,
        // but this ensures it's on even in debug).
        runCatching {
            FirebaseApp.initializeApp(this)
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.isCrashlyticsCollectionEnabled = true
            // Log the app version as a custom key for easier crash triage
            crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
            Log.i("VidyaPrayagApp", "Firebase Crashlytics initialized — crash reporting enabled.")

            // Global uncaught exception handler — records the exception as a
            // Crashlytics non-fatal record, then delegates to the platform's
            // default handler so the process still terminates normally.
            val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                runCatching {
                    crashlytics.recordException(throwable)
                    crashlytics.log("Uncaught exception on ${thread.name}: ${throwable.message}")
                }
                previousHandler?.uncaughtException(thread, throwable)
            }
        }.onFailure { e ->
            Log.w("VidyaPrayagApp", "Firebase Crashlytics init failed: ${e.message}")
        }

        // Set Crashlytics custom keys for crash triage.
        AnalyticsTracker.setCustomKey("app_version", BuildConfig.VERSION_NAME)
        AnalyticsTracker.setCustomKey("backend_url", AppConfig.authBaseUrl)
        AnalyticsTracker.log("App started — backend=${AppConfig.authBaseUrl}")

        // Set Clarity session tags for filtering on dashboard.
        AnalyticsTracker.setCustomTag("app_version", BuildConfig.VERSION_NAME)
        AnalyticsTracker.setCustomTag("environment", BuildConfig.FLAVOR)
        AnalyticsTracker.setCustomTag("build_type", BuildConfig.BUILD_TYPE)
        AnalyticsTracker.setCustomTag("platform", "android")

        // Log app_open event.
        AnalyticsTracker.event("vp_app_open", mapOf(
            "app_version" to BuildConfig.VERSION_NAME,
            "platform" to "android",
            "environment" to BuildConfig.FLAVOR,
        ))

        // Surface the resolved backend at boot so you can confirm in Logcat
        // exactly which server the phone is calling (filter tag: VidyaPrayagApp).
        Log.i(
            "VidyaPrayagApp",
            "Backend -> authBaseUrl=${AppConfig.authBaseUrl} schoolBaseUrl=${AppConfig.schoolBaseUrl}"
        )

        initKoin {
            androidContext(this@EnRollPlusApp)
        }
    }
}
