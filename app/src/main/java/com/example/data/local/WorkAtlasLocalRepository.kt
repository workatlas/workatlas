package com.example.data.local

import com.example.data.ApplicationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkAtlasLocalRepository(private val database: WorkAtlasDatabase) {
    private val bookmarkDao = database.bookmarkedJobDao()
    private val lessonDao = database.completedLessonDao()
    private val recentSearchDao = database.recentSearchDao()

    val bookmarkedJobsFlow: Flow<List<BookmarkedJobEntity>> = bookmarkDao.getAllBookmarkedJobs()

    val completedLessonsFlow: Flow<Set<String>> = lessonDao.getAllCompletedLessons().map { list ->
        list.map { it.lessonId }.toSet()
    }

    val recentSearchesFlow: Flow<List<String>> = recentSearchDao.getRecentSearches().map { list ->
        list.map { it.query }
    }

    suspend fun saveRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        recentSearchDao.insertSearch(
            RecentSearchEntity(
                query = trimmed,
                timestamp = System.currentTimeMillis()
            )
        )
        recentSearchDao.trimToMaxRecent()
    }

    suspend fun deleteRecentSearch(query: String) {
        recentSearchDao.deleteSearch(query.trim())
    }

    suspend fun clearRecentSearches() {
        recentSearchDao.clearAllSearches()
    }

    suspend fun saveBookmark(companyId: Int, status: ApplicationStatus = ApplicationStatus.SAVED) {
        bookmarkDao.insertOrUpdateBookmark(
            BookmarkedJobEntity(
                companyId = companyId,
                status = status.name
            )
        )
    }

    suspend fun removeBookmark(companyId: Int) {
        bookmarkDao.deleteBookmarkByCompanyId(companyId)
    }

    suspend fun updateStatus(companyId: Int, status: ApplicationStatus) {
        bookmarkDao.insertOrUpdateBookmark(
            BookmarkedJobEntity(
                companyId = companyId,
                status = status.name
            )
        )
    }

    suspend fun markLessonCompleted(lessonId: String) {
        lessonDao.markLessonCompleted(CompletedLessonEntity(lessonId = lessonId))
    }

    suspend fun unmarkLessonCompleted(lessonId: String) {
        lessonDao.unmarkLessonCompleted(lessonId)
    }
}
