package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for BrandingSettingsScreen.
 *
 * Requires a connected device or emulator.
 * Run with: ./gradlew :composeApp:connectedDevDebugAndroidTest
 *
 * These tests verify:
 * - Screen renders with expected sections
 * - Color picker labels are visible
 * - Asset upload rows are displayed
 * - Save/Reset buttons are present
 */
@RunWith(AndroidJUnit4::class)
class BrandingSettingsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    @Test
    fun brandingScreen_showsHeaderTitle() {
        composeRule.setContent {
            BrandingSettingsScreen(onBack = {})
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Branding Kit").assertIsDisplayed()
    }

    @Test
    fun brandingScreen_showsColorPickerLabels() {
        composeRule.setContent {
            BrandingSettingsScreen(onBack = {})
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Brand Colors").assertIsDisplayed()
        composeRule.onNodeWithText("Primary Color").assertIsDisplayed()
        composeRule.onNodeWithText("Secondary Color").assertIsDisplayed()
        composeRule.onNodeWithText("Accent Color").assertIsDisplayed()
    }

    @Test
    fun brandingScreen_showsSaveColorsButton() {
        composeRule.setContent {
            BrandingSettingsScreen(onBack = {})
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Save Colors").assertIsDisplayed()
    }

    @Test
    fun brandingScreen_showsBrandAssetsSection() {
        composeRule.setContent {
            BrandingSettingsScreen(onBack = {})
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Brand Assets").assertIsDisplayed()
        composeRule.onNodeWithText("Logo").assertIsDisplayed()
        composeRule.onNodeWithText("Favicon").assertIsDisplayed()
        composeRule.onNodeWithText("App Icon").assertIsDisplayed()
        composeRule.onNodeWithText("Splash Screen").assertIsDisplayed()
        composeRule.onNodeWithText("Login Background").assertIsDisplayed()
    }

    @Test
    fun brandingScreen_showsResetButton() {
        composeRule.setContent {
            BrandingSettingsScreen(onBack = {})
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Reset to Defaults").assertIsDisplayed()
    }

    @Test
    fun brandingScreen_showsSubdomainSection() {
        composeRule.setContent {
            BrandingSettingsScreen(onBack = {})
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText("Custom Subdomain").assertIsDisplayed()
    }
}
