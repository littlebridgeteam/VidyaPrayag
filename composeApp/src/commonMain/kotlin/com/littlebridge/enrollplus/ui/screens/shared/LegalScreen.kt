package com.littlebridge.enrollplus.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.components.VBackHeader
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography

@Composable
fun LegalScreen(
    title: String,
    sections: List<LegalSection>,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding(),
    ) {
        VBackHeader(title = title, onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 48.dp),
        ) {
            Text(
                text = title,
                style = VTypography.h2,
                color = VColors.ink,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Last updated: July 2026",
                style = VTypography.caption,
                color = VColors.ink3,
            )
            Spacer(Modifier.height(24.dp))

            sections.forEach { section ->
                Text(
                    text = section.heading,
                    style = VTypography.body.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
                )
                Text(
                    text = section.body,
                    style = VTypography.bodySmall,
                    color = VColors.ink2,
                    lineHeight = VTypography.bodySmall.lineHeight,
                )
            }
        }
    }
}

data class LegalSection(
    val heading: String,
    val body: String,
)

@Composable
fun TermsConditionScreen(onBack: () -> Unit) {
    LegalScreen(
        title = "Terms & Conditions",
        onBack = onBack,
        sections = listOf(
            LegalSection(
                heading = "1. Acceptance of Terms",
                body = "By accessing or using the Enroll+ platform, you agree to be bound by these Terms & Conditions. If you do not agree with any part of these terms, you must not use the service.",
            ),
            LegalSection(
                heading = "2. Service Description",
                body = "Enroll+ is a school management platform that connects parents, teachers, and school administrators. The service includes attendance tracking, fee management, progress reports, communication tools, and library management.",
            ),
            LegalSection(
                heading = "3. User Accounts",
                body = "You are responsible for maintaining the confidentiality of your account credentials and for all activities that occur under your account. You must immediately notify us of any unauthorized use of your account.",
            ),
            LegalSection(
                heading = "4. Acceptable Use",
                body = "Users must not use the platform to send unsolicited communications, upload malicious content, attempt to gain unauthorized access to other accounts, or use the service for any illegal purpose.",
            ),
            LegalSection(
                heading = "5. Data & Privacy",
                body = "Your use of the service is also governed by our Privacy Policy. By using Enroll+, you consent to the data collection and usage practices described in our Privacy Policy.",
            ),
            LegalSection(
                heading = "6. Fee Payments",
                body = "Fee payments processed through the platform are handled by third-party payment processors. Enroll+ is not liable for payment processing errors or disputes. Refunds are subject to the respective school's refund policy.",
            ),
            LegalSection(
                heading = "7. Termination",
                body = "We reserve the right to suspend or terminate accounts that violate these terms. Schools may also request termination of their institution's account at any time.",
            ),
            LegalSection(
                heading = "8. Limitation of Liability",
                body = "Enroll+ is provided on an \"as is\" basis. We are not liable for indirect, incidental, or consequential damages arising from the use of the service.",
            ),
            LegalSection(
                heading = "9. Changes to Terms",
                body = "We may update these terms from time to time. Users will be notified of significant changes. Continued use of the service after changes constitutes acceptance of the new terms.",
            ),
            LegalSection(
                heading = "10. Contact",
                body = "For questions about these Terms & Conditions, contact us at legal@enrollplus.in",
            ),
        ),
    )
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    LegalScreen(
        title = "Privacy Policy",
        onBack = onBack,
        sections = listOf(
            LegalSection(
                heading = "1. Information We Collect",
                body = "We collect information you provide directly, including name, phone number, email address, and school details. We also collect usage data such as app interactions, device information, and log data.",
            ),
            LegalSection(
                heading = "2. How We Use Your Information",
                body = "Your information is used to provide and improve the service, process fee payments, send attendance and progress notifications, facilitate communication between parents and schools, and for security and fraud prevention.",
            ),
            LegalSection(
                heading = "3. Information Sharing",
                body = "We do not sell your personal information. We share data with your school for educational purposes, with payment processors for fee transactions, and with service providers who help us operate the platform.",
            ),
            LegalSection(
                heading = "4. Data Security",
                body = "We use industry-standard encryption and security measures to protect your data. Access to personal information is restricted to authorized personnel only. Despite our efforts, no method of transmission over the internet is 100% secure.",
            ),
            LegalSection(
                heading = "5. Data Retention",
                body = "We retain your information for as long as your account is active or as needed to provide the service. School-related data is retained according to the school's data retention policy and applicable regulations.",
            ),
            LegalSection(
                heading = "6. Your Rights",
                body = "You have the right to access, correct, or delete your personal information. You can request data export or account deletion by contacting us at privacy@enrollplus.in",
            ),
            LegalSection(
                heading = "7. Children's Privacy",
                body = "Enroll+ is designed for parents and school staff. We do not knowingly collect personal information directly from children. Student data is provided by parents and schools for educational purposes only.",
            ),
            LegalSection(
                heading = "8. Notifications",
                body = "You can manage notification preferences within the app. We send push notifications for attendance, fees, progress reports, and school announcements. You may disable notifications at any time through your device settings.",
            ),
            LegalSection(
                heading = "9. Changes to This Policy",
                body = "We may update this Privacy Policy from time to time. We will notify you of significant changes through the app or via email. We encourage you to review this policy periodically.",
            ),
            LegalSection(
                heading = "10. Contact Us",
                body = "For privacy-related questions or requests, contact us at privacy@enrollplus.in",
            ),
        ),
    )
}
