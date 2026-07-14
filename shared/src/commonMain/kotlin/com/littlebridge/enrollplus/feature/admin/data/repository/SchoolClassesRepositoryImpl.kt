package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.SchoolClassesApi
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateSchoolClassRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateSchoolSubjectRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolClassDto
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolClassListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolSubjectDto
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolSubjectListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.ChangeRequestListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateChangeRequestRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.BulkCreatePeriodsRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.BulkCreatePeriodsResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateExceptionRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.CreatePeriodRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.PeriodDetailDto
import com.littlebridge.enrollplus.feature.admin.domain.model.PeriodExceptionDto
import com.littlebridge.enrollplus.feature.admin.domain.model.PeriodExceptionListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.ReviewRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.TimetableChangeRequestDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TimetableDto
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdatePeriodRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateSchoolClassRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateSchoolSubjectRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.SchoolClassesRepository

class SchoolClassesRepositoryImpl(
    private val api: SchoolClassesApi,
    private val cache: CacheManager,
) : SchoolClassesRepository {

    override suspend fun listClasses(token: String) =
        cacheFirstNetworkResult(cache, "admin_school_classes", ApiResponse.serializer(SchoolClassListResponse.serializer())) { api.listClasses(token) }
    override suspend fun createClass(token: String, req: CreateSchoolClassRequest) = api.createClass(token, req)
    override suspend fun updateClass(token: String, id: String, req: UpdateSchoolClassRequest) = api.updateClass(token, id, req)
    override suspend fun deleteClass(token: String, id: String) = api.deleteClass(token, id)

    override suspend fun listSubjects(token: String, classId: String) =
        cacheFirstNetworkResult(cache, "admin_school_subjects_$classId", ApiResponse.serializer(SchoolSubjectListResponse.serializer())) { api.listSubjects(token, classId) }
    override suspend fun createSubject(token: String, classId: String, req: CreateSchoolSubjectRequest) = api.createSubject(token, classId, req)
    override suspend fun updateSubject(token: String, id: String, req: UpdateSchoolSubjectRequest) = api.updateSubject(token, id, req)
    override suspend fun deleteSubject(token: String, id: String) = api.deleteSubject(token, id)

    override suspend fun getTimetable(token: String, classFilter: String?) =
        cacheFirstNetworkResult(cache, "admin_timetable_${classFilter ?: "all"}", ApiResponse.serializer(TimetableDto.serializer())) { api.getTimetable(token, classFilter) }

    override suspend fun createPeriod(token: String, req: CreatePeriodRequest) = api.createPeriod(token, req)
    override suspend fun bulkCreatePeriods(token: String, req: BulkCreatePeriodsRequest) = api.bulkCreatePeriods(token, req)
    override suspend fun updatePeriod(token: String, id: String, req: UpdatePeriodRequest) = api.updatePeriod(token, id, req)
    override suspend fun deletePeriod(token: String, id: String) = api.deletePeriod(token, id)

    override suspend fun listExceptions(token: String, date: String?) =
        cacheFirstNetworkResult(cache, "admin_period_exceptions_${date ?: "all"}", ApiResponse.serializer(PeriodExceptionListResponse.serializer())) { api.listExceptions(token, date) }
    override suspend fun createException(token: String, req: CreateExceptionRequest) = api.createException(token, req)
    override suspend fun deleteException(token: String, id: String) = api.deleteException(token, id)

    override suspend fun listChangeRequests(token: String, status: String?) =
        cacheFirstNetworkResult(cache, "admin_change_requests_${status ?: "all"}", ApiResponse.serializer(ChangeRequestListResponse.serializer())) { api.listChangeRequests(token, status) }
    override suspend fun approveChangeRequest(token: String, id: String, req: ReviewRequest) = api.approveChangeRequest(token, id, req)
    override suspend fun rejectChangeRequest(token: String, id: String, req: ReviewRequest) = api.rejectChangeRequest(token, id, req)
}
