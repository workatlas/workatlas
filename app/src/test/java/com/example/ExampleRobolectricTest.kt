package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppNavTab
import com.example.data.CompanyDirectory
import com.example.data.JobCategory
import com.example.data.local.RecentSearchEntity
import com.example.data.local.WorkAtlasDatabase
import com.example.data.local.WorkAtlasLocalRepository
import com.example.reminder.DailyJobReminderWorker
import com.example.reminder.DailyReminderScheduler
import com.example.reminder.ReminderPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WorkAtlas", appName)
    }

    @Test
    fun `verify directory contains all 100+ companies with required fields`() {
        val list = CompanyDirectory.companies
        assertTrue("Directory should contain 100+ entries", list.size >= 100)

        list.forEach { company ->
            assertTrue("Company name should not be blank", company.name.isNotBlank())
            assertTrue("Description should not be blank for ${company.name}", company.description.isNotBlank())
            assertTrue("Career URL should start with http for ${company.name}", company.careerUrl.startsWith("http"))
            assertTrue("Payout methods should not be empty for ${company.name}", company.payoutMethods.isNotEmpty())
            assertTrue("Category should not be ALL for ${company.name}", company.category != JobCategory.ALL)
        }
    }

    @Test
    fun `verify specific companies exist and referral links are updated`() {
        val list = CompanyDirectory.companies
        val companyMap = list.associateBy { it.name }

        assertTrue(companyMap.containsKey("micro1"))
        assertEquals("https://micro1.ai", companyMap["micro1"]?.careerUrl)

        assertTrue(companyMap.containsKey("Babel Audio"))
        assertEquals("https://babel.audio", companyMap["Babel Audio"]?.careerUrl)

        assertTrue(companyMap.containsKey("Mercor"))
        assertEquals("https://t.mercor.com/ZHGlm", companyMap["Mercor"]?.careerUrl)

        assertTrue(companyMap.containsKey("micro1 for Companies"))
        assertEquals("https://micro1.ai", companyMap["micro1 for Companies"]?.careerUrl)

        assertTrue(companyMap.containsKey("Spout"))
        assertEquals("https://withspout.com", companyMap["Spout"]?.careerUrl)

        assertTrue(companyMap.containsKey("Visora"))
        assertEquals("https://visora.com/jobs", companyMap["Visora"]?.careerUrl)
    }

    @Test
    fun `verify room local persistence for bookmarks and lesson completion`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = WorkAtlasDatabase.getDatabase(context)
        val repository = WorkAtlasLocalRepository(db)

        // Test Bookmark Save & Status Update
        repository.saveBookmark(10, com.example.data.ApplicationStatus.ASSESSMENT)
        val bookmarks = repository.bookmarkedJobsFlow.first()
        val found = bookmarks.find { it.companyId == 10 }
        assertTrue("Company 10 should be saved in Room", found != null)
        assertEquals("ASSESSMENT", found?.status)

        // Test Lesson Completion Persistence
        repository.markLessonCompleted("lesson_rlhf")
        val completedLessons = repository.completedLessonsFlow.first()
        assertTrue("lesson_rlhf should be marked as completed in Room", completedLessons.contains("lesson_rlhf"))

        // Test Unmarking
        repository.unmarkLessonCompleted("lesson_rlhf")
        val updatedLessons = repository.completedLessonsFlow.first()
        assertTrue("lesson_rlhf should be removed after unmarking", !updatedLessons.contains("lesson_rlhf"))

        // Test Removing Bookmark
        repository.removeBookmark(10)
        val bookmarksAfterDelete = repository.bookmarkedJobsFlow.first()
        assertTrue("Company 10 should be deleted from Room", bookmarksAfterDelete.none { it.companyId == 10 })
    }

    @Test
    fun `verify room local persistence for recent searches history and maximum 6 limit`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = WorkAtlasDatabase.getDatabase(context)
        val repository = WorkAtlasLocalRepository(db)

        repository.clearRecentSearches()
        var current = repository.recentSearchesFlow.first()
        assertTrue("Recent searches should start empty", current.isEmpty())

        // Insert searches
        val searches = listOf("Python", "RLHF", "Data Annotation", "Remote AI", "Prompt Engineering", "Full Stack")
        for (query in searches) {
            repository.saveRecentSearch(query)
        }

        current = repository.recentSearchesFlow.first()
        assertEquals("Should store up to 6 searches", 6, current.size)
        assertEquals("Most recent search should be at the front", "Full Stack", current[0])

        // Add 7th search to verify FIFO / trim behavior (storing up to the 6 most recent unique searches)
        repository.saveRecentSearch("QA Testing")
        current = repository.recentSearchesFlow.first()
        assertEquals("Should still store at most 6 searches", 6, current.size)
        assertEquals("Latest search should be first", "QA Testing", current[0])
        assertFalse("Oldest search ('Python') should have been trimmed out", current.contains("Python"))

        // Re-searching an existing query should bump it to the front without duplicates
        repository.saveRecentSearch("RLHF")
        current = repository.recentSearchesFlow.first()
        assertEquals("Size should remain 6", 6, current.size)
        assertEquals("'RLHF' should be bumped to front", "RLHF", current[0])
        assertEquals("Only one occurrence of 'RLHF'", 1, current.count { it == "RLHF" })

        // Delete a specific search
        repository.deleteRecentSearch("RLHF")
        current = repository.recentSearchesFlow.first()
        assertEquals("Size should decrease to 5", 5, current.size)
        assertFalse("Should not contain deleted query", current.contains("RLHF"))

        // Clear all history
        repository.clearRecentSearches()
        current = repository.recentSearchesFlow.first()
        assertTrue("Searches should be empty after clear", current.isEmpty())
    }

    @Test
    fun `verify daily reminder scheduler initial delay calculation`() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nowMillis = calendar.timeInMillis

        // Target at 11:30 AM (future today, 1h 30m away)
        val futureDelay = DailyReminderScheduler.calculateInitialDelay(11, 30, nowMillis)
        val expectedFutureMs = (1 * 60 + 30) * 60 * 1000L
        assertEquals(expectedFutureMs, futureDelay)

        // Target at 8:00 AM (past today, 22 hours away tomorrow)
        val pastDelay = DailyReminderScheduler.calculateInitialDelay(8, 0, nowMillis)
        val expectedPastMs = 22 * 60 * 60 * 1000L
        assertEquals(expectedPastMs, pastDelay)
    }

    @Test
    fun `verify reminder preferences persistence`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = ReminderPreferences(context)

        // Default state
        val initialState = prefs.state.value
        assertFalse("Default reminder should be disabled", initialState.isEnabled)

        // Set reminder ON at 8:30 AM
        prefs.setReminder(enabled = true, hour = 8, minute = 30)
        val updatedState = prefs.state.value
        assertTrue("Reminder should be enabled", updatedState.isEnabled)
        assertEquals(8, updatedState.hour)
        assertEquals(30, updatedState.minute)

        // Toggle OFF
        prefs.setReminder(enabled = false, hour = 8, minute = 30)
        assertFalse("Reminder should be disabled", prefs.state.value.isEnabled)
    }

    @Test
    fun `verify daily reminder worker intent targets Jobs tab`() {
        assertEquals("TARGET_TAB", DailyJobReminderWorker.EXTRA_TARGET_TAB)
        assertEquals("JOBS", AppNavTab.JOBS.name)
    }
}
