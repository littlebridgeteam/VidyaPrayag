package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.LinkRequestsApi
import com.littlebridge.enrollplus.feature.admin.domain.model.LinkDecisionResult
import com.littlebridge.enrollplus.feature.admin.domain.model.LinkRequestsResponse
import com.littlebridge.enrollplus.feature.admin.domain.repository.LinkRequestsRepository

class LinkRequestsRepositoryImpl(
    private val api: LinkRequestsApi,
    private val cache: CacheManager,
) : LinkRequestsRepository {

    override suspend fun getLinkRequests(
        token: String,
        status: String
    ): NetworkResult<ApiResponse<LinkRequestsResponse>> =
        cacheFirstNetworkResult(cache, "admin_link_requests_$status", ApiResponse.serializer(LinkRequestsResponse.serializer())) { api.getLinkRequests(token, status) }

    override suspend fun approve(
        token: String,
        id: String
    ): NetworkResult<ApiResponse<LinkDecisionResult>> = api.approve(token, id)

    override suspend fun reject(
        token: String,
        id: String
    ): NetworkResult<ApiResponse<LinkDecisionResult>> = api.reject(token, id)
}
