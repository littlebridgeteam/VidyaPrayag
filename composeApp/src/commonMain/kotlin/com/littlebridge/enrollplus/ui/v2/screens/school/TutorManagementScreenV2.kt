package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VLabel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored

@Composable
fun TutorManagementScreenV2(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = VTheme.colors

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Tutor Management", style = VTheme.type.h1.colored(c.ink))

        VCard {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VLabel("AI Tutor Configuration")
                Text(
                    "The AI Tutor module provides personalised tutoring for students across all subjects. " +
                        "Manage tutor availability, configure AI provider settings, and monitor student engagement " +
                        "from the server-side tutor dashboard.",
                    style = VTheme.type.body.colored(c.ink2),
                )
            }
        }

        VCard {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VLabel("Tutor Features")
                FeatureRow("Doubt Resolution", "AI-powered doubt resolution with RAG-based context retrieval")
                FeatureRow("Pace Tracking", "Monitors syllabus coverage pace and generates alerts for behind/ahead classes")
                FeatureRow("Narrator", "Generates personalised report card narratives using AI")
                FeatureRow("Caseworker", "Tracks at-risk students with AI-driven interventions")
            }
        }

        VCard {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VLabel("Server Configuration")
                Text(
                    "Tutor modules are configured via environment variables:\n" +
                        "  • AI_ENCRYPTION_KEY — encrypts provider API keys\n" +
                        "  • OPENAI_API_BASE — OpenAI-compatible endpoint\n" +
                        "  • TUTOR_MODEL — model name for tutor responses\n" +
                        "  • NARRATOR_MODEL — model for report card narratives\n" +
                        "  • EMBEDDING_MODEL — model for RAG embeddings",
                    style = VTheme.type.caption.colored(c.ink2),
                )
            }
        }

        VCard {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VLabel("Module Status")
                Text(
                    "Tutor modules are registered at server startup via TutorModuleRegistry. " +
                        "Each module (Sense, Triage, Learn, Act, Insights) can be individually enabled/disabled " +
                        "through the module status API endpoint.",
                    style = VTheme.type.body.colored(c.ink2),
                )
            }
        }
    }
}

@Composable
private fun FeatureRow(title: String, description: String) {
    val c = VTheme.colors
    Column(Modifier.fillMaxWidth()) {
        Text(title, style = VTheme.type.bodyStrong.colored(c.ink))
        Text(description, style = VTheme.type.caption.colored(c.ink2))
    }
}
