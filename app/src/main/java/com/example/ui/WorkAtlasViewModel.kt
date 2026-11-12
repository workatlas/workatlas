package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppNavTab
import com.example.data.ApplicationStatus
import com.example.data.Company
import com.example.data.CompanyDirectory
import com.example.data.JobCategory
import com.example.data.LearnLesson
import com.example.data.LearnLessonsData
import com.example.data.local.WorkAtlasDatabase
import com.example.data.local.WorkAtlasLocalRepository
import com.example.reminder.DailyReminderScheduler
import com.example.reminder.ReminderPreferences
import com.example.reminder.ReminderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WorkAtlasUiState(
    val currentTab: AppNavTab = AppNavTab.JOBS,
    // Jobs Directory
    val totalCompanyCount: Int = 102,
    val searchQuery: String = "",
    val recentSearches: List<String> = emptyList(),
    val selectedCategory: JobCategory = JobCategory.ALL,
    val bookmarkedIds: Set<Int> = emptySet(),
    val applicationStatuses: Map<Int, ApplicationStatus> = emptyMap(),
    val showOnlyBookmarked: Boolean = false,
    val filteredCompanies: List<Company> = emptyList(),
    val selectedCompanyForDetail: Company? = null,
    // Learn Hub
    val lessons: List<LearnLesson> = LearnLessonsData.lessons,
    val completedLessonIds: Set<String> = emptySet(),
    val selectedLessonForRead: LearnLesson? = null,
    val learnCategoryFilter: com.example.data.LearnCategory? = null,
    val isDatabaseSynced: Boolean = true,
    // Daily Reminder Notifications
    val isReminderEnabled: Boolean = false,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0
)

class WorkAtlasViewModel(application: Application) : AndroidViewModel(application) {

    private val localRepository = WorkAtlasLocalRepository(
        WorkAtlasDatabase.getDatabase(application)
    )

    private val reminderPreferences = ReminderPreferences(application)

    private val _currentTab = MutableStateFlow(AppNavTab.JOBS)
    val currentTab: StateFlow<AppNavTab> = _currentTab

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow(JobCategory.ALL)
    val selectedCategory: StateFlow<JobCategory> = _selectedCategory

    private val _showOnlyBookmarked = MutableStateFlow(false)
    val showOnlyBookmarked: StateFlow<Boolean> = _showOnlyBookmarked

    private val _selectedCompanyForDetail = MutableStateFlow<Company?>(null)
    val selectedCompanyForDetail: StateFlow<Company?> = _selectedCompanyForDetail

    private val _selectedLessonForRead = MutableStateFlow<LearnLesson?>(null)
    val selectedLessonForRead: StateFlow<LearnLesson?> = _selectedLessonForRead

    private val _learnCategoryFilter = MutableStateFlow<com.example.data.LearnCategory?>(null)
    val learnCategoryFilter: StateFlow<com.example.data.LearnCategory?> = _learnCategoryFilter

        val uiState: StateFlow<WorkAtlasUiState> = combine(
        _currentTab,
        _searchQuery,
        _selectedCategory,
        localRepository.bookmarkedJobsFlow,
        _showOnlyBookmarked,
        _selectedCompanyForDetail,
        localRepository.completedLessonsFlow,
        _selectedLessonForRead,
        _learnCategoryFilter,
        localRepository.recentSearchesFlow,
        reminderPreferences.state
    ) { params: Array<Any?> ->
        val tab = params[0] as AppNavTab
        val query = params[1] as String
        val category = params[2] as JobCategory
        @Suppress("UNCHECKED_CAST")
        val bookmarkEntities = params[3] as List<com.example.data.local.BookmarkedJobEntity>
        val onlyBookmarks = params[4] as Boolean
        val detailCompany = params[5] as Company?
        @Suppress("UNCHECKED_CAST")
        val completedLessons = params[6] as Set<String>
        val readLesson = params[7] as LearnLesson?
        val learnFilter = params[8] as com.example.data.LearnCategory?
        @Suppress("UNCHECKED_CAST")
        val recentSearches = params[9] as List<String>
        val reminder = params[10] as ReminderState

        val bookmarks = bookmarkEntities.map { it.companyId }.toSet()
        val appStatuses = bookmarkEntities.associate { entity ->
            val parsedStatus = try {
                ApplicationStatus.valueOf(entity.status)
            } catch (e: Exception) {
                ApplicationStatus.SAVED
            }
            entity.companyId to parsedStatus
        }

        val cleanQuery = query.trim().lowercase()
        val filtered = CompanyDirectory.companies.filter { company ->
            val matchesCategory = (category == JobCategory.ALL || company.category == category)
            val matchesQuery = cleanQuery.isEmpty() ||
                    company.name.lowercase().contains(cleanQuery) ||
                    company.description.lowercase().contains(cleanQuery) ||
                    company.payoutMethods.any { it.lowercase().contains(cleanQuery) } ||
                    company.tags.any { it.lowercase().contains(cleanQuery) }
            val matchesBookmark = !onlyBookmarks || bookmarks.contains(company.id)

            matchesCategory && matchesQuery && matchesBookmark
        }

        WorkAtlasUiState(
            currentTab = tab,
            totalCompanyCount = CompanyDirectory.companies.size,
            searchQuery = query,
            recentSearches = recentSearches,
            selectedCategory = category,
            bookmarkedIds = bookmarks,
            applicationStatuses = appStatuses,
            showOnlyBookmarked = onlyBookmarks,
            filteredCompanies = filtered,
            selectedCompanyForDetail = detailCompany,
            lessons = LearnLessonsData.lessons,
            completedLessonIds = completedLessons,
            selectedLessonForRead = readLesson,
            learnCategoryFilter = learnFilter,
            isDatabaseSynced = true,
            isReminderEnabled = reminder.isEnabled,
            reminderHour = reminder.hour,
            reminderMinute = reminder.minute
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WorkAtlasUiState(
            totalCompanyCount = CompanyDirectory.companies.size,
            filteredCompanies = CompanyDirectory.companies
        )
    )
    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: JobCategory) {
        _selectedCategory.value = category
    }

    fun toggleBookmark(companyId: Int) {
        viewModelScope.launch {
            val isBookmarked = uiState.value.bookmarkedIds.contains(companyId)
            if (isBookmarked) {
                localRepository.removeBookmark(companyId)
            } else {
                localRepository.saveBookmark(companyId, ApplicationStatus.SAVED)
            }
        }
    }

    fun updateApplicationStatus(companyId: Int, status: ApplicationStatus) {
        viewModelScope.launch {
            localRepository.updateStatus(companyId, status)
        }
    }

    fun toggleShowOnlyBookmarked() {
        _showOnlyBookmarked.value = !_showOnlyBookmarked.value
    }

    fun openCompanyDetail(company: Company) {
        _selectedCompanyForDetail.value = company
    }

    fun closeCompanyDetail() {
        _selectedCompanyForDetail.value = null
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    // Recent Searches Actions with local Room synchronization
    fun saveRecentSearch(query: String) {
        viewModelScope.launch {
            localRepository.saveRecentSearch(query)
        }
    }

    fun deleteRecentSearch(query: String) {
        viewModelScope.launch {
            localRepository.deleteRecentSearch(query)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            localRepository.clearRecentSearches()
        }
    }

    // Daily Reminder Actions with WorkManager synchronization
    fun setDailyReminder(enabled: Boolean, hour: Int, minute: Int) {
        reminderPreferences.setReminder(enabled, hour, minute)
        if (enabled) {
            DailyReminderScheduler.scheduleDailyReminder(getApplication(), hour, minute)
        } else {
            DailyReminderScheduler.cancelDailyReminder(getApplication())
        }
    }

    // Learn Hub Actions with local Room synchronization
    fun openLesson(lesson: LearnLesson) {
        _selectedLessonForRead.value = lesson
    }

    fun closeLesson() {
        _selectedLessonForRead.value = null
    }

    fun toggleLessonCompleted(lessonId: String) {
        viewModelScope.launch {
            val isCompleted = uiState.value.completedLessonIds.contains(lessonId)
            if (isCompleted) {
                localRepository.unmarkLessonCompleted(lessonId)
            } else {
                localRepository.markLessonCompleted(lessonId)
            }
        }
    }

    fun setLearnCategoryFilter(category: com.example.data.LearnCategory?) {
        _learnCategoryFilter.value = category
    }
}

