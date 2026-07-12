package com.littlebridge.enrollplus.feature.platform

import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.PlatformScreensTable
import com.littlebridge.enrollplus.db.PlatformFeaturesTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import java.util.UUID
import java.time.Instant

object ScreenRegistryService {

    suspend fun list(
        page: Int = 1,
        pageSize: Int = 25,
        module: String? = null,
        featureId: String? = null,
        search: String? = null,
    ): Pair<List<ScreenDto>, Long> = dbQuery {
        val conditions = Op.build {
            (if (module != null) PlatformScreensTable.module eq module else Op.TRUE) and
            (if (featureId != null) PlatformScreensTable.featureId eq UUID.fromString(featureId) else Op.TRUE) and
            (if (search != null) {
                (PlatformScreensTable.name like "%$search%") or
                (PlatformScreensTable.screenId like "%$search%") or
                (PlatformScreensTable.route like "%$search%")
            } else Op.TRUE)
        }
        val total = PlatformScreensTable.selectAll().where { conditions }.count()
        val items = PlatformScreensTable.selectAll().where { conditions }
            .orderBy(PlatformScreensTable.name, SortOrder.ASC)
            .limit(pageSize, ((page - 1) * pageSize).toLong())
            .map { row ->
                val featureName = row[PlatformScreensTable.featureId]?.let { fid ->
                    PlatformFeaturesTable.selectAll().where { PlatformFeaturesTable.id eq fid }
                        .singleOrNull()?.get(PlatformFeaturesTable.name)
                }
                row.toScreenDtoInline(featureName)
            }
        items to total
    }

    suspend fun getById(id: UUID): ScreenDto? = dbQuery {
        val row = PlatformScreensTable.selectAll().where { PlatformScreensTable.id eq id }
            .singleOrNull() ?: return@dbQuery null
        val featureName = row[PlatformScreensTable.featureId]?.let { fid ->
            PlatformFeaturesTable.selectAll().where { PlatformFeaturesTable.id eq fid }
                .singleOrNull()?.get(PlatformFeaturesTable.name)
        }
        row.toScreenDtoInline(featureName)
    }

    private fun ResultRow.toScreenDtoInline(featureName: String? = null): ScreenDto = ScreenDto(
        id = this[PlatformScreensTable.id].value.toString(),
        screen_id = this[PlatformScreensTable.screenId],
        name = this[PlatformScreensTable.name],
        route = this[PlatformScreensTable.route],
        module = this[PlatformScreensTable.module],
        purpose = this[PlatformScreensTable.purpose],
        screenshot_url = this[PlatformScreensTable.screenshotUrl],
        permissions = this[PlatformScreensTable.permissions],
        user_actions = this[PlatformScreensTable.userActions],
        connected_screens = this[PlatformScreensTable.connectedScreens],
        empty_state = this[PlatformScreensTable.emptyState],
        loading_state = this[PlatformScreensTable.loadingState],
        error_state = this[PlatformScreensTable.errorState],
        feature_id = this[PlatformScreensTable.featureId]?.toString(),
        feature_name = featureName,
        sort_order = this[PlatformScreensTable.sortOrder],
        metadata = this[PlatformScreensTable.metadata],
    )

    suspend fun create(req: CreateScreenRequest): UUID = dbQuery {
        PlatformScreensTable.insert {
            it[PlatformScreensTable.screenId] = req.screen_id
            it[PlatformScreensTable.name] = req.name
            it[PlatformScreensTable.route] = req.route
            it[PlatformScreensTable.module] = req.module
            it[PlatformScreensTable.purpose] = req.purpose
            it[PlatformScreensTable.screenshotUrl] = req.screenshot_url
            it[PlatformScreensTable.permissions] = req.permissions
            it[PlatformScreensTable.userActions] = req.user_actions
            it[PlatformScreensTable.connectedScreens] = req.connected_screens
            it[PlatformScreensTable.emptyState] = req.empty_state
            it[PlatformScreensTable.loadingState] = req.loading_state
            it[PlatformScreensTable.errorState] = req.error_state
            it[PlatformScreensTable.featureId] = req.feature_id?.let { UUID.fromString(it) }
            it[PlatformScreensTable.sortOrder] = req.sort_order
            it[PlatformScreensTable.metadata] = req.metadata
            it[PlatformScreensTable.createdAt] = Instant.now()
            it[PlatformScreensTable.updatedAt] = Instant.now()
        }[PlatformScreensTable.id].value
    }

    suspend fun update(id: UUID, req: UpdateScreenRequest): Boolean = dbQuery {
        PlatformScreensTable.update(where = { PlatformScreensTable.id eq id }) {
            req.name?.let { v -> it[PlatformScreensTable.name] = v }
            req.route?.let { v -> it[PlatformScreensTable.route] = v }
            req.module?.let { v -> it[PlatformScreensTable.module] = v }
            req.purpose?.let { v -> it[PlatformScreensTable.purpose] = v }
            req.screenshot_url?.let { v -> it[PlatformScreensTable.screenshotUrl] = v }
            req.permissions?.let { v -> it[PlatformScreensTable.permissions] = v }
            req.user_actions?.let { v -> it[PlatformScreensTable.userActions] = v }
            req.connected_screens?.let { v -> it[PlatformScreensTable.connectedScreens] = v }
            req.empty_state?.let { v -> it[PlatformScreensTable.emptyState] = v }
            req.loading_state?.let { v -> it[PlatformScreensTable.loadingState] = v }
            req.error_state?.let { v -> it[PlatformScreensTable.errorState] = v }
            req.feature_id?.let { v -> it[PlatformScreensTable.featureId] = UUID.fromString(v) }
            req.sort_order?.let { v -> it[PlatformScreensTable.sortOrder] = v }
            req.metadata?.let { v -> it[PlatformScreensTable.metadata] = v }
            it[PlatformScreensTable.updatedAt] = Instant.now()
        } > 0
    }

    suspend fun delete(id: UUID): Boolean = dbQuery {
        PlatformScreensTable.deleteWhere { PlatformScreensTable.id eq id } > 0
    }
}
