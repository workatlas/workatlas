package com.example.data

enum class AppNavTab(val title: String) {
    JOBS("Jobs"),
    LEARN("Learn"),
    AI_TOOLS("AI Tools"),
    SAVED("Saved")
}

enum class ApplicationStatus(val displayName: String) {
    SAVED("Saved"),
    APPLIED("Applied"),
    ASSESSMENT("Assessment"),
    INTERVIEW("Interview"),
    OFFERED("Active / Hired")
}

enum class LearnCategory(val displayName: String) {
    AI_TRAINING("AI Training & Annotation"),
    PASSING_TESTS("Assessments & Interviews"),
    REMOTE_SETUP("Payout & Remote Setup")
}

data class LearnLesson(
    val id: String,
    val title: String,
    val category: LearnCategory,
    val readTimeMinutes: Int,
    val summary: String,
    val keyTakeaways: List<String>,
    val fullContent: String
)
