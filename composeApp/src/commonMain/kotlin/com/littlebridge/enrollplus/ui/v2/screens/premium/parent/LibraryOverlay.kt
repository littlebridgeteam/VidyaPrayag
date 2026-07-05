package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.state.SelectedChildHolder
import com.littlebridge.enrollplus.feature.library.domain.model.LibraryBookDto
import com.littlebridge.enrollplus.feature.library.domain.model.LibraryIssueDto
import com.littlebridge.enrollplus.feature.library.presentation.ParentLibraryViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentLibraryViewModel = koinViewModel(),
    selectedChildHolder: SelectedChildHolder = koinInject(),
) {
    val state by viewModel.state.collectAsStateV2()
    val childId by selectedChildHolder.selectedChildId.collectAsStateV2()

    LaunchedEffect(childId) {
        childId?.let {
            viewModel.loadIssuedBooks(it)
            viewModel.loadReservations()
            viewModel.searchBooks()
        }
    }

    ParentOverlayScaffold(
        title = "Library",
        onBack = onBack,
        modifier = modifier,
    ) {
        // Search bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it); viewModel.searchBooks() },
            placeholder = { Text("Search books...", style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VColors.Primary,
                unfocusedBorderColor = VColors.SurfaceContainerHigh,
            ),
            shape = VShapes.Full,
        )

        Spacer(Modifier.height(16.dp))

        // Issued books section
        if (state.issuedBooks.isNotEmpty()) {
            Text(
                text = "Issued Books",
                style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(12.dp))
            state.issuedBooks.forEach { issue ->
                IssuedBookCard(issue = issue)
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(20.dp))
        }

        // Browse books
        Text(
            text = "Browse",
            style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
        )
        Spacer(Modifier.height(12.dp))

        VStateHostPremium(
            loading = state.isLoading && state.books.isEmpty(),
            error = state.error,
            isEmpty = state.books.isEmpty() && !state.isLoading,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = "No books found",
            emptyIcon = Icons.Filled.LibraryBooks,
            onRetry = { viewModel.searchBooks() },
            skeleton = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    repeat(4) { VShimmerBoxPremium(height = 80.dp, shape = VShapes.Lg) }
                }
            },
        ) {
            state.books.forEach { book ->
                BookCard(
                    book = book,
                    onReserve = { viewModel.reserveBook(book.id) },
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun IssuedBookCard(issue: LibraryIssueDto) {
    val isOverdue = issue.returnDate == null && issue.fineAmount > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(if (isOverdue) VColors.ErrorContainer.copy(alpha = 0.2f) else VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VColors.PrimaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Book, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(18.dp))
                }
                Text(
                    text = issue.bookTitle,
                    style = VTypography.BodyLarge.copy(color = VColors.OnSurface),
                )
            }
            if (issue.status == "issued") {
                Box(
                    modifier = Modifier
                        .clip(VShapes.Full)
                        .background(VColors.WarmOrange.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "Issued",
                        style = VTypography.ThreadTime.copy(color = VColors.WarmOrange),
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Due: ${issue.dueDate}",
            style = VTypography.ThreadPreview.copy(color = if (isOverdue) VColors.Error else VColors.OnSurfaceVariant),
        )
        if (issue.fineAmount > 0) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Fine: ${issue.fineAmount} (${issue.fineStatus})",
                style = VTypography.ThreadTime.copy(color = VColors.Error),
            )
        }
    }
}

@Composable
private fun BookCard(book: LibraryBookDto, onReserve: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = VTypography.BodyLarge.copy(color = VColors.OnSurface),
                )
                if (!book.author.isNullOrBlank()) {
                    val author = book.author!!
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = author,
                        style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                    )
                }
            }
            Text(
                text = "${book.availableCopies}/${book.totalCopies}",
                style = VTypography.QuickStatValue.copy(
                    color = if (book.availableCopies > 0) VColors.Primary else VColors.Error,
                ),
            )
        }
        if (!book.category.isNullOrBlank()) {
            val category = book.category!!
            Spacer(Modifier.height(6.dp))
            Text(
                text = category,
                style = VTypography.ThreadTime.copy(color = VColors.Outline),
            )
        }
        if (book.availableCopies == 0) {
            Spacer(Modifier.height(12.dp))
            VPrimaryButton(
                text = "Reserve",
                onClick = onReserve,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
