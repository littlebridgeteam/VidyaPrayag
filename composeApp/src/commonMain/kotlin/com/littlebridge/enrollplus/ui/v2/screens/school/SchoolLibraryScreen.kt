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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
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
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.util.formatDecimal
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.core.locale.StringKeys

private enum class LibraryTab {
    Dashboard, Books, Copies, Issues, QuickIssue, BulkReturn,
    Categories, Audit, Announcements, Acquisition, Reservations,
    History, More, Settings;

    @Composable
    fun label(): String = when (this) {
        Dashboard -> appString(StringKeys.LIB_TAB_DASHBOARD)
        Books -> appString(StringKeys.LIB_TAB_BOOKS)
        Copies -> appString(StringKeys.LIB_TAB_COPIES)
        Issues -> appString(StringKeys.LIB_TAB_ISSUES)
        QuickIssue -> appString(StringKeys.LIB_TAB_QUICK_ISSUE)
        BulkReturn -> appString(StringKeys.LIB_TAB_BULK_RETURN)
        Categories -> appString(StringKeys.LIB_TAB_CATEGORIES)
        Audit -> appString(StringKeys.LIB_TAB_AUDIT)
        Announcements -> appString(StringKeys.LIB_TAB_ANNOUNCEMENTS)
        Acquisition -> appString(StringKeys.LIB_TAB_ACQUISITION)
        Reservations -> appString(StringKeys.LIB_TAB_RESERVATIONS)
        History -> appString(StringKeys.LIB_TAB_HISTORY)
        More -> appString(StringKeys.LIB_TAB_MORE)
        Settings -> appString(StringKeys.LIB_TAB_SETTINGS)
    }
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
            .statusBarsPadding()
            .background(VColors.surface),
    ) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        com.littlebridge.enrollplus.ui.v2.components.VBackHeader(title = appString(StringKeys.LIB_TITLE), onBack = onBack, pinRouteId = "overlay_library")

        if (state.isOffline) {
            Row(
                modifier = Modifier.fillMaxWidth().background(VColors.gold.copy(alpha = 0.1f)).padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text("⚠️", style = VTypography.caption)
                Text(
                    if (state.isStaleData) appString(StringKeys.LIB_OFFLINE_CACHED) else appString(StringKeys.LIB_OFFLINE_CHECK),
                    style = VTypography.caption.copy(color = VColors.gold),
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
                    text = tab.label(),
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
            VBadge(text = state.actionMessage ?: "", tone = VBadgeTone.Accent)
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
        Text(appString(StringKeys.LIB_DASHBOARD), style = VTypography.h2.copy(color = VColors.ink))

        if (needsOnboarding) {
            VCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(appString(StringKeys.LIB_WELCOME), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.violetInk))
                    Text(appString(StringKeys.LIB_WELCOME_DESC), style = VTypography.body.copy(color = VColors.ink2))
                    VButton(
                        text = appString(StringKeys.LIB_RUN_ONBOARDING),
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
            MetricCard(appString(StringKeys.LIB_TOTAL_BOOKS), d?.totalBooks?.toString() ?: "0", Modifier.weight(1f))
            MetricCard(appString(StringKeys.LIB_TOTAL_COPIES), d?.totalCopies?.toString() ?: "0", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard(appString(StringKeys.LIB_AVAILABLE), d?.availableCopies?.toString() ?: "0", Modifier.weight(1f), color = VColors.success)
            MetricCard(appString(StringKeys.LIB_ISSUED), d?.issuedCopies?.toString() ?: "0", Modifier.weight(1f), color = VColors.violet)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard(appString(StringKeys.LIB_OVERDUE), d?.overdueBooks?.toString() ?: "0", Modifier.weight(1f), color = VColors.gold)
            MetricCard(appString(StringKeys.LIB_LOST), d?.lostBooks?.toString() ?: "0", Modifier.weight(1f), color = VColors.error)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard(appString(StringKeys.LIB_RESERVATIONS), d?.activeReservations?.toString() ?: "0", Modifier.weight(1f))
            MetricCard(appString(StringKeys.LIB_DAMAGED), d?.damagedBooks?.toString() ?: "0", Modifier.weight(1f), color = VColors.gold)
        }

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(appString(StringKeys.LIB_OUTSTANDING_FINES), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                Text("${d?.outstandingFinesCount ?: 0} ${appString(StringKeys.LIB_PENDING_LABEL)}", style = VTypography.caption.copy(color = VColors.ink2))
                Text(
                    "₹${formatDecimal(d?.outstandingFinesAmount ?: 0.0)}",
                    style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp).copy(color = VColors.error).copy(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
                )
                Spacer(Modifier.height(4.dp))
                Text(appString(StringKeys.LIB_COLLECTED_MONTH), style = VTypography.caption.copy(color = VColors.ink2))
                Text(
                    "₹${formatDecimal(d?.finesCollectedThisMonth ?: 0.0)}",
                    style = VTypography.caption.copy(color = VColors.success).copy(fontWeight = FontWeight.SemiBold),
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
        Text(appString(StringKeys.LIB_BOOKS), style = VTypography.h2.copy(color = VColors.ink))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VInput(
                value = state.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = appString(StringKeys.LIB_SEARCH_BOOKS),
                modifier = Modifier.weight(1f),
            )
            VButton(
                text = appString(StringKeys.LIB_ADD_BOOK),
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
            VLabel(appString(StringKeys.LIB_CATEGORY_LABEL))
            VBadge(
                text = state.searchCategory ?: appString(StringKeys.COMMON_ALL),
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
            VLabel(appString(StringKeys.LIB_AVAILABLE_LABEL))
            listOf("all" to appString(StringKeys.COMMON_ALL), "available" to appString(StringKeys.LIB_AVAILABLE_ONLY)).forEach { (key, label) ->
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
            VLabel(appString(StringKeys.LIB_SORT_LABEL))
            listOf("newest" to appString(StringKeys.LIB_SORT_NEWEST), "title" to appString(StringKeys.LIB_SORT_TITLE), "author" to appString(StringKeys.LIB_SORT_AUTHOR), "popular" to appString(StringKeys.LIB_SORT_POPULAR)).forEach { (key, label) ->
                VBadge(
                    text = label,
                    tone = if (state.searchSortBy == key) VBadgeTone.Accent else VBadgeTone.Neutral,
                    modifier = Modifier.clickable { viewModel.updateSearchSortBy(key) },
                )
            }
        }

        VButton(
            text = appString(StringKeys.LIB_SEARCH_BTN),
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
            VEmptyState(title = appString(StringKeys.LIB_NO_BOOKS), body = appString(StringKeys.LIB_NO_BOOKS_DESC))
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
                                    Text(book.title, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                                    if (book.isArchived) VBadge(text = appString(StringKeys.LIB_ARCHIVED), tone = VBadgeTone.Neutral)
                                }
                                book.author?.let { Text(it, style = VTypography.caption.copy(color = VColors.ink2)) }
                                book.isbn?.let { Text(appString(StringKeys.LIB_ISBN_PREFIX, "value" to it), style = VTypography.caption.copy(color = VColors.ink3)) }
                                if (book.seriesName != null) {
                                    Text("${book.seriesName} #${book.seriesNumber ?: 1}", style = VTypography.caption.copy(color = VColors.ink3))
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
                                text = if (book.isArchived) appString(StringKeys.LIB_UNARCHIVE) else appString(StringKeys.LIB_ARCHIVE),
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
                                text = appString(StringKeys.LIB_SET_COVER),
                                onClick = { showCoverUpload = book.id; coverUrl = book.coverUrl ?: "" },
                                variant = VButtonVariant.Secondary,
                                tone = VButtonTone.Sand,
                                size = VButtonSize.Sm,
                            )
                            VButton(
                                text = appString(StringKeys.LIB_ISSUE),
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
                        text = appString(StringKeys.COMMON_BUTTON_REFRESH) + " (${state.booksTotal - state.books.size})",
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
        val issueBookId = showIssueBook ?: return
        IssueBookSheet(
            bookId = issueBookId,
            onDismiss = { showIssueBook = null },
            onIssue = { req ->
                viewModel.issueBook(req)
                showIssueBook = null
            },
        )
    }

    // Cover Upload Dialog
    showCoverUpload?.let { coverBookId ->
        VBottomSheet(
            visible = true,
            onDismiss = { showCoverUpload = null },
        ) {
            VBottomSheetHeader(title = appString(StringKeys.LIB_SET_COVER_URL))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(value = coverUrl, onValueChange = { coverUrl = it }, label = appString(StringKeys.LIB_COVER_URL), modifier = Modifier.fillMaxWidth())
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = { showCoverUpload = null },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Ghost,
                )
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_SAVE),
                    onClick = {
                        if (coverUrl.isNotBlank()) {
                            viewModel.uploadCover(coverBookId, coverUrl)
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
        VBottomSheetHeader(title = appString(StringKeys.LIB_ADD_NEW_BOOK))
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            VInput(value = title, onValueChange = { title = it }, label = appString(StringKeys.LIB_TITLE_LABEL), modifier = Modifier.fillMaxWidth())
            VInput(value = author, onValueChange = { author = it }, label = appString(StringKeys.LIB_AUTHOR_LABEL), modifier = Modifier.fillMaxWidth())
            VInput(value = isbn, onValueChange = { isbn = it }, label = appString(StringKeys.LIB_ISBN_LABEL), modifier = Modifier.fillMaxWidth())
            VInput(value = publisher, onValueChange = { publisher = it }, label = appString(StringKeys.LIB_PUBLISHER_LABEL), modifier = Modifier.fillMaxWidth())
            VInput(value = totalCopies, onValueChange = { totalCopies = it }, label = appString(StringKeys.LIB_TOTAL_COPIES_LABEL), modifier = Modifier.fillMaxWidth())
            VInput(value = shelfLocation, onValueChange = { shelfLocation = it }, label = appString(StringKeys.LIB_SHELF_LOCATION), modifier = Modifier.fillMaxWidth())
            VInput(value = replacementCost, onValueChange = { replacementCost = it }, label = appString(StringKeys.LIB_REPLACEMENT_COST), modifier = Modifier.fillMaxWidth())
            VInput(value = language, onValueChange = { language = it }, label = appString(StringKeys.LIB_LANGUAGE), modifier = Modifier.fillMaxWidth())
            VInput(value = synopsis, onValueChange = { synopsis = it }, label = appString(StringKeys.LIB_SYNOPSIS), modifier = Modifier.fillMaxWidth())
            Text(appString(StringKeys.LIB_CATEGORY), style = VTypography.caption.copy(color = VColors.ink2))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                VBadge(
                    text = appString(StringKeys.COMMON_NONE),
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
                text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                variant = VButtonVariant.Ghost,
            )
            VButton(
                text = appString(StringKeys.LIB_CREATE),
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
        VBottomSheetHeader(title = appString(StringKeys.LIB_ISSUE_BOOK))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            VInput(value = borrowerId, onValueChange = { borrowerId = it }, label = appString(StringKeys.LIB_BORROWER_ID), modifier = Modifier.fillMaxWidth())
            VInput(value = borrowerName, onValueChange = { borrowerName = it }, label = appString(StringKeys.LIB_BORROWER_NAME), modifier = Modifier.fillMaxWidth())
            VInput(value = copyId, onValueChange = { copyId = it }, label = appString(StringKeys.LIB_COPY_ID), modifier = Modifier.fillMaxWidth())
            Text(appString(StringKeys.LIB_BORROWER_TYPE), style = VTypography.caption.copy(color = VColors.ink2))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("student" to appString(StringKeys.LIB_STUDENT), "teacher" to appString(StringKeys.LIB_TEACHER)).forEach { (key, label) ->
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
                text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                variant = VButtonVariant.Ghost,
            )
            VButton(
                text = appString(StringKeys.LIB_ISSUE),
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
        Text(appString(StringKeys.LIB_ISSUES), style = VTypography.h2.copy(color = VColors.ink))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(null, "issued", "returned", "lost").forEach { status ->
                VBadge(
                    text = status ?: appString(StringKeys.COMMON_ALL),
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
            VEmptyState(title = appString(StringKeys.LIB_NO_ISSUES), body = appString(StringKeys.LIB_NO_ISSUES_DESC))
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
                        text = appString(StringKeys.COMMON_BUTTON_REFRESH) + " (${state.issuesTotal - state.issues.size})",
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
            Text(issue.bookTitle, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
            Text(issue.borrowerName, style = VTypography.caption.copy(color = VColors.ink2))
            if (issue.status == "issued") {
                com.littlebridge.enrollplus.ui.v2.screens.library.DueDateBadge(dueDate = issue.dueDate)
            } else {
                Text(appString(StringKeys.LIB_DUE_PREFIX, "date" to issue.dueDate), style = VTypography.caption.copy(color = VColors.ink3))
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
                        text = appString(StringKeys.LIB_RETURN),
                        onClick = { showReturnDialog = true },
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Mint,
                        size = VButtonSize.Sm,
                        loading = isActionLoading,
                    )
                    VButton(
                        text = appString(StringKeys.LIB_RENEW),
                        onClick = { viewModel.renewBook(issue.id) },
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Lavender,
                        size = VButtonSize.Sm,
                        loading = isActionLoading,
                    )
                    VButton(
                        text = appString(StringKeys.LIB_MARK_LOST),
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
                        text = appString(StringKeys.LIB_PAY_FINE),
                        onClick = { viewModel.payFine(issue.id) },
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Sand,
                        size = VButtonSize.Sm,
                        loading = isActionLoading,
                    )
                    VButton(
                        text = appString(StringKeys.LIB_WAIVE_FINE),
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
            VBottomSheetHeader(title = appString(StringKeys.LIB_RETURN_BOOK))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(issue.bookTitle, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                Text(appString(StringKeys.LIB_SELECT_CONDITION), style = VTypography.caption.copy(color = VColors.ink2))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("good" to appString(StringKeys.LIB_CONDITION_GOOD), "fair" to appString(StringKeys.LIB_CONDITION_FAIR), "damaged" to appString(StringKeys.LIB_CONDITION_DAMAGED)).forEach { (key, label) ->
                        VBadge(
                            text = label,
                            tone = if (condition == key) VBadgeTone.Accent else VBadgeTone.Neutral,
                            modifier = Modifier.clickable { condition = key },
                        )
                    }
                }
                if (condition == "damaged") {
                    VInput(value = damageNotes, onValueChange = { damageNotes = it }, label = appString(StringKeys.LIB_DAMAGE_NOTES), modifier = Modifier.fillMaxWidth())
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = { showReturnDialog = false },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Ghost,
                )
                VButton(
                    text = appString(StringKeys.LIB_CONFIRM_RETURN),
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
            title = appString(StringKeys.LIB_MARK_LOST_TITLE),
            message = appString(StringKeys.LIB_MARK_LOST_MSG, "title" to issue.bookTitle),
            confirmLabel = appString(StringKeys.LIB_MARK_LOST),
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
            VBottomSheetHeader(title = appString(StringKeys.LIB_WAIVE_FINE_TITLE))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(appString(StringKeys.LIB_FINE_PREFIX, "amount" to formatDecimal(issue.fineAmount), "title" to issue.bookTitle), style = VTypography.body.copy(color = VColors.ink2))
                VInput(value = waiveReason, onValueChange = { waiveReason = it }, label = appString(StringKeys.LIB_WAIVER_REASON), modifier = Modifier.fillMaxWidth())
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = { showWaiveDialog = false },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Ghost,
                )
                VButton(
                    text = appString(StringKeys.LIB_WAIVE_FINE),
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
        Text(appString(StringKeys.LIB_SETTINGS), style = VTypography.h2.copy(color = VColors.ink))

        if (s == null) {
            Text(appString(StringKeys.LIB_LOADING_SETTINGS), style = VTypography.body.copy(color = VColors.ink2))
            return@Column
        }

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                VInput(value = defaultLoanDays, onValueChange = { defaultLoanDays = it }, label = appString(StringKeys.LIB_DEFAULT_LOAN_DAYS), modifier = Modifier.fillMaxWidth())
                VInput(value = finePerDay, onValueChange = { finePerDay = it }, label = appString(StringKeys.LIB_FINE_PER_DAY), modifier = Modifier.fillMaxWidth())
                VInput(value = maxBooksPerStudent, onValueChange = { maxBooksPerStudent = it }, label = appString(StringKeys.LIB_MAX_BOOKS), modifier = Modifier.fillMaxWidth())
                VInput(value = maxRenewals, onValueChange = { maxRenewals = it }, label = appString(StringKeys.LIB_MAX_RENEWALS), modifier = Modifier.fillMaxWidth())
                VInput(value = reservationTimeoutDays, onValueChange = { reservationTimeoutDays = it }, label = appString(StringKeys.LIB_RESERVATION_TIMEOUT), modifier = Modifier.fillMaxWidth())
                VInput(value = dueReminderDays, onValueChange = { dueReminderDays = it }, label = appString(StringKeys.LIB_DUE_REMINDER), modifier = Modifier.fillMaxWidth())

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    VLabel(appString(StringKeys.LIB_FINE_CAP))
                    VBadge(
                        text = if (fineCapEnabled) appString(StringKeys.COMMON_YES) else appString(StringKeys.COMMON_NO),
                        tone = if (fineCapEnabled) VBadgeTone.Accent else VBadgeTone.Neutral,
                        modifier = Modifier.clickable { fineCapEnabled = !fineCapEnabled },
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    VLabel(appString(StringKeys.LIB_QUICK_ISSUE_ENABLED))
                    VBadge(
                        text = if (quickIssueEnabled) appString(StringKeys.COMMON_YES) else appString(StringKeys.COMMON_NO),
                        tone = if (quickIssueEnabled) VBadgeTone.Accent else VBadgeTone.Neutral,
                        modifier = Modifier.clickable { quickIssueEnabled = !quickIssueEnabled },
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    VLabel(appString(StringKeys.LIB_BULK_RETURN_ENABLED))
                    VBadge(
                        text = if (bulkReturnEnabled) appString(StringKeys.COMMON_YES) else appString(StringKeys.COMMON_NO),
                        tone = if (bulkReturnEnabled) VBadgeTone.Accent else VBadgeTone.Neutral,
                        modifier = Modifier.clickable { bulkReturnEnabled = !bulkReturnEnabled },
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    VLabel(appString(StringKeys.LIB_LEADERBOARD_ENABLED))
                    VBadge(
                        text = if (leaderboardEnabled) appString(StringKeys.COMMON_YES) else appString(StringKeys.COMMON_NO),
                        tone = if (leaderboardEnabled) VBadgeTone.Accent else VBadgeTone.Neutral,
                        modifier = Modifier.clickable { leaderboardEnabled = !leaderboardEnabled },
                    )
                }
            }
        }

        VButton(
            text = appString(StringKeys.LIB_SAVE_SETTINGS),
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
            text = appString(StringKeys.LIB_RESET_DEFAULTS),
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
                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp).copy(color = color ?: VColors.ink).copy(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun SettingRow(label: String, value: String) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        VLabel(label)
        Text(value, style = VTypography.body.copy(color = VColors.ink).copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun ReservationsTab(state: SchoolLibraryState, viewModel: SchoolLibraryViewModel) {
        var bookId by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(appString(StringKeys.LIB_TAB_RESERVATIONS), style = VTypography.h2.copy(color = VColors.ink))

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(value = bookId, onValueChange = { bookId = it }, label = appString(StringKeys.LIB_BOOK_ID), modifier = Modifier.fillMaxWidth())
                VButton(
                    text = appString(StringKeys.LIB_LOAD_RESERVATIONS),
                    onClick = { if (bookId.isNotBlank()) viewModel.loadReservationsForBook(bookId) },
                    full = true,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Sm,
                )
            }
        }

        if (state.reservations.isEmpty()) {
            VEmptyState(title = appString(StringKeys.LIB_NO_RESERVATIONS), body = appString(StringKeys.LIB_NO_RESERVATIONS_DESC))
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(state.reservations, key = { it.id }) { res ->
                VCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(res.bookTitle, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                        Text(res.reservedByName, style = VTypography.caption.copy(color = VColors.ink2))
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
                        res.waitlistPosition?.let { Text(appString(StringKeys.LIB_WAITLIST_PREFIX, "position" to it), style = VTypography.caption.copy(color = VColors.ink3)) }
                        Text(appString(StringKeys.LIB_RESERVED_PREFIX, "date" to res.createdAt), style = VTypography.caption.copy(color = VColors.ink3))

                        if (res.status == "pending" || res.status == "notified") {
                            VButton(
                                text = appString(StringKeys.LIB_FULFILL),
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
        Text(appString(StringKeys.LIB_QUICK_ISSUE_TAB), style = VTypography.h2.copy(color = VColors.ink))
        Text(appString(StringKeys.LIB_QUICK_ISSUE_DESC), style = VTypography.caption.copy(color = VColors.ink2))

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(value = barcode, onValueChange = { barcode = it }, label = appString(StringKeys.LIB_BARCODE), modifier = Modifier.fillMaxWidth())
                VInput(value = borrowerId, onValueChange = { borrowerId = it }, label = appString(StringKeys.LIB_BORROWER_ID_LABEL), modifier = Modifier.fillMaxWidth())
                VInput(value = borrowerName, onValueChange = { borrowerName = it }, label = appString(StringKeys.LIB_BORROWER_NAME_LABEL), modifier = Modifier.fillMaxWidth())
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
                    text = appString(StringKeys.LIB_ISSUE),
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
        Text(appString(StringKeys.LIB_BULK_RETURN_TAB), style = VTypography.h2.copy(color = VColors.ink))
        Text(appString(StringKeys.LIB_BULK_RETURN_DESC), style = VTypography.caption.copy(color = VColors.ink2))

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(value = currentBarcode, onValueChange = { currentBarcode = it }, label = appString(StringKeys.LIB_SCAN_BARCODE), modifier = Modifier.fillMaxWidth())
                VButton(
                    text = appString(StringKeys.LIB_ADD),
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
            Text(appString(StringKeys.LIB_BARCODES_SCANNED, "count" to barcodes.size), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                items(barcodes.indices.toList()) { idx ->
                    VCard {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("${idx + 1}. ${barcodes[idx]}", style = VTypography.body.copy(color = VColors.ink))
                            VBadge(text = appString(StringKeys.GAM_REMOVE), tone = VBadgeTone.Danger, modifier = Modifier.clickable { barcodes.removeAt(idx) })
                        }
                    }
                }
            }
            VButton(
                text = appString(StringKeys.LIB_END_SESSION),
                onClick = { showConfirm = true },
                full = true,
                tone = VButtonTone.Lavender,
                size = VButtonSize.Md,
                loading = state.isActionLoading,
            )
        } else {
            VEmptyState(title = appString(StringKeys.LIB_NO_BARCODES), body = appString(StringKeys.LIB_NO_BARCODES_DESC))
        }

        if (showConfirm) {
            VConfirmDialog(
                visible = true,
                title = appString(StringKeys.LIB_CONFIRM_BULK_RETURN),
                message = appString(StringKeys.LIB_BULK_RETURN_MSG, "count" to barcodes.size),
                confirmLabel = appString(StringKeys.LIB_RETURN_ALL),
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
            Text(appString(StringKeys.LIB_CATEGORIES_TAB), style = VTypography.h2.copy(color = VColors.ink))
            VButton(text = appString(StringKeys.LIB_ADD_CATEGORY), onClick = { showCreate = true }, variant = VButtonVariant.Secondary, tone = VButtonTone.Lavender, size = VButtonSize.Sm)
        }

        if (state.categories.isEmpty()) {
            if (state.error != null) {
                VErrorState(message = state.error ?: "", onRetry = { viewModel.loadCategories() })
                return@Column
            }
            VEmptyState(title = appString(StringKeys.LIB_NO_CATEGORIES), body = appString(StringKeys.LIB_NO_CATEGORIES_DESC))
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
                            Text(cat.icon, style = VTypography.caption.copy(color = VColors.ink3))
                            Text(cat.name, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
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
                            VBadge(text = appString(StringKeys.COMMON_BUTTON_DELETE), tone = VBadgeTone.Danger, modifier = Modifier.clickable { showDeleteConfirm = cat.id })
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
                VBottomSheetHeader(title = appString(StringKeys.LIB_NEW_CATEGORY))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    VInput(value = newName, onValueChange = { newName = it }, label = appString(StringKeys.LIB_NAME), modifier = Modifier.fillMaxWidth())
                    VInput(value = newColor, onValueChange = { newColor = it }, label = appString(StringKeys.LIB_COLOR), modifier = Modifier.fillMaxWidth())
                    VInput(value = newIcon, onValueChange = { newIcon = it }, label = appString(StringKeys.LIB_ICON_NAME), modifier = Modifier.fillMaxWidth())
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VButton(
                        text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                        onClick = { showCreate = false },
                        modifier = Modifier.weight(1f),
                        variant = VButtonVariant.Ghost,
                    )
                    VButton(
                        text = appString(StringKeys.LIB_CREATE),
                        onClick = {
                            viewModel.createCategory(CreateCategoryRequest(newName, newColor, newIcon))
                            newName = ""; showCreate = false
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        showDeleteConfirm?.let { deleteCatId ->
            val catName = state.categories.find { it.id == deleteCatId }?.name ?: ""
            VConfirmDialog(
                visible = true,
                title = appString(StringKeys.LIB_DELETE_CATEGORY_TITLE),
                message = appString(StringKeys.LIB_DELETE_CATEGORY_MSG, "name" to catName),
                confirmLabel = appString(StringKeys.COMMON_BUTTON_DELETE),
                onConfirm = {
                    viewModel.deleteCategory(deleteCatId)
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
        Text(appString(StringKeys.LIB_AUDIT_TRAIL), style = VTypography.h2.copy(color = VColors.ink))

        if (state.error != null && state.auditLog.isEmpty()) {
            VErrorState(message = state.error ?: "", onRetry = { viewModel.loadAuditLog(1) })
            return@Column
        }

        if (state.isLoading && state.auditLog.isEmpty()) {
            repeat(3) { BookCardSkeleton() }
            return@Column
        }

        if (state.auditLog.isEmpty()) {
            VEmptyState(title = appString(StringKeys.LIB_NO_AUDIT), body = appString(StringKeys.LIB_NO_AUDIT_DESC))
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(state.auditLog, key = { it.id }) { log ->
                VCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(log.action, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                            VBadge(text = log.entityType, tone = VBadgeTone.Neutral)
                        }
                        Text(appString(StringKeys.LIB_BY_PREFIX, "name" to log.actorName), style = VTypography.caption.copy(color = VColors.ink2))
                        Text(log.createdAt, style = VTypography.caption.copy(color = VColors.ink3))
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
            Text(appString(StringKeys.LIB_ANNOUNCEMENTS_TAB), style = VTypography.h2.copy(color = VColors.ink))
            VButton(text = appString(StringKeys.LIB_NEW_ANNOUNCEMENT), onClick = { showCreate = true }, variant = VButtonVariant.Secondary, tone = VButtonTone.Lavender, size = VButtonSize.Sm)
        }

        if (state.announcements.isEmpty()) {
            if (state.error != null) {
                VErrorState(message = state.error ?: "", onRetry = { viewModel.loadAnnouncements(false) })
                return@Column
            }
            VEmptyState(title = appString(StringKeys.LIB_NO_ANNOUNCEMENTS), body = appString(StringKeys.LIB_NO_ANNOUNCEMENTS_DESC))
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(state.announcements, key = { it.id }) { ann ->
                VCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(ann.title, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                            if (!ann.isActive) VBadge(text = appString(StringKeys.LIB_INACTIVE), tone = VBadgeTone.Neutral)
                        }
                        Text(ann.message, style = VTypography.body.copy(color = VColors.ink2))
                        Text(appString(StringKeys.LIB_EXPIRES_PREFIX, "date" to (ann.expiresAt ?: appString(StringKeys.LIB_NEVER))), style = VTypography.caption.copy(color = VColors.ink3))
                        Row(
                            Modifier.fillMaxWidth().padding(top = 4.dp).horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            VButton(
                                text = if (ann.isActive) appString(StringKeys.LIB_DEACTIVATE) else appString(StringKeys.LIB_ACTIVATE),
                                onClick = { viewModel.toggleAnnouncement(ann.id, ann.isActive) },
                                variant = VButtonVariant.Secondary,
                                tone = if (ann.isActive) VButtonTone.Sand else VButtonTone.Mint,
                                size = VButtonSize.Sm,
                                loading = state.isActionLoading,
                            )
                            VButton(
                                text = appString(StringKeys.COMMON_BUTTON_DELETE),
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
                VBottomSheetHeader(title = appString(StringKeys.LIB_NEW_ANNOUNCEMENT_TITLE))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    VInput(value = title, onValueChange = { title = it }, label = appString(StringKeys.LIB_ANN_TITLE), modifier = Modifier.fillMaxWidth())
                    VInput(value = body, onValueChange = { body = it }, label = appString(StringKeys.LIB_ANN_BODY), modifier = Modifier.fillMaxWidth())
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VButton(
                        text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                        onClick = { showCreate = false },
                        modifier = Modifier.weight(1f),
                        variant = VButtonVariant.Ghost,
                    )
                    VButton(
                        text = appString(StringKeys.LIB_POST),
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

        showDeleteAnnouncement?.let { deleteAnnId ->
            val annTitle = state.announcements.find { it.id == deleteAnnId }?.title ?: ""
            VConfirmDialog(
                visible = true,
                title = appString(StringKeys.LIB_DELETE_ANN_TITLE),
                message = appString(StringKeys.LIB_DELETE_ANN_MSG, "title" to annTitle),
                confirmLabel = appString(StringKeys.COMMON_BUTTON_DELETE),
                onConfirm = {
                    viewModel.deleteAnnouncement(deleteAnnId)
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
        Text(appString(StringKeys.LIB_ACQUISITION_REQUESTS), style = VTypography.h2.copy(color = VColors.ink))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(null, "pending", "approved", "ordered", "received").forEach { status ->
                val isSelected: Boolean = statusFilter == status
                VBadge(
                    text = status ?: appString(StringKeys.COMMON_ALL),
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
            VEmptyState(title = appString(StringKeys.LIB_NO_REQUESTS), body = appString(StringKeys.LIB_NO_REQUESTS_DESC))
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(state.acquisitionRequests, key = { it.id }) { req ->
                VCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(req.title, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                        req.author?.let { Text(appString(StringKeys.LIB_AUTHOR_PREFIX, "name" to it), style = VTypography.caption.copy(color = VColors.ink2)) }
                        req.isbn?.let { Text(appString(StringKeys.LIB_ISBN_PREFIX, "value" to it), style = VTypography.caption.copy(color = VColors.ink2)) }
                        req.publisher?.let { Text(appString(StringKeys.LIB_PUBLISHER_PREFIX, "name" to it), style = VTypography.caption.copy(color = VColors.ink2)) }
                        req.reason?.let { Text(appString(StringKeys.LIB_REASON_PREFIX, "reason" to it), style = VTypography.caption.copy(color = VColors.ink2)) }
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
                                    VButton(text = appString(StringKeys.LIB_APPROVE), onClick = { viewModel.updateAcquisitionStatus(req.id, "approve") }, variant = VButtonVariant.Secondary, tone = VButtonTone.Mint, size = VButtonSize.Sm, loading = state.isActionLoading)
                                }
                                if (req.status == "approved") {
                                    VButton(text = appString(StringKeys.LIB_ORDER), onClick = { viewModel.updateAcquisitionStatus(req.id, "order") }, variant = VButtonVariant.Secondary, tone = VButtonTone.Lavender, size = VButtonSize.Sm, loading = state.isActionLoading)
                                }
                                if (req.status == "ordered") {
                                    VButton(text = appString(StringKeys.LIB_RECEIVE), onClick = { viewModel.updateAcquisitionStatus(req.id, "receive") }, variant = VButtonVariant.Secondary, tone = VButtonTone.Mint, size = VButtonSize.Sm, loading = state.isActionLoading)
                                }
                                if (req.status == "received") {
                                    VButton(text = appString(StringKeys.LIB_CONVERT_TO_BOOK), onClick = { viewModel.convertAcquisitionToBook(req.id) }, variant = VButtonVariant.Secondary, tone = VButtonTone.Lavender, size = VButtonSize.Sm, loading = state.isActionLoading)
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
        Text(appString(StringKeys.LIB_MORE_TAB), style = VTypography.h2.copy(color = VColors.ink))

        Text(appString(StringKeys.LIB_QUICK_ACTIONS), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(text = appString(StringKeys.LIB_RUN_ONBOARDING), onClick = { viewModel.runOnboarding() }, full = true, tone = VButtonTone.Lavender, size = VButtonSize.Sm, loading = state.isActionLoading)
                VButton(text = appString(StringKeys.LIB_EXPORT_CATALOG), onClick = { viewModel.exportCatalog() }, full = true, tone = VButtonTone.Mint, size = VButtonSize.Sm, loading = state.isActionLoading)
                VButton(text = appString(StringKeys.LIB_IMPORT_BOOKS), onClick = { showImport = true }, full = true, tone = VButtonTone.Sky, size = VButtonSize.Sm)
            }
        }

        if (state.trending.isNotEmpty()) {
            Text(appString(StringKeys.LIB_TRENDING_BOOKS), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
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
                            Text(book.title, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink), maxLines = 1)
                            book.author?.let { Text(it, style = VTypography.caption.copy(color = VColors.ink2), maxLines = 1) }
                            VBadge(text = "${book.issueCount} ${appString(StringKeys.LIB_ISSUES)}", tone = VBadgeTone.Accent)
                        }
                    }
                }
            }
        }

        Text(appString(StringKeys.LIB_REPAIR_QUEUE), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
        if (state.repairCopies.isEmpty()) {
            VEmptyState(title = appString(StringKeys.LIB_NO_REPAIR), body = appString(StringKeys.LIB_NO_REPAIR_DESC))
        } else {
            state.repairCopies.forEach { copy ->
                VCard {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(copy.bookTitle, style = VTypography.body.copy(color = VColors.ink))
                            Text(appString(StringKeys.LIB_COPY_PREFIX, "id" to copy.copyId), style = VTypography.caption.copy(color = VColors.ink2))
                        }
                        VButton(
                            text = appString(StringKeys.LIB_MARK_REPAIRED),
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
                VBottomSheetHeader(title = appString(StringKeys.LIB_IMPORT_BOOKS_TITLE))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(appString(StringKeys.LIB_PASTE_JSON), style = VTypography.caption.copy(color = VColors.ink2))
                    VInput(value = importJson, onValueChange = { importJson = it }, label = appString(StringKeys.LIB_JSON_LABEL), modifier = Modifier.fillMaxWidth())
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VButton(
                        text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                        onClick = { showImport = false },
                        modifier = Modifier.weight(1f),
                        variant = VButtonVariant.Ghost,
                    )
                    VButton(
                        text = appString(StringKeys.LIB_IMPORT),
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
        Text(appString(StringKeys.LIB_BOOK_COPIES), style = VTypography.h2.copy(color = VColors.ink))
        Text(appString(StringKeys.LIB_COPIES_DESC), style = VTypography.caption.copy(color = VColors.ink2))

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(
                    value = bookIdInput,
                    onValueChange = { bookIdInput = it },
                    label = appString(StringKeys.LIB_BOOK_ID),
                    modifier = Modifier.fillMaxWidth(),
                )
                VButton(
                    text = appString(StringKeys.LIB_LOAD_COPIES),
                    onClick = { if (bookIdInput.isNotBlank()) viewModel.loadCopies(bookIdInput) },
                    full = true,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Sm,
                )
            }
        }

        if (state.copies.isEmpty()) {
            VEmptyState(title = appString(StringKeys.LIB_NO_COPIES), body = appString(StringKeys.LIB_NO_COPIES_DESC))
            return@Column
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(appString(StringKeys.LIB_COPIES_COUNT, "count" to state.copies.size), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
            VButton(
                text = appString(StringKeys.LIB_ADD_COPY),
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
                            Text(appString(StringKeys.LIB_COPY_PREFIX, "id" to copy.copyNumber), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                            copy.barcode?.let { Text(appString(StringKeys.LIB_BARCODE_PREFIX, "code" to it), style = VTypography.caption.copy(color = VColors.ink2)) }
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
                VBottomSheetHeader(title = appString(StringKeys.LIB_ADD_COPY_TITLE))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(appString(StringKeys.LIB_CONDITION_LABEL), style = VTypography.caption.copy(color = VColors.ink2))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("new" to appString(StringKeys.LIB_CONDITION_NEW), "good" to appString(StringKeys.LIB_CONDITION_GOOD), "fair" to appString(StringKeys.LIB_CONDITION_FAIR), "poor" to appString(StringKeys.LIB_CONDITION_POOR)).forEach { (key, label) ->
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
                        text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                        onClick = { showAddCopy = false },
                        modifier = Modifier.weight(1f),
                        variant = VButtonVariant.Ghost,
                    )
                    VButton(
                        text = appString(StringKeys.LIB_ADD),
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
        Text(appString(StringKeys.LIB_BOOK_HISTORY), style = VTypography.h2.copy(color = VColors.ink))
        Text(appString(StringKeys.LIB_HISTORY_DESC), style = VTypography.caption.copy(color = VColors.ink2))

        VCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VInput(
                    value = bookIdInput,
                    onValueChange = { bookIdInput = it },
                    label = appString(StringKeys.LIB_BOOK_ID),
                    modifier = Modifier.fillMaxWidth(),
                )
                VButton(
                    text = appString(StringKeys.LIB_LOAD_HISTORY),
                    onClick = { if (bookIdInput.isNotBlank()) viewModel.loadBookHistory(bookIdInput) },
                    full = true,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Sm,
                )
            }
        }

        if (state.bookHistory.isEmpty()) {
            VEmptyState(title = appString(StringKeys.LIB_NO_HISTORY), body = appString(StringKeys.LIB_NO_HISTORY_DESC))
            return@Column
        }

        Text(appString(StringKeys.LIB_RECORDS_COUNT, "count" to state.bookHistory.size), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(state.bookHistory, key = { it.id }) { issue ->
                VCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(issue.borrowerName, style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
                        Text(appString(StringKeys.LIB_ISSUED_PREFIX, "date" to issue.issueDate), style = VTypography.caption.copy(color = VColors.ink2))
                        Text(appString(StringKeys.LIB_DUE_PREFIX, "date" to issue.dueDate), style = VTypography.caption.copy(color = VColors.ink2))
                        issue.returnDate?.let { Text(appString(StringKeys.LIB_RETURNED_PREFIX, "date" to it), style = VTypography.caption.copy(color = VColors.ink2)) }
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
