package com.littlebridge.enrollplus.core

import org.slf4j.LoggerFactory

object RuntimeEnvironment {
    private val log = LoggerFactory.getLogger("RuntimeEnvironment")

    val isProduction: Boolean by lazy {
        val appEnv = EnvConfig.get("APP_ENV")
        val databaseUrl = EnvConfig.get("DATABASE_URL")

        val prod = when {
            appEnv?.lowercase() == "production" -> true
            appEnv?.lowercase() == "development" -> false
            databaseUrl != null -> true
            else -> false
        }

        if (prod) {
            log.info("RuntimeEnvironment: production mode detected")
        } else {
            log.info("RuntimeEnvironment: development mode detected")
        }

        prod
    }
}
