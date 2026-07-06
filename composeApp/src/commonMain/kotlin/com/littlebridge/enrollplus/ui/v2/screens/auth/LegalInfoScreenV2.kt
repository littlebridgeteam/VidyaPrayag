package com.littlebridge.enrollplus.ui.v2.screens.auth

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VLabel
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.VThemeRegistry
import com.littlebridge.enrollplus.ui.v2.theme.colored

/**
 * The single point of truth for the user-facing support address. Surfaced on the Help Desk tab and
 * used to build the `mailto:` intent. Keep in sync with the footer copy on [CommonLandingScreenV2].
 */
const val SUPPORT_EMAIL: String = "littlebridge.team@gmail.com"

/** Which legal/info document the screen opens on. */
enum class LegalDoc { Privacy, Terms, Help }

/**
 * LegalInfoScreenV2 — the public Privacy Policy / Terms of Service / Help Desk surface.
 *
 * Reached from the landing footer ("Privacy Policy", "Terms of Service", "Help Desk") and from the
 * "Terms & Privacy Policy" continue-footnote. A single screen with a [VTopTabs] switcher so the
 * three documents share one back-stack entry. Rendered entirely in the V* system (VColors tokens,
 * VType, VDimens) — Light tone, matching the unauthenticated funnel.
 *
 * The copy is intentionally **minimal and honest** for the current phase: it states what the app
 * actually does (school-scoped data, JWT auth, no third-party ad tracking) without fabricating
 * certifications. The Help Desk tab is the live support channel — a tap on the email opens the
 * device mail composer via [LocalUriHandler].
 */
@Composable
fun LegalInfoScreenV2(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initial: LegalDoc = LegalDoc.Privacy,
) = VTheme(themeDef = VThemeRegistry.resolve("light")) {
    val c = VTheme.colors
    val d = VTheme.dimens

    val tabPrivacy = appString(StringKeys.LEGAL_TAB_PRIVACY)
    val tabTerms = appString(StringKeys.LEGAL_TAB_TERMS)
    val tabHelp = appString(StringKeys.LEGAL_TAB_HELP)
    val tabs = listOf(tabPrivacy, tabTerms, tabHelp)
    var selected by remember {
        mutableStateOf(
            when (initial) {
                LegalDoc.Privacy -> tabPrivacy
                LegalDoc.Terms -> tabTerms
                LegalDoc.Help -> tabHelp
            },
        )
    }

    Column(
        modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // ── Header: back chevron + title ──
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BackChip(onClick = onBack)
            Text(
                appString(StringKeys.LEGAL_TITLE),
                style = VTheme.type.h4.colored(c.ink).copy(fontWeight = FontWeight.Bold),
            )
        }

        VTopTabs(tabs = tabs, selected = selected, onSelect = { selected = it })

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            when (selected) {
                tabPrivacy -> PrivacyContent()
                tabTerms -> TermsContent()
                else -> HelpDeskContent()
            }
            Spacer(Modifier.height(28.dp))
            Text(
                appString(StringKeys.LEGAL_FOOTER),
                style = VTheme.type.caption.colored(c.ink3),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(d.lg))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Documents — minimal, honest, current-phase copy.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PrivacyContent() {
    DocHeader(icon = VIcons.ShieldCheck, eyebrow = appString(StringKeys.LEGAL_PRIV_EYEBROW), title = appString(StringKeys.LEGAL_PRIV_TITLE))
    LastUpdated()
    Para(appString(StringKeys.LEGAL_PRIV_INTRO))
    SectionTitle(appString(StringKeys.LEGAL_PRIV_COLLECT_T))
    Bullet(appString(StringKeys.LEGAL_PRIV_COLLECT_1))
    Bullet(appString(StringKeys.LEGAL_PRIV_COLLECT_2))
    Bullet(appString(StringKeys.LEGAL_PRIV_COLLECT_3))
    Bullet(appString(StringKeys.LEGAL_PRIV_COLLECT_4))

    SectionTitle(appString(StringKeys.LEGAL_PRIV_USE_T))
    Bullet(appString(StringKeys.LEGAL_PRIV_USE_1))
    Bullet(appString(StringKeys.LEGAL_PRIV_USE_2))
    Bullet(appString(StringKeys.LEGAL_PRIV_USE_3))

    SectionTitle(appString(StringKeys.LEGAL_PRIV_NEVER_T))
    Bullet(appString(StringKeys.LEGAL_PRIV_NEVER_1))
    Bullet(appString(StringKeys.LEGAL_PRIV_NEVER_2))
    Bullet(appString(StringKeys.LEGAL_PRIV_NEVER_3))

    SectionTitle(appString(StringKeys.LEGAL_PRIV_SCOPED_T))
    Para(appString(StringKeys.LEGAL_PRIV_SCOPED_B))

    SectionTitle(appString(StringKeys.LEGAL_PRIV_RETENTION_T))
    Para(appString(StringKeys.LEGAL_PRIV_RETENTION_B))
}

@Composable
private fun TermsContent() {
    DocHeader(icon = VIcons.FileText, eyebrow = appString(StringKeys.LEGAL_TERMS_EYEBROW), title = appString(StringKeys.LEGAL_TERMS_TITLE))
    LastUpdated()
    Para(appString(StringKeys.LEGAL_TERMS_INTRO))
    SectionTitle(appString(StringKeys.LEGAL_TERMS_USE_T))
    Bullet(appString(StringKeys.LEGAL_TERMS_USE_1))
    Bullet(appString(StringKeys.LEGAL_TERMS_USE_2))
    Bullet(appString(StringKeys.LEGAL_TERMS_USE_3))

    SectionTitle(appString(StringKeys.LEGAL_TERMS_ACCOUNTS_T))
    Para(appString(StringKeys.LEGAL_TERMS_ACCOUNTS_B))

    SectionTitle(appString(StringKeys.LEGAL_TERMS_CONTENT_T))
    Bullet(appString(StringKeys.LEGAL_TERMS_CONTENT_1))
    Bullet(appString(StringKeys.LEGAL_TERMS_CONTENT_2))
    Bullet(appString(StringKeys.LEGAL_TERMS_CONTENT_3))

    SectionTitle(appString(StringKeys.LEGAL_TERMS_AVAIL_T))
    Para(appString(StringKeys.LEGAL_TERMS_AVAIL_B))

    SectionTitle(appString(StringKeys.LEGAL_TERMS_CHANGES_T))
    Para(appString(StringKeys.LEGAL_TERMS_CHANGES_B))

    SectionTitle(appString(StringKeys.LEGAL_TERMS_CONTACT_T))
    Para(appString(StringKeys.LEGAL_TERMS_CONTACT_B))
}

@Composable
private fun HelpDeskContent() {
    val c = VTheme.colors
    val uriHandler = LocalUriHandler.current

    DocHeader(icon = VIcons.Chat, eyebrow = appString(StringKeys.LEGAL_HELP_EYEBROW), title = appString(StringKeys.LEGAL_HELP_TITLE))
    Para(appString(StringKeys.LEGAL_HELP_INTRO))
    Spacer(Modifier.height(16.dp))

    // Primary contact card — taps open the device mail composer.
    VCard(
        onClick = {
            runCatching {
                uriHandler.openUri("mailto:$SUPPORT_EMAIL?subject=VidyaSetu%20Support")
            }
        },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(c.teal.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(VIcons.Mail, contentDescription = null, tint = c.tealDeep, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    appString(StringKeys.LEGAL_HELP_EMAIL),
                    style = VTheme.type.bodyStrong.colored(c.ink).copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(Modifier.height(2.dp))
                Text(SUPPORT_EMAIL, style = VTheme.type.caption.colored(c.tealDeep))
            }
            Icon(VIcons.ChevronRight, contentDescription = null, tint = c.ink3, modifier = Modifier.size(20.dp))
        }
    }

    Spacer(Modifier.height(20.dp))
    SectionTitle(appString(StringKeys.LEGAL_HELP_INCLUDE_T))
    Bullet(appString(StringKeys.LEGAL_HELP_INCLUDE_1))
    Bullet(appString(StringKeys.LEGAL_HELP_INCLUDE_2))
    Bullet(appString(StringKeys.LEGAL_HELP_INCLUDE_3))

    Spacer(Modifier.height(20.dp))
    SectionTitle(appString(StringKeys.LEGAL_HELP_FAQ_T))
    FaqRow(
        appString(StringKeys.LEGAL_HELP_FAQ_Q1),
        appString(StringKeys.LEGAL_HELP_FAQ_A1),
    )
    FaqRow(
        appString(StringKeys.LEGAL_HELP_FAQ_Q2),
        appString(StringKeys.LEGAL_HELP_FAQ_A2),
    )
    FaqRow(
        appString(StringKeys.LEGAL_HELP_FAQ_Q3),
        appString(StringKeys.LEGAL_HELP_FAQ_A3),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Small building blocks
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BackChip(onClick: () -> Unit) {
    val c = VTheme.colors
    val interaction = remember { MutableInteractionSource() }
    Box(
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(c.card)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(VIcons.ArrowLeft, contentDescription = appString(StringKeys.LEGAL_BACK), tint = c.ink, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun DocHeader(icon: ImageVector, eyebrow: String, title: String) {
    val c = VTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(c.lavender.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = c.navy, modifier = Modifier.size(22.dp))
        }
        Column {
            VLabel(eyebrow)
            Spacer(Modifier.height(2.dp))
            Text(title, style = VTheme.type.h2.colored(c.ink).copy(fontWeight = FontWeight.Bold))
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun LastUpdated() {
    val c = VTheme.colors
    Text(
        appString(StringKeys.LEGAL_LAST_UPDATED),
        style = VTheme.type.caption.colored(c.ink3),
    )
    Spacer(Modifier.height(14.dp))
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(Modifier.height(18.dp))
    Text(
        text,
        style = VTheme.type.h4.colored(VTheme.colors.ink).copy(fontWeight = FontWeight.Bold),
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun Para(text: String) {
    Text(
        text,
        style = VTheme.type.body.colored(VTheme.colors.ink2),
    )
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun Bullet(text: String) {
    val c = VTheme.colors
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier.padding(top = 8.dp).size(5.dp).clip(CircleShape).background(c.tealDeep),
        )
        Text(text, style = VTheme.type.body.colored(c.ink2), modifier = Modifier.weight(1f))
    }
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun FaqRow(question: String, answer: String) {
    val c = VTheme.colors
    Spacer(Modifier.height(10.dp))
    VCard(border = true, elevated = false, background = c.card) {
        Text(question, style = VTheme.type.bodyStrong.colored(c.ink).copy(fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(4.dp))
        Text(answer, style = VTheme.type.caption.colored(c.ink2))
    }
}
