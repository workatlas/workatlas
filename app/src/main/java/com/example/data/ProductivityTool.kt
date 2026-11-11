package com.example.data

enum class ProductivityToolCategory(val displayName: String) {
    ALL("All Tools"),
    RESEARCH_STUDY("Study & Research"),
    WRITING_GRAMMAR("Writing & Formatting"),
    AUDIO_TRANSCRIBE("Audio & Speech"),
    CODING_TECH("Coding & Logic")
}

data class ProductivityTool(
    val id: String,
    val name: String,
    val provider: String,
    val category: ProductivityToolCategory,
    val description: String,
    val ethicalUseGuide: String,
    val url: String,
    val isFreeTierAvailable: Boolean = true
)

object ProductivityToolsData {
    val tools: List<ProductivityTool> = listOf(
        ProductivityTool(
            id = "gemini",
            name = "Google Gemini",
            provider = "Google",
            category = ProductivityToolCategory.RESEARCH_STUDY,
            description = "Frontier multimodal AI with deep live web search grounding, ideal for fact-checking claims and cross-referencing research topics.",
            ethicalUseGuide = "Use for researching background facts, understanding complex historical dates, or debugging concepts. DO NOT copy-paste answers during live platform qualification tests.",
            url = "https://gemini.google.com"
        ),
        ProductivityTool(
            id = "notebooklm",
            name = "NotebookLM",
            provider = "Google",
            category = ProductivityToolCategory.RESEARCH_STUDY,
            description = "Personalized AI research assistant that sources insights strictly from user-uploaded PDFs, guidelines, and project rubrics.",
            ethicalUseGuide = "Upload 50-page platform guideline PDFs (e.g., search quality or annotation handbooks) to quiz yourself and search tricky edge-case rules before starting real tasks.",
            url = "https://notebooklm.google.com"
        ),
        ProductivityTool(
            id = "claude",
            name = "Claude",
            provider = "Anthropic",
            category = ProductivityToolCategory.WRITING_GRAMMAR,
            description = "Constitutional AI renowned for nuanced reasoning, clear formatting, and following complex multi-constraint instructions.",
            ethicalUseGuide = "Study how Claude compares different prose drafts and structures comparative arguments to understand how high-tier model evaluations work.",
            url = "https://claude.ai"
        ),
        ProductivityTool(
            id = "chatgpt",
            name = "ChatGPT",
            provider = "OpenAI",
            category = ProductivityToolCategory.RESEARCH_STUDY,
            description = "Versatile conversational AI model useful for brainstorming study questions and generating practice prompts for RLHF training.",
            ethicalUseGuide = "Generate practice side-by-side responses to sharpen your own human evaluation criteria before taking entry assessments.",
            url = "https://chatgpt.com"
        ),
        ProductivityTool(
            id = "grammarly",
            name = "Grammarly",
            provider = "Grammarly Inc.",
            category = ProductivityToolCategory.WRITING_GRAMMAR,
            description = "Real-time spelling, punctuation, and tone checker that prevents careless grammatical slips in written critiques.",
            ethicalUseGuide = "Keep basic spelling and punctuation corrections enabled on client communications and resumes. Avoid full AI rewrite generation on testing portals.",
            url = "https://www.grammarly.com"
        ),
        ProductivityTool(
            id = "whisper",
            name = "OpenAI Whisper",
            provider = "OpenAI",
            category = ProductivityToolCategory.AUDIO_TRANSCRIBE,
            description = "State-of-the-art automatic speech recognition system supporting robust multilingual audio transcription.",
            ethicalUseGuide = "Use open-source audio tools to analyze noise floors, check audio clarity, and benchmark human transcription accuracy.",
            url = "https://openai.com/research/whisper"
        ),
        ProductivityTool(
            id = "otter",
            name = "Otter.ai",
            provider = "Otter.ai Inc.",
            category = ProductivityToolCategory.AUDIO_TRANSCRIBE,
            description = "Automated voice meeting transcription and automated summary note generator.",
            ethicalUseGuide = "Great for recording and transcribing your own practice interview answers to check for filler words ('um', 'uh') and speaking pace.",
            url = "https://otter.ai"
        ),
        ProductivityTool(
            id = "github_copilot",
            name = "GitHub Copilot",
            provider = "GitHub / Microsoft",
            category = ProductivityToolCategory.CODING_TECH,
            description = "AI pair programmer providing autocompletions and syntax suggestions across 50+ programming languages.",
            ethicalUseGuide = "Accelerate personal coding projects and learn syntax variations. Never use during unassisted coding tests (e.g. HackerRank/CodeSignal tests for micro1).",
            url = "https://github.com/features/copilot"
        )
    )
}
