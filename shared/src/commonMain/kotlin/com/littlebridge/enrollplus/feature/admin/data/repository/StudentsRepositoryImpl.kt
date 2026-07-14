package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.StudentsApi
import com.littlebridge.enrollplus.feature.admin.domain.model.BulkImportStudentsRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.BulkImportStudentsResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateStudentRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.StudentDto
import com.littlebridge.enrollplus.feature.admin.domain.model.StudentListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.StudentProfileDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherProfileDto
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateStudentRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.StudentsRepository

class StudentsRepositoryImpl(
    private val api: StudentsApi,
    private val cache: CacheManager,
) : StudentsRepository {

    override suspend fun getStudents(token: String): NetworkResult<ApiResponse<StudentListResponse>> =
        cacheFirstNetworkResult(cache, "admin_students", ApiResponse.serializer(StudentListResponse.serializer())) { api.getStudents(token) }

    override suspend fun createStudent(token: String, request: CreateStudentRequest): NetworkResult<ApiResponse<StudentDto>> =
        api.createStudent(token, request)

    override suspend fun updateStudent(token: String, studentId: String, request: UpdateStudentRequest): NetworkResult<ApiResponse<StudentDto>> =
        api.updateStudent(token, studentId, request)

    override suspend fun importStudents(token: String, request: BulkImportStudentsRequest): NetworkResult<ApiResponse<BulkImportStudentsResponse>> =
        api.importStudents(token, request)

    override suspend fun deleteStudent(token: String, studentId: String): NetworkResult<ApiResponse<Unit>> =
        api.deleteStudent(token, studentId)

    override suspend fun getStudentProfile(token: String, studentId: String): NetworkResult<ApiResponse<StudentProfileDto>> =
        cacheFirstNetworkResult(cache, "admin_student_profile_$studentId", ApiResponse.serializer(StudentProfileDto.serializer())) { api.getStudentProfile(token, studentId) }

    override suspend fun getTeacherProfile(token: String, teacherId: String): NetworkResult<ApiResponse<TeacherProfileDto>> =
        cacheFirstNetworkResult(cache, "admin_teacher_profile_$teacherId", ApiResponse.serializer(TeacherProfileDto.serializer())) { api.getTeacherProfile(token, teacherId) }
}
