package com.example.data

enum class JobCategory(val displayName: String) {
    ALL("All"),
    AI_TRAINING("AI Training"),
    DATA_ANNOTATION("Data Annotation"),
    AUDIO_VOICE("Audio & Voice"),
    TECH_CODING("Tech & Coding"),
    MICROTASKS("Microtasks")
}

data class Company(
    val id: Int,
    val name: String,
    val description: String,
    val category: JobCategory,
    val payoutMethods: List<String>,
    val careerUrl: String,
    val hiringRegions: String = "Global Remote (Worldwide)",
    val tags: List<String> = emptyList()
)  
