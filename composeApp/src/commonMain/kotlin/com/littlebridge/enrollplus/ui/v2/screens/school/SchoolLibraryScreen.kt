package com.littlebridge.enrollplus.ui.v2.screens.school

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.library.domain.model.*
import com.littlebridge.enrollplus.feature.library.presentation.SchoolLibraryState
import com.littlebridge.enrollplus.feature.library.presentation.SchoolLibraryViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheet
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheetHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VLabel
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonDashboard
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.VErrorState
import com.littlebridge.enrollplus.ui.v2.screens.library.BookCardSkeleton
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.util.formatDecimal
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

private enum class LibraryTab(val label: String) {
    Dashboard("Dashboard"),
    Books("Books"),
    Copies("Copies"),
    Issues("Issues"),
    QuickIssue("Quick Issue"),
    BulkReturn("Bulk Return"),
    Categories("Categories"),
    Audit("Audit"),
    Announcements("Announcements"),
    Acquisition("Acquisition"),
    Reservations("Reservations"),
    History("History"),
    More("More"),
    Settings("Settings"),
}

@Composable
fun SchoolLibraryScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SchoolLibraryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var activeTab by remember { mutableStateOf(LibraryTab.Dashboard) }

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
        viewModel.loadCategories()
        viewModel.loadSettings()
        viewModel.loadAnnouncements(true)
    }

    // Onboarding redirect: if library has no books and no categories, suggest onboarding
    val needsOnboarding = state.dashboard != null &&
        state.dashboard?.totalBooks == 0 &&
        state.categories.isEmpty() &&
        !state.isActionLoading

    // Auto-redirect to Dashboard tab when onboarding is needed so the user
    // always sees the onboarding prompt regardless of which tab they land on.
    LaunchedEffect(needsOnboarding) {
        if (needsOnboarding && activeTab != LibraryTab.Dashboard) {
            activeTab = LibraryTab.Dashboard
        }
    }

    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VTheme.colors.surface),
    ) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        com.littlebridge.enrollplus.ui.v2.components.VBackHeader(title = "Library", onBack = onBack, pinRouteId = "overlay_library")

        if (state.isOffline) {
            Row(
                modifier = Modifier.fillMaxWidth().background(VTheme.colors.gold.copy(alpha = 0.1f)).padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text("⚠️", style = VTheme.type.caption)
                Text(
                    if (state.isStaleData) "Offline — showing cached data" else "Offline — check your connection",
                    style = VTheme.type.caption.copy(color = VTheme.colors.gold),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LibraryTab.entries.forEach { tab ->
                VBadge(
                    text = tab.label,
                    tone = if (activeTab == tab) VBadgeTone.Accent else VBadgeTone.Neutral,
                    modifier = Modifier.clickable { activeTab = tab },
                )
            }
        }

        VPullRefresh(
            isRefreshing = state.isLoading,
            onRefresh = {
                when (activeTab) {
                    LibraryTab.Dashboard -> viewModel.loadDashboard()
                    LibraryTab.Books -> viewModel.searchBooks(1)
                    LibraryTab.Copies -> viewModel.loadCopies("")
                    LibraryTab.Issues -> viewModel.loadIssues(1)
                    LibraryTab.Categories -> viewModel.loadCategories()
                    LibraryTab.Audit -> viewModel.loadAuditLog(1)
                    LibraryTab.Announcements -> viewModel.loadAnnouncements(true)
                    LibraryTab.Acquisition -> viewModel.loadAcquisitionRequests(null)
                    LibraryTab.History -> viewModel.loadBookHistory("")
                    else -> viewModel.loadDashboard()
                }
            },
        ) {
            when (activeTab) {
                LibraryTab.Dashboard -> DashboardTab(state, viewModel, needsOnboarding)
                LibraryTab.Books -> BooksTab(state, viewModel)
                LibraryTab.Copies -> CopiesTab(state, viewModel)
                LibraryTab.Issues -> IssuesTab(state, viewModel)
                LibraryTab.QuickIssue -> QuickIssueTab(state, viewModel)
                LibraryTab.BulkReturn -> BulkReturnTab(state, viewModel)
                LibraryTab.Categories -> CategoriesTab(state, viewModel)
                LibraryTab.Audit -> AuditTab(state, viewModel)
                LibraryTab.Announcements -> AnnouncementsTab(state, viewModel)
                LibraryTab.Acquisition -> AcquisitionTab(state, viewModel)
                LibraryTab.Reservations -> ReservationsTab(state, viewModel)
                LibraryTab.History -> HistoryTab(state, viewModel)
                LibraryTab.More -> MoreTab(state, viewModel)
                LibraryTab.Settings -> SettingsTab(state, viewModel)
            }
        }
    }

    if (state.actionMessage != null) {
        Box(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            VBadge(text = state.actionMessage!!, tone = VBadgeTone.Accent)
        }
        LaunchedEffect(state.actionMessage) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearActionMessage()
        }
    }
    }
}

@Composable
private fun DashboardTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel, needsOnboarding: Boolean = false) {
    
    LaunchedEffect(Unit) { viewModel.loadDashboard() }

    if (state.isLoading && state.dashboard == null) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SkeletonDashboard()
        }
        return
    }

    if (state.error != null && state.dashboard == null) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            VErrorState(message = state.error ?: "", onRetry = { viewModel.loadDashboard() })
        }
        return
    }

    val d = state.dashboard
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Dashboard", style = VTheme.type.h2.copy(color = VTheme.colors.ink))

        if (needsOnboarding) {
            VCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Welcome to Library Management!", style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.violetInk))
                    Text("Your library is empty. Run the onboarding wizard to set up categories and add your first books.", style = VTheme.type.body.copy(color = VTheme.colors.ink2))
                    VButton(
                        text = "Run Onboarding Wizard",
                        onClick = { viewModel.runOnboarding() },
                        full = true,
                        tone = VButtonTone.Lavender,
                        size = VButtonSize.Md,
                        loading = state.isActionLoading,
                    )
                }
            }
        }

        // Active announcement banner
        val activeAnnouncement = state.announcements.firstOrNull { it.isActive }
        if (activeAnnouncement != null) {
            com.littlebridge.enrollplus.ui.v2.screens.library.AnnouncementBanner(
                title = activeAnnouncement.title,
                message = activeAnnouncement.message,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Total Books", d?.totalBooks?.toString() ?: "0", Modifier.weight(1f))
            MetricCard("Total Copies", d?.totalCopies?.toString() ?: "0", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Available", d?.availableCopies?.toString() ?: "0", Modifier.weight(1f), color = VTheme.colors.success)
            MetricCard("Issued", d?.issuedCopies?.toString() ?: "0", Modifier.weight(1f), color = VTheme.colors.violet)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Overdue", d?.overdueBooks?.toString() ?: "0", Modifier.weight(1f), color = VTheme.colors.gold)
            MetricCard("Lost", d?.lostBooks?.toString() ?: "0", Modifier.weight(1f), color = VTheme.colors.error)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Reservations", d?.activeReservations?.toString() ?: "0", Modifier.weight(1f))
            MetricCard("Damaged", d?.damagedBooks?.toString() ?: "0", Modifier.weight(1f), color = VTheme.colors.gold)
        }

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Outstanding Fines", style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
                Text("${d?.outstandingFinesCount ?: 0} pending", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
                Text(
                    "₹${formatDecimal(d?.outstandingFinesAmount ?: 0.0)}",
                    style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp).copy(color = VTheme.colors.error).copy(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
                )
                Spacer(Modifier.height(4.dp))
                Text("Collected this month", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
                Text(
                    "₹${formatDecimal(d?.finesCollectedThisMonth ?: 0.0)}",
                    style = VTheme.type.bodySmall.copy(color = VTheme.colors.success).copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
private fun BooksTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        var showAddBook by remember { mutableStateOf(false) }
    var showCoverUpload by remember { mutableStateOf<String?>(null) }
    var coverUrl by remember { mutableStateOf("") }
    var showIssueBook by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.searchBooks(1) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Books", style = VTheme.type.h2.copy(color = VTheme.colors.ink))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VInput(
                value = state.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = "Search books",
                modifier = Modifier.weight(1f),
            )
            VButton(
                text = "+ Add Book",
                onClick = { showAddBook = true },
                variant = VButtonVariant.Secondary,
                tone = VButtonTone.Lavender,
                size = VButtonSize.Sm,
            )
        }

        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VLabel("Category:")
            VBadge(
                text = state.searchCategory ?: "All",
                tone = if (state.searchCategory == null) VBadgeTone.Accent else VBadgeTone.Neutral,
                modifier = Modifier.clickable { viewModel.updateSearchCategory(null) },
            )
            state.categories.forEach { cat ->
                VBadge(
                    text = cat.name,
                    tone = if (state.searchCategory == cat.name) VBadgeTone.Accent else VBadgeTone.Neutral,
                    modifier = Modifier.clickable { viewModel.updateSearchCategory(cat.name) },
                )
            }
        }

        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VLabel("Available:")
            listOf("all" to "All", "available" to "Available Only").forEach { (key, label) ->
                VBadge(
                    text = label,
                    tone = if (state.searchAvailability == key) VBadgeTone.Accent else VBadgeTone.Neutral,
                    modifier = Modifier.clickable { viewModel.updateSearchAvailability(key) },
                )
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VLabel("Sort:")
            listOf("newest" to "Newest", "title" to "Title A-Z", "author" to "Author", "popular" to "Popular").forEach { (key, label) ->
                VBadge(
                    text = label,
                    tone = if (state.searchSortBy == key) VBadgeTone.Accent else VBadgeTone.Neutral,
                    modifier = Modifier.clickable { viewModel.updateSearchSortBy(key) },
                )
            }
        }

        VButton(
            text = "Search",
            onClick = { viewModel.searchBooks(1) },
            full = true,
            tone = VButtonTone.Lavender,
            size = VButtonSize.Md,
        )

        if (state.error != null && state.books.isEmpty()) {
            VErrorState(message = state.error ?: "", onRetry = { viewModel.searchBooks(1) })
            return@Column
        }

        if (state.isLoading && state.books.isEmpty()) {
            repeat(3) { BookCardSkeleton() }
            return@Column
        }

        if (state.books.isEmpty()) {
            VEmptyState(title = "No books found", body = "Try a different search query or add a new book.")
            return@Column
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
        ) {
            itemsIndexed(state.books, key = { _, it -> it.id }) { index, book ->
                VCard(modifier = Modifier.staggeredItemEntrance(index, state.books.isNotEmpty())) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            com.littlebridge.enrollplus.ui.v2.screens.library.BookCover(
                                title = book.title,
                                author = book.author,
                                coverUrl = book.coverUrl,
                                modifier = Modifier.size(64.dp, 96.dp),
                            )
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(book.title, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
                                    if (book.isArchived) VBadge(text = "Archived", tone = VBadgeTone.Neutral)
                                }
                                book.author?.let { Text(it, style = VTheme.type.caption.copy(color = VTheme.colors.ink2)) }
                                book.isbn?.let { Text("ISBN: $it", style = VTheme.type.caption.copy(color = VTheme.colors.ink3)) }
                                if (book.seriesName != null) {
                                    Text("${book.seriesName} #${book.seriesNumber ?: 1}", style = VTheme.type.caption.copy(color = VTheme.colors.ink3))
                                }
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            com.littlebridge.enrollplus.ui.v2.screens.library.AvailabilityBadge(
                                availableCopies = book.availableCopies,
                                totalCopies = book.totalCopies,
                            )
                            book.category?.let { VBadge(text = it, tone = VBadgeTone.Neutral) }
                            if (book.language != "en") VBadge(text = book.language, tone = VBadgeTone.Neutral)
                        }
                        com.littlebridge.enrollplus.ui.v2.screens.library.TagChips(tags = book.tags)
                        Row(
                            Modifier.fillMaxWidth().padding(top = 4.dp).horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            VButton(
                                text = if (book.isArchived) "Unarchive" else "Archive",
                                onClick = {
                                    if (book.isArchived) viewModel.unarchiveBook(book.id)
                                    else viewModel.archiveBook(book.id)
                                },
                                variant = VButtonVariant.Secondary,
                                tone = VButtonTone.Sky,
                                size = VButtonSize.Sm,
                                loading = state.isActionLoading,
                            )
                            VButton(
                                text = "Set Cover",
                                onClick = { showCoverUpload = book.id; coverUrl = book.coverUrl ?: "" },
                                variant = VButtonVariant.Secondary,
                                tone = VButtonTone.Sand,
                                size = VButtonSize.Sm,
                            )
                            VButton(
                                text = "Issue",
                                onClick = { showIssueBook = book.id },
                                variant = VButtonVariant.Secondary,
                                tone = VButtonTone.Mint,
                                size = VButtonSize.Sm,
                                enabled = book.availableCopies > 0 && !book.isArchived,
                            )
                        }
                    }
                }
            }
            // Pagination
            if (state.books.size >= 20 && state.books.size < state.booksTotal) {
                item {
                    VButton(
                        text = "Load More (${state.booksTotal - state.books.size} remaining)",
                        onClick = { viewModel.searchBooks(state.booksPage + 1) },
                        full = true,
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Sky,
                        size = VButtonSize.Sm,
                        loading = state.isLoading,
                    )
                }
            }
        }
    }

    // Add Book Dialog
    if (showAddBook) {
        AddBookSheet(
            categories = state.categories,
            onDismiss = { showAddBook = false },
            onCreate = { req ->
                viewModel.createBook(req)
                showAddBook = false
            },
        )
    }

    // Issue Book Dialog
    if (showIssueBook != null) {
        IssueBookSheet(
            bookId = showIssueBook!!,
            onDismiss = { showIssueBook = null },
            onIssue = { req ->
                viewModel.issueBook(req)
                showIssueBook = null
            },
        )
    }

    // Cover Upload Dialog
    if (showCoverUpload != null) {
        VBottomSheet(
            visible = true,
            onDismiss = { showCoverUpload = null },
        ) {
            VBottomSheetHeader(title = "Set Cover URL")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(value = coverUrl, onValueChange = { coverUrl = it }, label = "Cover image URL", modifier = Modifier.fillMaxWidth())
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = "Cancel",
                    onClick = { showCoverUpload = null },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Ghost,
                )
                VButton(
                    text = "Save",
                    onClick = {
                        if (coverUrl.isNotBlank()) {
                            viewModel.uploadCover(showCoverUpload!!, coverUrl)
                        }
                        showCoverUpload = null
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun AddBookSheet(
    categories: List<LibraryCategoryDto>,
    onDismiss: () -> Unit,
    onCreate: (CreateBookRequest) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<String?>(null) }
    var totalCopies by remember { mutableStateOf("1") }
    var shelfLocation by remember { mutableStateOf("") }
    var replacementCost by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("en") }
    var synopsis by remember { mutableStateOf("") }

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        VBottomSheetHeader(title = "Add New Book")
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            VInput(value = title, onValueChange = { title = it }, label = "Title *", modifier = Modifier.fillMaxWidth())
            VInput(value = author, onValueChange = { author = it }, label = "Author", modifier = Modifier.fillMaxWidth())
            VInput(value = isbn, onValueChange = { isbn = it }, label = "ISBN", modifier = Modifier.fillMaxWidth())
            VInput(value = publisher, onValueChange = { publisher = it }, label = "Publisher", modifier = Modifier.fillMaxWidth())
            VInput(value = totalCopies, onValueChange = { totalCopies = it }, label = "Total Copies", modifier = Modifier.fillMaxWidth())
            VInput(value = shelfLocation, onValueChange = { shelfLocation = it }, label = "Shelf Location", modifier = Modifier.fillMaxWidth())
            VInput(value = replacementCost, onValueChange = { replacementCost = it }, label = "Replacement Cost (₹)", modifier = Modifier.fillMaxWidth())
            VInput(value = language, onValueChange = { language = it }, label = "Language", modifier = Modifier.fillMaxWidth())
            VInput(value = synopsis, onValueChange = { synopsis = it }, label = "Synopsis", modifier = Modifier.fillMaxWidth())
            Text("Category", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                VBadge(
                    text = "None",
                    tone = if (category == null) VBadgeTone.Accent else VBadgeTone.Neutral,
                    modifier = Modifier.clickable { category = null },
                )
                categories.forEach { cat ->
                    VBadge(
                        text = cat.name,
                        tone = if (category == cat.name) VBadgeTone.Accent else VBadgeTone.Neutral,
                        modifier = Modifier.clickable { category = cat.name },
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VButton(
                text = "Cancel",
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                variant = VButtonVariant.Ghost,
            )
            VButton(
                text = "Create",
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(
                            CreateBookRequest(
                                title = title,
                                author = author.ifBlank { null },
                                isbn = isbn.ifBlank { null },
                                publisher = publisher.ifBlank { null },
                                category = category,
                                totalCopies = totalCopies.toIntOrNull() ?: 1,
                                shelfLocation = shelfLocation.ifBlank { null },
                                replacementCost = replacementCost.toDoubleOrNull(),
                                language = language.ifBlank { "en" },
                                synopsis = synopsis.ifBlank { null },
                            ),
                        )
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun IssueBookSheet(
    bookId: String,
    onDismiss: () -> Unit,
    onIssue: (IssueBookRequest) -> Unit,
) {
    var borrowerId by remember { mutableStateOf("") }
    var borrowerName by remember { mutableStateOf("") }
    var borrowerType by remember { mutableStateOf("student") }
    var copyId by remember { mutableStateOf("") }

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        VBottomSheetHeader(title = "Issue Book")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            VInput(value = borrowerId, onValueChange = { borrowerId = it }, label = "Borrower ID *", modifier = Modifier.fillMaxWidth())
            VInput(value = borrowerName, onValueChange = { borrowerName = it }, label = "Borrower Name *", modifier = Modifier.fillMaxWidth())
            VInput(value = copyId, onValueChange = { copyId = it }, label = "Copy ID (optional)", modifier = Modifier.fillMaxWidth())
            Text("Borrower Type", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("student" to "Student", "teacher" to "Teacher").forEach { (key, label) ->
                    VBadge(
                        text = label,
                        tone = if (borrowerType == key) VBadgeTone.Accent else VBadgeTone.Neutral,
                        modifier = Modifier.clickable { borrowerType = key },
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VButton(
                text = "Cancel",
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                variant = VButtonVariant.Ghost,
            )
            VButton(
                text = "Issue",
                onClick = {
                    if (borrowerId.isNotBlank() && borrowerName.isNotBlank()) {
                        onIssue(
                            IssueBookRequest(
                                bookId = bookId,
                                copyId = copyId.ifBlank { null },
                                borrowerId = borrowerId,
                                borrowerType = borrowerType,
                                borrowerName = borrowerName,
                            ),
                        )
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun IssuesTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
    
    LaunchedEffect(Unit) { viewModel.loadIssues(1) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Issues", style = VTheme.type.h2.copy(color = VTheme.colors.ink))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(null, "issued", "returned", "lost").forEach { status ->
                VBadge(
                    text = status ?: "All",
                    tone = if (state.issuesStatusFilter == status) VBadgeTone.Accent else VBadgeTone.Neutral,
                    modifier = Modifier.clickable { viewModel.updateIssuesStatusFilter(status) },
                )
            }
        }

        if (state.error != null && state.issues.isEmpty()) {
            VErrorState(message = state.error ?: "", onRetry = { viewModel.loadIssues(1) })
            return@Column
        }

        if (state.isLoading && state.issues.isEmpty()) {
            repeat(3) { BookCardSkeleton() }
            return@Column
        }

        if (state.issues.isEmpty()) {
            VEmptyState(title = "No issues found", body = "Issues will appear here once books are issued.")
            return@Column
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
        ) {
            itemsIndexed(state.issues, key = { _, it -> it.id }) { index, issue ->
                IssueCard(issue, state.isActionLoading, viewModel, modifier = Modifier.staggeredItemEntrance(index, state.issues.isNotEmpty()))
            }
            if (state.issues.size >= 20 && state.issues.size < state.issuesTotal) {
                item {
                    VButton(
                        text = "Load More (${state.issuesTotal - state.issues.size} remaining)",
                        onClick = { viewModel.loadIssues(state.issuesPage + 1) },
                        full = true,
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Sky,
                        size = VButtonSize.Sm,
                        loading = state.isLoading,
                    )
                }
            }
        }
    }
}

@Composable
private fun IssueCard(
    issue: LibraryIssueDto,
    isActionLoading: Boolean,
    viewModel: SchoolLibraryViewModel,
    modifier: Modifier = Modifier,
) {
        var showReturnDialog by remember { mutableStateOf(false) }
    var showMarkLostConfirm by remember { mutableStateOf(false) }
    var showWaiveDialog by remember { mutableStateOf(false) }

    VCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(issue.bookTitle, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
            Text(issue.borrowerName, style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
            if (issue.status == "issued") {
                com.littlebridge.enrollplus.ui.v2.screens.library.DueDateBadge(dueDate = issue.dueDate)
            } else {
                Text("Due: ${issue.dueDate}", style = VTheme.type.caption.copy(color = VTheme.colors.ink3))
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                VBadge(
                    text = issue.status.replaceFirstChar { it.uppercase() },
                    tone = when (issue.status) {
                        "issued" -> VBadgeTone.Accent
                        "returned" -> VBadgeTone.Success
                        "lost" -> VBadgeTone.Danger
                        else -> VBadgeTone.Neutral
                    },
                )
                if (issue.fineAmount > 0) {
                    VBadge(
                        text = "₹${formatDecimal(issue.fineAmount)} ${issue.fineStatus}",
                        tone = when (issue.fineStatus) {
                            "pending" -> VBadgeTone.Warning
                            "paid" -> VBadgeTone.Success
                            "waived" -> VBadgeTone.Neutral
                            else -> VBadgeTone.Neutral
                        },
                    )
                }
            }

            if (issue.status == "issued") {
                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    VButton(
                        text = "Return",
                        onClick = { showReturnDialog = true },
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Mint,
                        size = VButtonSize.Sm,
                        loading = isActionLoading,
                    )
                    VButton(
                        text = "Renew",
                        onClick = { viewModel.renewBook(issue.id) },
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Lavender,
                        size = VButtonSize.Sm,
                        loading = isActionLoading,
                    )
                    VButton(
                        text = "Mark Lost",
                        onClick = { showMarkLostConfirm = true },
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Rose,
                        size = VButtonSize.Sm,
                    )
                }
            }

            if (issue.fineStatus == "pending") {
                com.littlebridge.enrollplus.ui.v2.screens.library.FineMeter(
                    currentFine = issue.fineAmount,
                    replacementCost = null,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                )
                Row(
                    Modifier.fillMaxWidth().padding(top = 4.dp).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    VButton(
                        text = "Pay Fine",
                        onClick = { viewModel.payFine(issue.id) },
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Sand,
                        size = VButtonSize.Sm,
                        loading = isActionLoading,
                    )
                    VButton(
                        text = "Waive Fine",
                        onClick = { showWaiveDialog = true },
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Sky,
                        size = VButtonSize.Sm,
                        loading = isActionLoading,
                    )
                }
            }
        }
    }

    // Return Dialog with condition selector + damage notes
    if (showReturnDialog) {
        var condition by remember { mutableStateOf("good") }
        var damageNotes by remember { mutableStateOf("") }
        VBottomSheet(
            visible = true,
            onDismiss = { showReturnDialog = false },
        ) {
            VBottomSheetHeader(title = "Return Book")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(issue.bookTitle, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
                Text("Select condition:", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("good" to "Good", "fair" to "Fair", "damaged" to "Damaged").forEach { (key, label) ->
                        VBadge(
                            text = label,
                            tone = if (condition == key) VBadgeTone.Accent else VBadgeTone.Neutral,
                            modifier = Modifier.clickable { condition = key },
                        )
                    }
                }
                if (condition == "damaged") {
                    VInput(value = damageNotes, onValueChange = { damageNotes = it }, label = "Damage notes", modifier = Modifier.fillMaxWidth())
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = "Cancel",
                    onClick = { showReturnDialog = false },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Ghost,
                )
                VButton(
                    text = "Confirm Return",
                    onClick = {
                        viewModel.returnBook(issue.id, condition, damageNotes.ifBlank { null })
                        showReturnDialog = false
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    // Mark Lost Confirmation Dialog
    if (showMarkLostConfirm) {
        VConfirmDialog(
            visible = true,
            title = "Mark as Lost?",
            message = "This will mark \"${issue.bookTitle}\" as lost and may incur a fine for the borrower.",
            confirmLabel = "Mark Lost",
            onConfirm = {
                viewModel.markLost(issue.id)
                showMarkLostConfirm = false
            },
            onDismiss = { showMarkLostConfirm = false },
        )
    }

    // Waive Fine Dialog with reason
    if (showWaiveDialog) {
        var waiveReason by remember { mutableStateOf("") }
        VBottomSheet(
            visible = true,
            onDismiss = { showWaiveDialog = false },
        ) {
            VBottomSheetHeader(title = "Waive Fine?")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Fine: \u20b9${formatDecimal(issue.fineAmount)} for \"${issue.bookTitle}\"", style = VTheme.type.body.copy(color = VTheme.colors.ink2))
                VInput(value = waiveReason, onValueChange = { waiveReason = it }, label = "Reason for waiver *", modifier = Modifier.fillMaxWidth())
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = "Cancel",
                    onClick = { showWaiveDialog = false },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Ghost,
                )
                VButton(
                    text = "Waive Fine",
                    onClick = {
                        viewModel.waiveFine(issue.id, waiveReason)
                        showWaiveDialog = false
                    },
                    modifier = Modifier.weight(1f),
                    enabled = waiveReason.isNotBlank(),
                )
            }
        }
    }
}

@Composable
private fun SettingsTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        val s = state.settings

    var defaultLoanDays by remember(s) { mutableStateOf(s?.defaultLoanDays?.toString() ?: "14") }
    var finePerDay by remember(s) { mutableStateOf(s?.finePerDay?.toString() ?: "1.0") }
    var maxBooksPerStudent by remember(s) { mutableStateOf(s?.maxBooksPerStudent?.toString() ?: "3") }
    var maxRenewals by remember(s) { mutableStateOf(s?.maxRenewals?.toString() ?: "2") }
    var reservationTimeoutDays by remember(s) { mutableStateOf(s?.reservationTimeoutDays?.toString() ?: "7") }
    var dueReminderDays by remember(s) { mutableStateOf(s?.dueReminderDays?.toString() ?: "2") }
    var fineCapEnabled by remember(s) { mutableStateOf(s?.fineCapEnabled ?: true) }
    var quickIssueEnabled by remember(s) { mutableStateOf(s?.quickIssueEnabled ?: true) }
    var bulkReturnEnabled by remember(s) { mutableStateOf(s?.bulkReturnEnabled ?: true) }
    var leaderboardEnabled by remember(s) { mutableStateOf(s?.leaderboardEnabled ?: false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Library Settings", style = VTheme.type.h2.copy(color = VTheme.colors.ink))

        if (s == null) {
            Text("Loading settings...", style = VTheme.type.body.copy(color = VTheme.colors.ink2))
            return@Column
        }

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                VInput(value = defaultLoanDays, onValueChange = { defaultLoanDays = it }, label = "Default Loan Days", modifier = Modifier.fillMaxWidth())
                VInput(value = finePerDay, onValueChange = { finePerDay = it }, label = "Fine Per Day (₹)", modifier = Modifier.fillMaxWidth())
                VInput(value = maxBooksPerStudent, onValueChange = { maxBooksPerStudent = it }, label = "Max Books Per Student", modifier = Modifier.fillMaxWidth())
                VInput(value = maxRenewals, onValueChange = { maxRenewals = it }, label = "Max Renewals", modifier = Modifier.fillMaxWidth())
                VInput(value = reservationTimeoutDays, onValueChange = { reservationTimeoutDays = it }, label = "Reservation Timeout (days)", modifier = Modifier.fillMaxWidth())
                VInput(value = dueReminderDays, onValueChange = { dueReminderDays = it }, label = "Due Reminder (days before)", modifier = Modifier.fillMaxWidth())

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    VLabel("Fine Cap Enabled")
                    VBadge(
                        text = if (fineCapEnabled) "Yes" else "No",
                        tone = if (fineCapEnabled) VBadgeTone.Accent else VBadgeTone.Neutral,
                        modifier = Modifier.clickable { fineCapEnabled = !fineCapEnabled },
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    VLabel("Quick Issue Enabled")
                    VBadge(
                        text = if (quickIssueEnabled) "Yes" else "No",
                        tone = if (quickIssueEnabled) VBadgeTone.Accent else VBadgeTone.Neutral,
                        modifier = Modifier.clickable { quickIssueEnabled = !quickIssueEnabled },
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    VLabel("Bulk Return Enabled")
                    VBadge(
                        text = if (bulkReturnEnabled) "Yes" else "No",
                        tone = if (bulkReturnEnabled) VBadgeTone.Accent else VBadgeTone.Neutral,
                        modifier = Modifier.clickable { bulkReturnEnabled = !bulkReturnEnabled },
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    VLabel("Leaderboard Enabled")
                    VBadge(
                        text = if (leaderboardEnabled) "Yes" else "No",
                        tone = if (leaderboardEnabled) VBadgeTone.Accent else VBadgeTone.Neutral,
                        modifier = Modifier.clickable { leaderboardEnabled = !leaderboardEnabled },
                    )
                }
            }
        }

        VButton(
            text = "Save Settings",
            onClick = {
                viewModel.updateSettings(
                    UpdateSettingsRequest(
                        defaultLoanDays = defaultLoanDays.toIntOrNull(),
                        finePerDay = finePerDay.toDoubleOrNull(),
                        maxBooksPerStudent = maxBooksPerStudent.toIntOrNull(),
                        maxRenewals = maxRenewals.toIntOrNull(),
                        reservationTimeoutDays = reservationTimeoutDays.toIntOrNull(),
                        dueReminderDays = dueReminderDays.toIntOrNull(),
                        fineCapEnabled = fineCapEnabled,
                        quickIssueEnabled = quickIssueEnabled,
                        bulkReturnEnabled = bulkReturnEnabled,
                        leaderboardEnabled = leaderboardEnabled,
                    ),
                )
            },
            full = true,
            tone = VButtonTone.Lavender,
            size = VButtonSize.Md,
            loading = state.isActionLoading,
        )
        VButton(
            text = "Reset to Defaults",
            onClick = {
                defaultLoanDays = "14"
                finePerDay = "1.0"
                maxBooksPerStudent = "3"
                maxRenewals = "2"
                reservationTimeoutDays = "7"
                dueReminderDays = "2"
                fineCapEnabled = true
                quickIssueEnabled = true
                bulkReturnEnabled = true
                leaderboardEnabled = false
            },
            full = true,
            variant = VButtonVariant.Secondary,
            tone = VButtonTone.Sand,
            size = VButtonSize.Sm,
        )
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier, color: Color? = null) {
        VCard(modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            VLabel(label)
            Text(
                value,
                style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp).copy(color = color ?: VTheme.colors.ink).copy(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun SettingRow(label: String, value: String) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        VLabel(label)
        Text(value, style = VTheme.type.body.copy(color = VTheme.colors.ink).copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun ReservationsTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        var bookId by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Reservations", style = VTheme.type.h2.copy(color = VTheme.colors.ink))

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(value = bookId, onValueChange = { bookId = it }, label = "Book ID", modifier = Modifier.fillMaxWidth())
                VButton(
                    text = "Load Reservations",
                    onClick = { if (bookId.isNotBlank()) viewModel.loadReservationsForBook(bookId) },
                    full = true,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Sm,
                )
            }
        }

        if (state.reservations.isEmpty()) {
            VEmptyState(title = "No reservations", body = "Enter a book ID to view its reservation queue.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(state.reservations, key = { it.id }) { res ->
                VCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(res.bookTitle, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
                        Text(res.reservedByName, style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            VBadge(
                                text = res.reservedByType.replaceFirstChar { it.uppercase() },
                                tone = VBadgeTone.Neutral,
                            )
                            VBadge(
                                text = res.status.replaceFirstChar { it.uppercase() },
                                tone = when (res.status) {
                                    "pending" -> VBadgeTone.Warning
                                    "notified" -> VBadgeTone.Accent
                                    "fulfilled" -> VBadgeTone.Success
                                    "cancelled" -> VBadgeTone.Neutral
                                    else -> VBadgeTone.Neutral
                                },
                            )
                        }
                        res.waitlistPosition?.let { Text("Waitlist #$it", style = VTheme.type.caption.copy(color = VTheme.colors.ink3)) }
                        Text("Reserved: ${res.createdAt}", style = VTheme.type.caption.copy(color = VTheme.colors.ink3))

                        if (res.status == "pending" || res.status == "notified") {
                            VButton(
                                text = "Fulfill",
                                onClick = { viewModel.fulfillReservation(res.id) },
                                variant = VButtonVariant.Secondary,
                                tone = VButtonTone.Mint,
                                size = VButtonSize.Sm,
                                loading = state.isActionLoading,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Quick Issue Tab ──────────────────────────────────────────────────────

@Composable
private fun QuickIssueTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        var barcode by remember { mutableStateOf("") }
    var borrowerId by remember { mutableStateOf("") }
    var borrowerName by remember { mutableStateOf("") }
    var borrowerType by remember { mutableStateOf("student") }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Quick Issue", style = VTheme.type.h2.copy(color = VTheme.colors.ink))
        Text("Scan or enter a barcode to instantly issue a book.", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(value = barcode, onValueChange = { barcode = it }, label = "Barcode", modifier = Modifier.fillMaxWidth())
                VInput(value = borrowerId, onValueChange = { borrowerId = it }, label = "Borrower ID", modifier = Modifier.fillMaxWidth())
                VInput(value = borrowerName, onValueChange = { borrowerName = it }, label = "Borrower Name", modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("student", "teacher", "parent").forEach { type ->
                        VBadge(
                            text = type.replaceFirstChar { it.uppercase() },
                            tone = if (borrowerType == type) VBadgeTone.Accent else VBadgeTone.Neutral,
                            modifier = Modifier.clickable { borrowerType = type },
                        )
                    }
                }
                VButton(
                    text = "Issue",
                    onClick = {
                        if (barcode.isNotBlank() && borrowerId.isNotBlank() && borrowerName.isNotBlank()) {
                            viewModel.quickIssue(QuickIssueRequest(barcode, borrowerId, borrowerType, borrowerName))
                            barcode = ""
                        }
                    },
                    full = true,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Md,
                    loading = state.isActionLoading,
                )
            }
        }
    }
}

// ── Bulk Return Tab ──────────────────────────────────────────────────────

@Composable
private fun BulkReturnTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        val barcodes = remember { mutableStateListOf<String>() }
    var currentBarcode by remember { mutableStateOf("") }
    var showConfirm by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Bulk Return", style = VTheme.type.h2.copy(color = VTheme.colors.ink))
        Text("Scan barcodes sequentially, then end the session.", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(value = currentBarcode, onValueChange = { currentBarcode = it }, label = "Scan barcode", modifier = Modifier.fillMaxWidth())
                VButton(
                    text = "Add",
                    onClick = {
                        if (currentBarcode.isNotBlank()) {
                            barcodes.add(currentBarcode)
                            currentBarcode = ""
                        }
                    },
                    full = true,
                    tone = VButtonTone.Mint,
                    size = VButtonSize.Md,
                )
            }
        }

        if (barcodes.isNotEmpty()) {
            Text("${barcodes.size} barcode(s) scanned", style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                items(barcodes.indices.toList()) { idx ->
                    VCard {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("${idx + 1}. ${barcodes[idx]}", style = VTheme.type.body.copy(color = VTheme.colors.ink))
                            VBadge(text = "Remove", tone = VBadgeTone.Danger, modifier = Modifier.clickable { barcodes.removeAt(idx) })
                        }
                    }
                }
            }
            VButton(
                text = "End Session & Return All",
                onClick = { showConfirm = true },
                full = true,
                tone = VButtonTone.Lavender,
                size = VButtonSize.Md,
                loading = state.isActionLoading,
            )
        } else {
            VEmptyState(title = "No barcodes scanned", body = "Scan barcodes above to start a bulk return session.")
        }

        if (showConfirm) {
            VConfirmDialog(
                visible = true,
                title = "Confirm Bulk Return",
                message = "Return ${barcodes.size} book(s)?",
                confirmLabel = "Return All",
                onConfirm = {
                    viewModel.bulkReturn(barcodes.toList())
                    barcodes.clear()
                    showConfirm = false
                },
                onDismiss = { showConfirm = false },
            )
        }
    }
}

// ── Categories Tab ───────────────────────────────────────────────────────

@Composable
private fun CategoriesTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        var showCreate by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newColor by remember { mutableStateOf("#2196F3") }
    var newIcon by remember { mutableStateOf("menu_book") }
    var showDeleteConfirm by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadCategories() }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Categories", style = VTheme.type.h2.copy(color = VTheme.colors.ink))
            VButton(text = "+ Add", onClick = { showCreate = true }, variant = VButtonVariant.Secondary, tone = VButtonTone.Lavender, size = VButtonSize.Sm)
        }

        if (state.categories.isEmpty()) {
            if (state.error != null) {
                VErrorState(message = state.error ?: "", onRetry = { viewModel.loadCategories() })
                return@Column
            }
            VEmptyState(title = "No categories", body = "Create categories to organize your library.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            itemsIndexed(state.categories, key = { _, cat -> cat.id }) { index, cat ->
                VCard {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(parseHexColor(cat.color)))
                            Text(cat.icon, style = VTheme.type.caption.copy(color = VTheme.colors.ink3))
                            Text(cat.name, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            VBadge(
                                text = "▲",
                                tone = VBadgeTone.Neutral,
                                modifier = Modifier.clickable {
                                    if (index > 0) {
                                        viewModel.reorderCategories(
                                            state.categories.mapIndexed { i, category ->
                                                category.id to if (i == index) index - 1 else if (i == index - 1) index else i
                                            },
                                        )
                                    }
                                },
                            )
                            VBadge(
                                text = "▼",
                                tone = VBadgeTone.Neutral,
                                modifier = Modifier.clickable {
                                    if (index < state.categories.lastIndex) {
                                        viewModel.reorderCategories(
                                            state.categories.mapIndexed { i, category ->
                                                category.id to if (i == index) index + 1 else if (i == index + 1) index else i
                                            },
                                        )
                                    }
                                },
                            )
                            VBadge(text = "Delete", tone = VBadgeTone.Danger, modifier = Modifier.clickable { showDeleteConfirm = cat.id })
                        }
                    }
                }
            }
        }

        if (showCreate) {
            VBottomSheet(
                visible = true,
                onDismiss = { showCreate = false },
            ) {
                VBottomSheetHeader(title = "New Category")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    VInput(value = newName, onValueChange = { newName = it }, label = "Name", modifier = Modifier.fillMaxWidth())
                    VInput(value = newColor, onValueChange = { newColor = it }, label = "Color (hex)", modifier = Modifier.fillMaxWidth())
                    VInput(value = newIcon, onValueChange = { newIcon = it }, label = "Icon name", modifier = Modifier.fillMaxWidth())
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VButton(
                        text = "Cancel",
                        onClick = { showCreate = false },
                        modifier = Modifier.weight(1f),
                        variant = VButtonVariant.Ghost,
                    )
                    VButton(
                        text = "Create",
                        onClick = {
                            viewModel.createCategory(CreateCategoryRequest(newName, newColor, newIcon))
                            newName = ""; showCreate = false
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        if (showDeleteConfirm != null) {
            val catName = state.categories.find { it.id == showDeleteConfirm }?.name ?: ""
            VConfirmDialog(
                visible = true,
                title = "Delete Category?",
                message = "Are you sure you want to delete \"$catName\"? Books in this category will remain but lose their category label.",
                confirmLabel = "Delete",
                onConfirm = {
                    viewModel.deleteCategory(showDeleteConfirm!!)
                    showDeleteConfirm = null
                },
                onDismiss = { showDeleteConfirm = null },
            )
        }
    }
}

// ── Audit Tab ────────────────────────────────────────────────────────────

@Composable
private fun AuditTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
    
    LaunchedEffect(Unit) { viewModel.loadAuditLog(1) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Audit Trail", style = VTheme.type.h2.copy(color = VTheme.colors.ink))

        if (state.error != null && state.auditLog.isEmpty()) {
            VErrorState(message = state.error ?: "", onRetry = { viewModel.loadAuditLog(1) })
            return@Column
        }

        if (state.isLoading && state.auditLog.isEmpty()) {
            repeat(3) { BookCardSkeleton() }
            return@Column
        }

        if (state.auditLog.isEmpty()) {
            VEmptyState(title = "No audit logs", body = "Audit entries will appear here as actions are performed.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(state.auditLog, key = { it.id }) { log ->
                VCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(log.action, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
                            VBadge(text = log.entityType, tone = VBadgeTone.Neutral)
                        }
                        Text("By: ${log.actorName}", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
                        Text(log.createdAt, style = VTheme.type.caption.copy(color = VTheme.colors.ink3))
                    }
                }
            }
        }
    }
}

// ── Announcements Tab ────────────────────────────────────────────────────

@Composable
private fun AnnouncementsTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        var showCreate by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var showDeleteAnnouncement by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadAnnouncements(false) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Announcements", style = VTheme.type.h2.copy(color = VTheme.colors.ink))
            VButton(text = "+ New", onClick = { showCreate = true }, variant = VButtonVariant.Secondary, tone = VButtonTone.Lavender, size = VButtonSize.Sm)
        }

        if (state.announcements.isEmpty()) {
            if (state.error != null) {
                VErrorState(message = state.error ?: "", onRetry = { viewModel.loadAnnouncements(false) })
                return@Column
            }
            VEmptyState(title = "No announcements", body = "Post library announcements and notices here.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(state.announcements, key = { it.id }) { ann ->
                VCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(ann.title, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
                            if (!ann.isActive) VBadge(text = "Inactive", tone = VBadgeTone.Neutral)
                        }
                        Text(ann.message, style = VTheme.type.body.copy(color = VTheme.colors.ink2))
                        Text("Expires: ${ann.expiresAt ?: "Never"}", style = VTheme.type.caption.copy(color = VTheme.colors.ink3))
                        Row(
                            Modifier.fillMaxWidth().padding(top = 4.dp).horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            VButton(
                                text = if (ann.isActive) "Deactivate" else "Activate",
                                onClick = { viewModel.toggleAnnouncement(ann.id, ann.isActive) },
                                variant = VButtonVariant.Secondary,
                                tone = if (ann.isActive) VButtonTone.Sand else VButtonTone.Mint,
                                size = VButtonSize.Sm,
                                loading = state.isActionLoading,
                            )
                            VButton(
                                text = "Delete",
                                onClick = { showDeleteAnnouncement = ann.id },
                                variant = VButtonVariant.Secondary,
                                tone = VButtonTone.Rose,
                                size = VButtonSize.Sm,
                            )
                        }
                    }
                }
            }
        }

        if (showCreate) {
            VBottomSheet(
                visible = true,
                onDismiss = { showCreate = false },
            ) {
                VBottomSheetHeader(title = "New Announcement")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    VInput(value = title, onValueChange = { title = it }, label = "Title", modifier = Modifier.fillMaxWidth())
                    VInput(value = body, onValueChange = { body = it }, label = "Body", modifier = Modifier.fillMaxWidth())
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VButton(
                        text = "Cancel",
                        onClick = { showCreate = false },
                        modifier = Modifier.weight(1f),
                        variant = VButtonVariant.Ghost,
                    )
                    VButton(
                        text = "Post",
                        onClick = {
                            viewModel.createAnnouncement(CreateAnnouncementRequest(title, body))
                            title = ""; body = ""; showCreate = false
                            // Note: CreateAnnouncementRequest uses 'message' field, but we pass 'body' as positional
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        if (showDeleteAnnouncement != null) {
            val annTitle = state.announcements.find { it.id == showDeleteAnnouncement }?.title ?: ""
            VConfirmDialog(
                visible = true,
                title = "Delete Announcement?",
                message = "Are you sure you want to delete \"$annTitle\"? This cannot be undone.",
                confirmLabel = "Delete",
                onConfirm = {
                    viewModel.deleteAnnouncement(showDeleteAnnouncement!!)
                    showDeleteAnnouncement = null
                },
                onDismiss = { showDeleteAnnouncement = null },
            )
        }
    }
}

// ── Acquisition Tab ──────────────────────────────────────────────────────

@Composable
private fun AcquisitionTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        var statusFilter by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadAcquisitionRequests(null) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Acquisition Requests", style = VTheme.type.h2.copy(color = VTheme.colors.ink))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(null, "pending", "approved", "ordered", "received").forEach { status ->
                val isSelected: Boolean = statusFilter == status
                VBadge(
                    text = status ?: "All",
                    tone = if (isSelected) VBadgeTone.Accent else VBadgeTone.Neutral,
                    modifier = Modifier.clickable {
                        statusFilter = status
                        viewModel.loadAcquisitionRequests(status)
                    },
                )
            }
        }

        if (state.acquisitionRequests.isEmpty()) {
            if (state.error != null) {
                VErrorState(message = state.error ?: "", onRetry = { viewModel.loadAcquisitionRequests(null) })
                return@Column
            }
            VEmptyState(title = "No requests", body = "Acquisition requests from teachers will appear here.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(state.acquisitionRequests, key = { it.id }) { req ->
                VCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(req.title, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
                        req.author?.let { Text("Author: $it", style = VTheme.type.caption.copy(color = VTheme.colors.ink2)) }
                        req.isbn?.let { Text("ISBN: $it", style = VTheme.type.caption.copy(color = VTheme.colors.ink2)) }
                        req.publisher?.let { Text("Publisher: $it", style = VTheme.type.caption.copy(color = VTheme.colors.ink2)) }
                        req.reason?.let { Text("Reason: $it", style = VTheme.type.caption.copy(color = VTheme.colors.ink2)) }
                        Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            VBadge(
                                text = req.status.replaceFirstChar { it.uppercase() },
                                tone = when (req.status) {
                                    "pending" -> VBadgeTone.Warning
                                    "approved" -> VBadgeTone.Accent
                                    "ordered" -> VBadgeTone.Neutral
                                    "received" -> VBadgeTone.Success
                                    else -> VBadgeTone.Neutral
                                },
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (req.status == "pending") {
                                    VButton(text = "Approve", onClick = { viewModel.updateAcquisitionStatus(req.id, "approve") }, variant = VButtonVariant.Secondary, tone = VButtonTone.Mint, size = VButtonSize.Sm, loading = state.isActionLoading)
                                }
                                if (req.status == "approved") {
                                    VButton(text = "Order", onClick = { viewModel.updateAcquisitionStatus(req.id, "order") }, variant = VButtonVariant.Secondary, tone = VButtonTone.Lavender, size = VButtonSize.Sm, loading = state.isActionLoading)
                                }
                                if (req.status == "ordered") {
                                    VButton(text = "Receive", onClick = { viewModel.updateAcquisitionStatus(req.id, "receive") }, variant = VButtonVariant.Secondary, tone = VButtonTone.Mint, size = VButtonSize.Sm, loading = state.isActionLoading)
                                }
                                if (req.status == "received") {
                                    VButton(text = "Convert to Book", onClick = { viewModel.convertAcquisitionToBook(req.id) }, variant = VButtonVariant.Secondary, tone = VButtonTone.Lavender, size = VButtonSize.Sm, loading = state.isActionLoading)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── More Tab (Import, Export, Onboarding, Trending, Repair) ──────────────

@Composable
private fun MoreTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        var showImport by remember { mutableStateOf(false) }
    var importJson by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadTrending()
        viewModel.loadRepairCopies()
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("More", style = VTheme.type.h2.copy(color = VTheme.colors.ink))

        Text("Quick Actions", style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(text = "Run Onboarding Wizard", onClick = { viewModel.runOnboarding() }, full = true, tone = VButtonTone.Lavender, size = VButtonSize.Sm, loading = state.isActionLoading)
                VButton(text = "Export Catalog (CSV)", onClick = { viewModel.exportCatalog() }, full = true, tone = VButtonTone.Mint, size = VButtonSize.Sm, loading = state.isActionLoading)
                VButton(text = "Import Books (JSON)", onClick = { showImport = true }, full = true, tone = VButtonTone.Sky, size = VButtonSize.Sm)
            }
        }

        if (state.trending.isNotEmpty()) {
            Text("Trending Books", style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.trending, key = { it.bookId }) { book ->
                    VCard {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            com.littlebridge.enrollplus.ui.v2.screens.library.BookCover(
                                title = book.title,
                                author = book.author,
                                coverUrl = book.coverUrl,
                                modifier = Modifier.size(48.dp, 72.dp),
                            )
                            Text(book.title, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink), maxLines = 1)
                            book.author?.let { Text(it, style = VTheme.type.caption.copy(color = VTheme.colors.ink2), maxLines = 1) }
                            VBadge(text = "${book.issueCount} issues", tone = VBadgeTone.Accent)
                        }
                    }
                }
            }
        }

        Text("Repair Queue", style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
        if (state.repairCopies.isEmpty()) {
            VEmptyState(title = "No books in repair", body = "Damaged copies will appear here.")
        } else {
            state.repairCopies.forEach { copy ->
                VCard {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(copy.bookTitle, style = VTheme.type.body.copy(color = VTheme.colors.ink))
                            Text("Copy #${copy.copyId}", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
                        }
                        VButton(
                            text = "Mark Repaired",
                            onClick = { viewModel.repairCopy(copy.copyId) },
                            variant = VButtonVariant.Secondary,
                            tone = VButtonTone.Mint,
                            size = VButtonSize.Sm,
                            loading = state.isActionLoading,
                        )
                    }
                }
            }
        }

        if (showImport) {
            VBottomSheet(
                visible = true,
                onDismiss = { showImport = false },
            ) {
                VBottomSheetHeader(title = "Import Books (JSON)")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Paste JSON array of book objects:", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
                    VInput(value = importJson, onValueChange = { importJson = it }, label = "JSON", modifier = Modifier.fillMaxWidth())
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VButton(
                        text = "Cancel",
                        onClick = { showImport = false },
                        modifier = Modifier.weight(1f),
                        variant = VButtonVariant.Ghost,
                    )
                    VButton(
                        text = "Import",
                        onClick = {
                            runCatching {
                                val rows = kotlinx.serialization.json.Json.decodeFromString<List<CreateBookRequest>>(importJson)
                                viewModel.bulkImport(rows)
                            }
                            importJson = ""; showImport = false
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private fun parseHexColor(hex: String): Color {
    return runCatching {
        val clean = hex.removePrefix("#")
        val r = clean.substring(0, 2).toInt(16)
        val g = clean.substring(2, 4).toInt(16)
        val b = clean.substring(4, 6).toInt(16)
        Color(r, g, b)
    }.getOrDefault(Color.Gray)
}

// ── Copies Tab ────────────────────────────────────────────────────────────

@Composable
private fun CopiesTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        var bookIdInput by remember { mutableStateOf("") }
    var showAddCopy by remember { mutableStateOf(false) }
    var newCopyCondition by remember { mutableStateOf("new") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Book Copies", style = VTheme.type.h2.copy(color = VTheme.colors.ink))
        Text("View and manage individual copy records for a book.", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(
                    value = bookIdInput,
                    onValueChange = { bookIdInput = it },
                    label = "Book ID",
                    modifier = Modifier.fillMaxWidth(),
                )
                VButton(
                    text = "Load Copies",
                    onClick = { if (bookIdInput.isNotBlank()) viewModel.loadCopies(bookIdInput) },
                    full = true,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Sm,
                )
            }
        }

        if (state.copies.isEmpty()) {
            VEmptyState(title = "No copies loaded", body = "Enter a book ID above to view its copies.")
            return@Column
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("${state.copies.size} copies", style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
            VButton(
                text = "+ Add Copy",
                onClick = { showAddCopy = true },
                variant = VButtonVariant.Secondary,
                tone = VButtonTone.Mint,
                size = VButtonSize.Sm,
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(state.copies, key = { it.id }) { copy ->
                VCard {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Copy #${copy.copyNumber}", style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
                            copy.barcode?.let { Text("Barcode: $it", style = VTheme.type.caption.copy(color = VTheme.colors.ink2)) }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            VBadge(
                                text = copy.condition.replaceFirstChar { it.uppercase() },
                                tone = when (copy.condition) {
                                    "new" -> VBadgeTone.Success
                                    "good" -> VBadgeTone.Accent
                                    "fair" -> VBadgeTone.Warning
                                    "poor", "damaged" -> VBadgeTone.Danger
                                    else -> VBadgeTone.Neutral
                                },
                            )
                            VBadge(
                                text = copy.status.replaceFirstChar { it.uppercase() },
                                tone = when (copy.status) {
                                    "available" -> VBadgeTone.Success
                                    "issued" -> VBadgeTone.Accent
                                    "lost" -> VBadgeTone.Danger
                                    "repair" -> VBadgeTone.Warning
                                    else -> VBadgeTone.Neutral
                                },
                            )
                        }
                    }
                }
            }
        }

        if (showAddCopy) {
            VBottomSheet(
                visible = true,
                onDismiss = { showAddCopy = false },
            ) {
                VBottomSheetHeader(title = "Add Copy")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Condition:", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("new" to "New", "good" to "Good", "fair" to "Fair", "poor" to "Poor").forEach { (key, label) ->
                            VBadge(
                                text = label,
                                tone = if (newCopyCondition == key) VBadgeTone.Accent else VBadgeTone.Neutral,
                                modifier = Modifier.clickable { newCopyCondition = key },
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VButton(
                        text = "Cancel",
                        onClick = { showAddCopy = false },
                        modifier = Modifier.weight(1f),
                        variant = VButtonVariant.Ghost,
                    )
                    VButton(
                        text = "Add",
                        onClick = {
                            viewModel.addCopy(bookIdInput, newCopyCondition)
                            showAddCopy = false
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

// ── History Tab ────────────────────────────────────────────────────────────

@Composable
private fun HistoryTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        var bookIdInput by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Book History", style = VTheme.type.h2.copy(color = VTheme.colors.ink))
        Text("View the full issue history for a specific book.", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(
                    value = bookIdInput,
                    onValueChange = { bookIdInput = it },
                    label = "Book ID",
                    modifier = Modifier.fillMaxWidth(),
                )
                VButton(
                    text = "Load History",
                    onClick = { if (bookIdInput.isNotBlank()) viewModel.loadBookHistory(bookIdInput) },
                    full = true,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Sm,
                )
            }
        }

        if (state.bookHistory.isEmpty()) {
            VEmptyState(title = "No history loaded", body = "Enter a book ID above to view its issue history.")
            return@Column
        }

        Text("${state.bookHistory.size} records", style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(state.bookHistory, key = { it.id }) { issue ->
                VCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(issue.borrowerName, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VTheme.colors.ink))
                        Text("Issued: ${issue.issueDate}", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
                        Text("Due: ${issue.dueDate}", style = VTheme.type.caption.copy(color = VTheme.colors.ink2))
                        issue.returnDate?.let { Text("Returned: $it", style = VTheme.type.caption.copy(color = VTheme.colors.ink2)) }
                        Row(
                            Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            VBadge(
                                text = issue.status.replaceFirstChar { it.uppercase() },
                                tone = when (issue.status) {
                                    "issued" -> VBadgeTone.Accent
                                    "returned" -> VBadgeTone.Success
                                    "lost" -> VBadgeTone.Danger
                                    else -> VBadgeTone.Neutral
                                },
                            )
                            if (issue.renewalCount > 0) {
                                VBadge(text = "${issue.renewalCount} renewal(s)", tone = VBadgeTone.Neutral)
                            }
                            if (issue.fineAmount > 0) {
                                VBadge(
                                    text = "₹${formatDecimal(issue.fineAmount)} ${issue.fineStatus}",
                                    tone = when (issue.fineStatus) {
                                        "pending" -> VBadgeTone.Warning
                                        "paid" -> VBadgeTone.Success
                                        "waived" -> VBadgeTone.Neutral
                                        else -> VBadgeTone.Neutral
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
