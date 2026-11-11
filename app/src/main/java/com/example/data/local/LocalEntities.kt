package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_jobs")
data class BookmarkedJobEntity(
    @PrimaryKey val companyId: Int,
    val status: String, // ApplicationStatus name: SAVED, APPLIED, ASSESSMENT, INTERVIEW, OFFERED
    val savedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "completed_lessons")
data class CompletedLessonEntity(
    @PrimaryKey val lessonId: String,
    val completedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)
