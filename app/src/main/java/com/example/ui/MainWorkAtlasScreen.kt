package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppNavTab
import com.example.data.Company
import com.example.data.CompanyDirectory
import com.example.ui.components.AiToolsScreen
import com.example.ui.components.BannerAdView
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.CompanyCard
import com.example.ui.components.CompanyDetailBottomSheet
import com.example.ui.components.DailyReminderDialog
import com.example.ui.components.DirectoryFooterDisclaimer
import com.example.ui.components.DirectorySearchBar
import com.example.ui.components.EmptyDirectoryState
import com.example.ui.components.LearnScreen
import com.example.ui.components.LessonReaderBottomSheet
import com.example.ui.components.SavedTrackerScreen
import com.example.ui.components.WorkAtlasBottomNavigation
import com.example.ui.components.WorkAtlasHeader
import com.example.ui.theme.AtlasDarkNavy
import com.example.ui.theme.EmeraldPrimary
import com.example.util.AdMobManager
import com.example.util.CustomTabHelper
import com.example.util.findActivity
import kotlinx.coroutines.launch

@Composable
fun MainWorkAtlasScreen(
    viewModel: WorkAtlasViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showReminderDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val showBackToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }

    val openOfficialWebsite: (Company) -> Unit = { company ->
        val activity = context.findActivity()
        if (activity != null) {
            AdMobManager.handleApplyClick(activity) {
                CustomTabHelper.openCustomTab(context, company.careerUrl)
            }
        } else {
            CustomTabHelper.openCustomTab(context, company.careerUrl)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasDarkNavy)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                BannerAdView()
                WorkAtlasBottomNavigation(
                    currentTab = uiState.currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        },
        floatingActionButton = {
            if (uiState.currentTab == AppNavTab.JOBS) {
                AnimatedVisibility(
                    visible = showBackToTop,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(0)
                            }
                        },
                        containerColor = EmeraldPrimary,
                        contentColor = AtlasDarkNavy,
                        shape = CircleShape,
                        modifier = Modifier.testTag("back_to_top_fab")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Scroll back to top"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState.currentTab) {
                AppNavTab.JOBS -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(bottom = 24.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("companies_list")
                    ) {
                        // WorkAtlas Header with brand logo & live metrics
item(key = "header") {
    WorkAtlasHeader(
        totalCompanyCount = uiState.totalCompanyCount,
        filteredCount = uiState.filteredCompanies.size,
        bookmarkedCount = uiState.bookmarkedIds.size,
        showOnlyBookmarked = uiState.showOnlyBookmarked,
        onToggleBookmarkFilter = { viewModel.toggleShowOnlyBookmarked() },
        isReminderEnabled = uiState.isReminderEnabled,
        onOpenDailyReminderDialog = { showReminderDialog = true }
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {
                val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        android.content.Intent.EXTRA_SUBJECT,
                        "WorkAtlas - Global AI & Remote Job Directory"
                    )
                    putExtra(
                        android.content.Intent.EXTRA_TEXT,
                        "Check out WorkAtlas! Browse 100+ verified global AI training, data annotation, and remote work platforms with a free ATS resume builder."
                    )
                }
                context.startActivity(
                    android.content.Intent.createChooser(shareIntent, "Share WorkAtlas")
                )
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
        ) {
            Icon(
              imageVector = androidx.compose.material.icons.Icons.Default.Add,

                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Share App",
                color = EmeraldPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = { showPrivacyDialog = true },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Privacy & Info",
                color = EmeraldPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


                        // Search Bar
                        item(key = "search_bar") {
                            Spacer(modifier = Modifier.height(10.dp))
                            DirectorySearchBar(
                                query = uiState.searchQuery,
                                onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                                onClear = { viewModel.clearSearch() },
                                recentSearches = uiState.recentSearches,
                                onSelectRecentSearch = { selectedQuery ->
                                    viewModel.onSearchQueryChanged(selectedQuery)
                                    viewModel.saveRecentSearch(selectedQuery)
                                },
                                onClearRecentSearches = { viewModel.clearRecentSearches() },
                                onSearchSubmit = { submittedQuery ->
                                    viewModel.saveRecentSearch(submittedQuery)
                                }
                            )
                        }

                        // Category Filter Chips
                        item(key = "category_filters") {
                            CategoryFilterRow(
                                selectedCategory = uiState.selectedCategory,
                                onCategorySelected = { viewModel.onCategorySelected(it) }
                            )
                        }

                        // Saved filter alert banner
                        if (uiState.showOnlyBookmarked) {
                            item(key = "bookmark_banner") {
                                Surface(
                                    color = EmeraldPrimary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = EmeraldPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Showing ${uiState.bookmarkedIds.size} saved companies",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.toggleShowOnlyBookmarked() },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(text = "Show All", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Empty State if no match
                        if (uiState.filteredCompanies.isEmpty()) {
                            item(key = "empty_state") {
                                EmptyDirectoryState(
                                    searchQuery = uiState.searchQuery,
                                    isBookmarkedOnly = uiState.showOnlyBookmarked,
                                    onResetFilters = {
                                        viewModel.clearSearch()
                                        viewModel.onCategorySelected(com.example.data.JobCategory.ALL)
                                        if (uiState.showOnlyBookmarked) {
                                            viewModel.toggleShowOnlyBookmarked()
                                        }
                                    }
                                )
                            }
                        } else {
                            // Company Cards
                            items(
                                items = uiState.filteredCompanies,
                                key = { it.id }
                            ) { company ->
                                val isBookmarked = uiState.bookmarkedIds.contains(company.id)
                                Box(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    CompanyCard(
                                        company = company,
                                        isBookmarked = isBookmarked,
                                        onToggleBookmark = { viewModel.toggleBookmark(company.id) },
                                        onApplyClick = { openOfficialWebsite(company) },
                                        onCardClick = { viewModel.openCompanyDetail(company) }
                                    )
                                }
                            }
                        }

                        // Footer disclaimer
                        item(key = "footer") {
                            DirectoryFooterDisclaimer()
                        }
                    }
                }

                AppNavTab.LEARN -> {
                    LearnScreen(
                        lessons = uiState.lessons,
                        completedIds = uiState.completedLessonIds,
                        selectedCategory = uiState.learnCategoryFilter,
                        onCategorySelected = { viewModel.setLearnCategoryFilter(it) },
                        onLessonClick = { viewModel.openLesson(it) },
                        onToggleCompleted = { viewModel.toggleLessonCompleted(it) }
                    )
                }

                AppNavTab.AI_TOOLS -> {
                    AiToolsScreen()
                }

                AppNavTab.SAVED -> {
                    val bookmarkedList = CompanyDirectory.companies.filter {
                        uiState.bookmarkedIds.contains(it.id)
                    }
                    SavedTrackerScreen(
                        bookmarkedCompanies = bookmarkedList,
                        statuses = uiState.applicationStatuses,
                        onStatusChange = { id, status -> viewModel.updateApplicationStatus(id, status) },
                        onRemoveBookmark = { companyId: Int -> viewModel.toggleBookmark(companyId) },
                        onApplyClick = { openOfficialWebsite(it) }
                    )
                }
            }
        }
    }

    // Modal detail bottom sheet for company
    uiState.selectedCompanyForDetail?.let { detailCompany ->
        val isBookmarked = uiState.bookmarkedIds.contains(detailCompany.id)
        CompanyDetailBottomSheet(
            company = detailCompany,
            isBookmarked = isBookmarked,
            onToggleBookmark = { viewModel.toggleBookmark(detailCompany.id) },
            onApplyClick = {
                viewModel.closeCompanyDetail()
                openOfficialWebsite(detailCompany)
            },
            onDismiss = { viewModel.closeCompanyDetail() }
        )
    }

    // Modal lesson reader sheet
    uiState.selectedLessonForRead?.let { lesson ->
        val isCompleted = uiState.completedLessonIds.contains(lesson.id)
        LessonReaderBottomSheet(
            lesson = lesson,
            isCompleted = isCompleted,
            onToggleCompleted = { viewModel.toggleLessonCompleted(lesson.id) },
            onDismiss = { viewModel.closeLesson() }
        )
    }

      // Modal Daily Job Reminder Dialog
    DailyReminderDialog(
        isOpen = showReminderDialog,
        isReminderEnabled = uiState.isReminderEnabled,
        initialHour = uiState.reminderHour,
        initialMinute = uiState.reminderMinute,
        onSave = { enabled, hour, minute ->
            viewModel.setDailyReminder(enabled, hour, minute)
        },
        onDismiss = { showReminderDialog = false }
    )

    // Privacy Policy & Affiliate Disclosure Dialog
    if (showPrivacyDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = AtlasDarkNavy,
            title = {
                Text(
                    text = "Privacy Policy & Disclosure",
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "WorkAtlas is a free global directory of remote work and AI training platforms.\n\n" +
                        "• Local Data Only: Your saved jobs, resume inputs, and reminder preferences stay strictly on your device. We do not collect or sell personal accounts.\n" +
                        "• Ads & Referrals: WorkAtlas displays Google AdMob ads to remain free. Some external company links include referral codes that help support app updates at zero cost to you.\n" +
                        "• External Sites: Always verify job terms directly on each company's official career portal.",
                    color = androidx.compose.ui.graphics.Color(0xFFCBD5E1),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showPrivacyDialog = false }
                ) {
                    Text(
                        text = "Got It",
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}

