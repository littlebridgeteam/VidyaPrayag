package com.littlebridge.enrollplus.feature.admin.presentation

import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.OnboardingBasics
import com.littlebridge.enrollplus.feature.admin.domain.model.OnboardingCompletionResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.OnboardingStatusResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.OnboardingStepResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.OnboardingSubmitRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.OnboardingSubmitResponse
import com.littlebridge.enrollplus.feature.admin.domain.repository.OnboardingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Regression test for the "mandatory validation missing" onboarding bug.
 *
 * The BASIC step must block submission when the school name or principal's
 * mobile number is empty, so the wizard cannot advance and the admin cannot
 * reach the dashboard with incomplete data.
 */
class InstitutionalBasicOBViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submit with empty school name does not advance and shows error`() = runTest(testDispatcher) {
        val viewModel = createViewModel(
            basics = OnboardingBasics(schoolName = "", contactNumber = "9876543210"),
        )

        var advanced = false
        viewModel.submit { advanced = true }
        advanceUntilIdle()

        assertFalse(advanced, "Wizard must not advance when school name is empty")
        assertEquals("School name is required.", viewModel.errorMessage.value)
    }

    @Test
    fun `submit with empty contact number does not advance and shows error`() = runTest(testDispatcher) {
        val viewModel = createViewModel(
            basics = OnboardingBasics(schoolName = "Saraswati Vidya Mandir", contactNumber = ""),
        )

        var advanced = false
        viewModel.submit { advanced = true }
        advanceUntilIdle()

        assertFalse(advanced, "Wizard must not advance when contact number is empty")
        assertEquals("Principal's mobile number is required.", viewModel.errorMessage.value)
    }

    @Test
    fun `submit with valid basics advances on repository success`() = runTest(testDispatcher) {
        val viewModel = createViewModel(
            basics = OnboardingBasics(
                schoolName = "Saraswati Vidya Mandir",
                contactNumber = "9876543210",
            ),
        )

        var advanced = false
        viewModel.submit { advanced = true }
        advanceUntilIdle()

        assertTrue(advanced, "Wizard should advance when mandatory fields are filled")
        assertEquals(null, viewModel.errorMessage.value)
    }

    private fun createViewModel(
        token: String? = "test-token",
        basics: OnboardingBasics = OnboardingBasics(),
    ): InstitutionalBasicOBViewModel {
        val prefs = FakePreferenceRepository(token = token)
        val vm = InstitutionalBasicOBViewModel(FakeOnboardingRepository(), prefs)
        vm.updateSchoolName(basics.schoolName)
        vm.updateContact(basics.contactNumber)
        vm.updateBoard(basics.boardAffiliation)
        vm.updateEmail(basics.officialEmail)
        vm.updateCountryCode(basics.countryCode)
        vm.updateAddress(basics.address)
        return vm
    }

    private class FakePreferenceRepository(
        private val token: String? = "test-token",
    ) : PreferenceRepository {
        override fun getThemeName(): Flow<String> = MutableStateFlow("light")
        override suspend fun setThemeName(name: String) {}
        override fun getThemeMode(): Flow<String> = MutableStateFlow("light")
        override suspend fun setThemeMode(mode: String) {}
        override fun getCustomThemeId(): Flow<String?> = MutableStateFlow(null)
        override suspend fun setCustomThemeId(id: String?) {}
        override fun getUserRole(): Flow<String> = MutableStateFlow("SCHOOL_ADMIN")
        override suspend fun setUserRole(role: String) {}
        override fun getUserToken(): Flow<String?> = MutableStateFlow(token)
        override suspend fun setUserToken(token: String?) {}
        override fun getUserId(): Flow<String?> = MutableStateFlow("user-1")
        override suspend fun setUserId(userId: String?) {}
        override fun getRefreshToken(): Flow<String?> = MutableStateFlow("refresh-token")
        override suspend fun setRefreshToken(token: String?) {}
        override fun getProfileCompleted(): Flow<Boolean?> = MutableStateFlow(false)
        override suspend fun setProfileCompleted(completed: Boolean?) {}
        override fun getUserName(): Flow<String?> = MutableStateFlow("Test Admin")
        override suspend fun setUserName(name: String?) {}
        override fun getFcmToken(): Flow<String?> = MutableStateFlow(null)
        override suspend fun setFcmToken(token: String?) {}
        override fun getNotificationsDeclined(): Flow<Boolean> = MutableStateFlow(false)
        override suspend fun setNotificationsDeclined(declined: Boolean) {}
        override fun getFontScale(): Flow<Float> = MutableStateFlow(1f)
        override suspend fun setFontScale(scale: Float) {}
        override fun getCachedBranding(): Flow<String?> = MutableStateFlow(null)
        override suspend fun setCachedBranding(brandingJson: String?) {}
        override fun getLanguagePref(): Flow<String> = MutableStateFlow("en")
        override suspend fun setLanguagePref(lang: String) {}
        override suspend fun clearSession() {}
    }

    private class FakeOnboardingRepository : OnboardingRepository {
        override suspend fun getStep(token: String, obStepType: String) =
            NetworkResult.Success(OnboardingStepResponse(obStepType = obStepType))

        override suspend fun submitStep(
            token: String,
            request: OnboardingSubmitRequest,
        ) = NetworkResult.Success(
            OnboardingSubmitResponse(
                nextStep = "BRANDING",
                isOnboardingComplete = false,
                redirectToHome = false,
            )
        )

        override suspend fun getClassDetails(token: String, classId: String) =
            TODO("Not used")

        override suspend fun getStatus(token: String) =
            NetworkResult.Success(OnboardingStatusResponse())

        override suspend fun completeOnboarding(token: String) =
            NetworkResult.Success(OnboardingCompletionResponse("school-1", true, "active"))
    }
}
