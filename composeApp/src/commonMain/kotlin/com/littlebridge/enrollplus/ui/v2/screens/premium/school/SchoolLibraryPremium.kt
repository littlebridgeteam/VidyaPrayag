package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.library.presentation.SchoolLibraryViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VStatCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolLibraryPremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SchoolLibraryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
        viewModel.loadCategories()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Library", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.dashboard == null && !state.isLoading,
            emptyTitle = "No library data",
            onRetry = { viewModel.loadDashboard() },
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 4)
                }
            },
        ) {
            val dash = state.dashboard ?: return@VStateHostPremium

            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VGradientHeroPremium(
                    title = "Library Dashboard",
                    subtitle = "${dash.totalBooks} books | ${dash.totalCopies} copies",
                    stats = listOf(
                        HeroStatPremium("${dash.totalBooks}", "Books"),
                        HeroStatPremium("${dash.availableCopies}", "Available"),
                        HeroStatPremium("${dash.issuedCopies}", "Issued"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Overview", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VStatCardPremium(value = "${dash.overdueBooks}", label = "Overdue", onClick = {}, icon = VIcons.AlertCircle)
                    VStatCardPremium(value = "${dash.activeReservations}", label = "Reservations", onClick = {}, icon = VIcons.Bookmark)
                    VStatCardPremium(value = "${dash.lostBooks}", label = "Lost", onClick = {}, icon = VIcons.AlertTriangle)

                    Text("Categories", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.categories.forEach { c ->
                        VListTilePremium(
                            title = c.name,
                            subtitle = "Order: ${c.displayOrder}",
                            onClick = {},
                            leadingIcon = VIcons.Bookmark,
                        )
                    }

                    if (state.books.isNotEmpty()) {
                        Text("Recent Books", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        state.books.take(10).forEach { b ->
                            VListTilePremium(
                                title = b.title,
                                subtitle = "${b.author} | ${b.availableCopies}/${b.totalCopies} available",
                                onClick = {},
                                leadingIcon = VIcons.BookOpen,
                            )
                        }
                    }
                }
            }
        }
    }
}
