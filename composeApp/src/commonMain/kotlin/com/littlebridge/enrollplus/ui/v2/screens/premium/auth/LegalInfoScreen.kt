package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

enum class LegalDocPremium { Privacy, Terms, Help }

const val SUPPORT_EMAIL = "littlebridge.team@gmail.com"

/**
 * Premium legal info screen — Privacy Policy / Terms of Service / Help Desk.
 * Tabbed interface with M3 Expressive tokens.
 */
@Composable
fun LegalInfoScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initial: String = "Privacy",
) = PremiumTheme(isDark = false) {
    val tabs = listOf("Privacy", "Terms", "Help")
    var selected by remember {
        mutableStateOf(when (initial.lowercase()) {
            "terms" -> 1
            "help" -> 2
            else -> 0
        })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val backInteraction = remember { MutableInteractionSource() }
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VColors.SurfaceContainerHigh)
                    .pressScale(backInteraction, pressedScale = 0.9f)
                    .clickable(interactionSource = backInteraction, indication = null, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VColors.OnSurface, modifier = Modifier.size(20.dp))
            }
            Text(
                "Legal & Support",
                style = VTypography.SectionHeader.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold),
            )
        }

        // Tab selector
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tabs.forEachIndexed { index, label ->
                val isActive = selected == index
                val tabInteraction = remember { MutableInteractionSource() }
                Box(
                    Modifier
                        .weight(1f)
                        .clip(VShapes.Full)
                        .background(if (isActive) VColors.Primary else VColors.SurfaceContainerLow)
                        .pressScale(tabInteraction, pressedScale = 0.95f)
                        .clickable(interactionSource = tabInteraction, indication = null) { selected = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        style = VTypography.NavLabel.copy(
                            color = if (isActive) VColors.OnPrimary else VColors.OnSurfaceVariant,
                            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        ),
                    )
                }
            }
        }

        // Content
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            when (selected) {
                0 -> PrivacyContent()
                1 -> TermsContent()
                else -> HelpContent()
            }
            Spacer(Modifier.height(28.dp))
            Text(
                "Last updated: January 2026. Enroll+ is a product of LittleBridge Technologies.",
                style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DocHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, eyebrow: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.size(44.dp).clip(VShapes.Lg).background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.OnPrimaryContainer, modifier = Modifier.size(22.dp))
        }
        Column {
            Text(eyebrow, style = VTypography.Eyebrow.copy(color = VColors.Primary))
            Spacer(Modifier.height(2.dp))
            Text(title, style = VTypography.SectionHeader.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(Modifier.height(18.dp))
    Text(text, style = VTypography.SectionHeader.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun Para(text: String) {
    Text(text, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun Bullet(text: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.padding(top = 7.dp).size(5.dp).clip(CircleShape).background(VColors.Primary))
        Text(text, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant), modifier = Modifier.weight(1f))
    }
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun PrivacyContent() {
    DocHeader(Icons.Filled.PrivacyTip, "PRIVACY", "Privacy Policy")
    Para("Your privacy matters to us. This policy explains what data we collect and how we use it.")
    SectionTitle("Information We Collect")
    Bullet("Student name, class, section, and roll number — provided by your school")
    Bullet("Parent/guardian contact details (phone, email) for communication")
    Bullet("Attendance, grades, and fee records — synced from your school's system")
    Bullet("App usage analytics to improve user experience")
    SectionTitle("How We Use Your Data")
    Bullet("To show real-time updates about your child's school activities")
    Bullet("To send fee reminders and payment confirmations")
    Bullet("To facilitate communication between parents and teachers")
    SectionTitle("What We Never Do")
    Bullet("Sell your data to third parties")
    Bullet("Show third-party advertisements")
    Bullet("Share data outside your school's scope")
    SectionTitle("Data Security")
    Para("All data is encrypted in transit (TLS 1.2+) and at rest. Access is scoped to your school. We use JWT-based authentication with short-lived tokens.")
    SectionTitle("Data Retention")
    Para("Data is retained as long as your child is enrolled. After graduation or transfer, data is archived per school policy.")
}

@Composable
private fun TermsContent() {
    DocHeader(Icons.Filled.Description, "TERMS", "Terms of Service")
    Para("By using Enroll+, you agree to these terms. Please read them carefully.")
    SectionTitle("Acceptable Use")
    Bullet("Use the app only for school-related communication and activities")
    Bullet("Do not share your login credentials with others")
    Bullet("Do not attempt to access data outside your authorized scope")
    SectionTitle("Account Responsibilities")
    Para("You are responsible for keeping your login secure. Contact your school administrator if you suspect unauthorized access.")
    SectionTitle("Content")
    Bullet("Schools are responsible for the accuracy of data they publish")
    Bullet("Users must not post inappropriate or offensive content")
    Bullet("We reserve the right to remove violating content")
    SectionTitle("Service Availability")
    Para("We strive for 99.9% uptime but do not guarantee uninterrupted service. Maintenance windows are scheduled during off-hours.")
    SectionTitle("Changes to Terms")
    Para("We may update these terms periodically. Continued use after changes constitutes acceptance.")
    SectionTitle("Contact")
    Para("Questions? Email us at $SUPPORT_EMAIL")
}

@Composable
private fun HelpContent() {
    val uriHandler = LocalUriHandler.current
    DocHeader(Icons.AutoMirrored.Filled.Chat, "SUPPORT", "Help Desk")
    Para("We're here to help. Reach out and we'll get back to you within 24 hours.")
    Spacer(Modifier.height(16.dp))

    // Contact card
    val emailInteraction = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .pressScale(emailInteraction, pressedScale = 0.97f)
            .clickable(interactionSource = emailInteraction, indication = null) {
                runCatching { uriHandler.openUri("mailto:$SUPPORT_EMAIL?subject=Enroll+%20Support") }
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(44.dp).clip(CircleShape).background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Email, contentDescription = null, tint = VColors.OnPrimaryContainer, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text("Email Support", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(2.dp))
            Text(SUPPORT_EMAIL, style = VTypography.NavLabel.copy(color = VColors.Primary))
        }
    }

    Spacer(Modifier.height(20.dp))
    SectionTitle("Include in Your Email")
    Bullet("Your registered phone number or email")
    Bullet("Your child's name and school")
    Bullet("A description of the issue you're facing")
    Spacer(Modifier.height(20.dp))
    SectionTitle("Frequently Asked Questions")
    FaqItem("How do I link my child?", "Go to Profile > Link Child, search for your school, and enter your child's roll number.")
    FaqItem("I forgot my password", "Tap 'Forgot password?' on the login screen. You'll receive a reset link via email.")
    FaqItem("How do I change my language?", "Go to Profile > Settings > Language and select your preferred language.")
}

@Composable
private fun FaqItem(question: String, answer: String) {
    Spacer(Modifier.height(10.dp))
    Column(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        Text(question, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(4.dp))
        Text(answer, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}
