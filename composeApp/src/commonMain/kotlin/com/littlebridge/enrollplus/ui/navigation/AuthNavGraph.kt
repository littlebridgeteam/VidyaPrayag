package com.littlebridge.enrollplus.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.littlebridge.enrollplus.feature.auth.presentation.AuthViewModel
import com.littlebridge.enrollplus.ui.screens.admin.AdminLoginScreen
import com.littlebridge.enrollplus.ui.screens.admin.AdminSignupScreen
import com.littlebridge.enrollplus.ui.screens.parent.ParentLoginScreen
import com.littlebridge.enrollplus.ui.screens.parent.ParentSignupScreen
import com.littlebridge.enrollplus.ui.screens.shared.ForgotPasswordScreen
import com.littlebridge.enrollplus.ui.screens.shared.LandingScreen
import com.littlebridge.enrollplus.ui.screens.shared.PrivacyPolicyScreen
import com.littlebridge.enrollplus.ui.screens.shared.SplashScreen
import com.littlebridge.enrollplus.ui.screens.shared.TermsConditionScreen
import com.littlebridge.enrollplus.ui.tokens.VMotion

@Composable
fun AuthNavGraph(
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
) {
    var currentRoute by remember { mutableStateOf(AuthRoute.Splash) }

    fun navigateTo(route: AuthRoute) {
        currentRoute = route
    }

    fun navigateByName(name: String) {
        when (name) {
            "Splash" -> navigateTo(AuthRoute.Splash)
            "Landing" -> navigateTo(AuthRoute.Landing)
            "ParentLogin" -> navigateTo(AuthRoute.ParentLogin)
            "ParentSignup" -> navigateTo(AuthRoute.ParentSignup)
            "AdminLogin" -> navigateTo(AuthRoute.AdminLogin)
            "AdminSignup" -> navigateTo(AuthRoute.AdminSignup)
            "Terms" -> navigateTo(AuthRoute.Terms)
            "PrivacyPolicy" -> navigateTo(AuthRoute.PrivacyPolicy)
            "ForgotPassword" -> navigateTo(AuthRoute.ForgotPassword)
        }
    }

    AnimatedContent(
        targetState = currentRoute,
        transitionSpec = {
            fadeIn(tween(VMotion.durDefault)) togetherWith fadeOut(tween(VMotion.durDefault))
        },
        label = "authNav",
    ) { route ->
        when (route) {
            AuthRoute.Splash -> SplashScreen(
                onTimeout = { navigateTo(AuthRoute.Landing) },
            )
            AuthRoute.Landing -> LandingScreen(
                onNavigate = { target -> navigateByName(target) },
            )
            AuthRoute.ParentLogin -> ParentLoginScreen(
                viewModel = authViewModel,
                onBack = { navigateTo(AuthRoute.Landing) },
                onNavigateToSignup = { navigateTo(AuthRoute.ParentSignup) },
                onAuthSuccess = onAuthSuccess,
            )
            AuthRoute.ParentSignup -> ParentSignupScreen(
                viewModel = authViewModel,
                onBack = { navigateTo(AuthRoute.ParentLogin) },
                onNavigateToLogin = { navigateTo(AuthRoute.ParentLogin) },
                onAuthSuccess = onAuthSuccess,
            )
            AuthRoute.AdminLogin -> AdminLoginScreen(
                viewModel = authViewModel,
                onBack = { navigateTo(AuthRoute.Landing) },
                onNavigateToSignup = { navigateTo(AuthRoute.AdminSignup) },
                onAuthSuccess = onAuthSuccess,
                onForgotPassword = { navigateTo(AuthRoute.ForgotPassword) },
            )
            AuthRoute.AdminSignup -> AdminSignupScreen(
                viewModel = authViewModel,
                onBack = { navigateTo(AuthRoute.AdminLogin) },
                onNavigateToLogin = { navigateTo(AuthRoute.AdminLogin) },
                onAuthSuccess = onAuthSuccess,
            )
            AuthRoute.Terms -> TermsConditionScreen(
                onBack = { navigateTo(AuthRoute.Landing) },
            )
            AuthRoute.PrivacyPolicy -> PrivacyPolicyScreen(
                onBack = { navigateTo(AuthRoute.Landing) },
            )
            AuthRoute.ForgotPassword -> ForgotPasswordScreen(
                onBack = { navigateTo(AuthRoute.AdminLogin) },
            )
        }
    }
}
