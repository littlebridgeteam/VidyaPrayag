package com.littlebridge.enrollplus.db

import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory

object FlywayMigrationRunner {
    private val logger = LoggerFactory.getLogger(FlywayMigrationRunner::class.java)

    fun runMigrations(dataSource: HikariDataSource) {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .baselineOnMigrate(true)
            .baselineVersion("1")
            .locations("classpath:db/migration")
            .validateOnMigrate(true)
            .outOfOrder(true)
            .load()

        flyway.repair()
        val result = flyway.migrate()
        logger.info(
            "FLYWAY: migrations applied={}, pending={}",
            result.migrationsExecuted,
            result.pendingMigrations
        )
    }
}
