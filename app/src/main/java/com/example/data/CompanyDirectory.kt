package com.example.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URL

object CompanyDirectory {
    private const val LIVE_GIST_URL =
        "https://githubusercontent.com"

  var companies: List<Company> by mutableStateOf(emptyList())
    private set

    init {
        companies = defaultCompanies
        refreshFromGist()
    }

    fun refreshFromGist() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonText = URL(LIVE_GIST_URL).readText()
                val parsed = parseCompanies(jsonText)
                if (parsed.isNotEmpty()) {
                    companies = parsed
                }
            } catch (_: Exception) {
                // Keeps offline defaultCompanies if user has no internet
            }
        }
    }

    private fun parseCompanies(jsonText: String): List<Company> {
        val array = JSONArray(jsonText)
        val result = mutableListOf<Company>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val catStr = obj.optString("category", "AI Training")
            val category = JobCategory.entries.find {
                it.displayName.equals(catStr, ignoreCase = true) || it.name.equals(catStr, ignoreCase = true)
            } ?: JobCategory.AI_TRAINING

            val payouts = mutableListOf<String>()
            obj.optJSONArray("payoutMethods")?.let { arr ->
                for (j in 0 until arr.length()) payouts.add(arr.getString(j))
            }

            val tags = mutableListOf<String>()
            obj.optJSONArray("tags")?.let { arr ->
                for (j in 0 until arr.length()) tags.add(arr.getString(j))
            }

            result.add(
                Company(
                    id = obj.optInt("id", i + 1),
                    name = obj.getString("name"),
                    description = obj.getString("description"),
                    category = category,
                    payoutMethods = payouts,
                    careerUrl = obj.getString("careerUrl"),
                    hiringRegions = obj.optString("hiringRegions", "Global Remote (Worldwide)"),
                    tags = tags
                )
            )
        }
        return result
    }

    private val defaultCompanies: List<Company> = listOf(

        Company(
            id = 1,
            name = "micro1",
            description = "AI-powered talent engine connecting vetted global software engineers and AI trainers with high-growth US tech companies.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Bank Wire", "Deel", "Wise"),
            careerUrl = "https://micro1.ai",
            tags = listOf("Software Engineering", "AI Tutor", "Full-time / Contract")
        ),
        Company(
            id = 2,
            name = "Alignerr",
            description = "Elite platform training state-of-the-art LLMs by hiring top domain experts, coders, mathematicians, and linguists worldwide.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("Deel", "Direct Deposit"),
            careerUrl = "https://www.alignerr.com",
            tags = listOf("LLM RLHF", "Domain Experts", "Prompt Evaluation")
        ),
        Company(
            id = 3,
            name = "AnnoTara AI",
            description = "Specialized AI data annotation and labeling provider offering remote image, text, and multimodal tagging roles.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("PayPal", "Direct Bank Transfer"),
            careerUrl = "https://annotara.ai",
            tags = listOf("Computer Vision", "Text Tagging", "Bounding Boxes")
        ),
        Company(
            id = 4,
            name = "Appen (CrowdGen)",
            description = "Global leader in AI training datasets and crowd services, offering search evaluation, speech transcription, and LLM tuning on CrowdGen.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Payoneer", "PayPal"),
            careerUrl = "https://crowdgen.com",
            tags = listOf("Search Evaluator", "Multilingual", "Large Crowd")
        ),
        Company(
            id = 5,
            name = "AppTek",
            description = "Pioneer in automatic speech recognition (ASR) and neural machine translation hiring remote linguists and transcriptionists.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("Wire Transfer", "Direct Deposit"),
            careerUrl = "https://www.apptek.com/careers",
            tags = listOf("Audio Transcription", "ASR Testing", "Phonetics")
        ),
        Company(
            id = 6,
            name = "Assemble",
            description = "Collaborative platform coordinating remote data enrichment, knowledge graph curation, and specialized AI labeling operations.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Wise", "PayPal", "Bank Transfer"),
            careerUrl = "https://www.assemble.inc",
            tags = listOf("Data Enrichment", "Content Review", "Flexible Hours")
        ),
        Company(
            id = 7,
            name = "Babel Audio",
            description = "Voice data collection and audio localization platform hiring freelance voice artists, transcribers, and dialect contributors.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal", "Wise"),
            careerUrl = "https://babel.audio",
            tags = listOf("Voice Prompts", "Acoustic Modeling", "Regional Dialects")
        ),
        Company(
            id = 8,
            name = "Braintrust",
            description = "Decentralized talent network where global software engineers and AI developers keep 100% of their earnings working with top enterprises.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Bank Deposit", "Crypto (USDC/BTRST)", "Wire"),
            careerUrl = "https://www.usebraintrust.com",
            tags = listOf("Senior Developers", "Zero Fee", "US Clients")
        ),
        Company(
            id = 9,
            name = "CloudFactory",
            description = "Combines human-in-the-loop intelligence and tech to deliver ethical data labeling for autonomous vehicles, medical AI, and fintech.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Direct Bank Transfer", "Payoneer"),
            careerUrl = "https://www.cloudfactory.com/careers",
            tags = listOf("Ethical AI", "Computer Vision", "Structured Teams")
        ),
        Company(
            id = 10,
            name = "DataAnnotation",
            description = "Premier remote platform paying competitive hourly rates for AI chatbot training, fact-checking, code benchmarking, and creative writing.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://www.dataannotation.tech",
            tags = listOf("High Hourly Rate", "Coding / Non-Coding", "Fast Payouts")
        ),
        Company(
            id = 11,
            name = "Outlier",
            description = "Scale AI's dedicated contributor network paying experts worldwide to review model answers, evaluate reasoning, and write code prompts.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("PayPal", "AirTM"),
            careerUrl = "https://outlier.ai",
            tags = listOf("Weekly Payout", "STEM Specialists", "RLHF Feedback")
        ),
        Company(
            id = 12,
            name = "Scale AI",
            description = "The generative AI infrastructure unicorn supplying human data to frontier labs (OpenAI, Anthropic, Meta) with diverse global roles.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("Direct Deposit", "Deel", "PayPal"),
            careerUrl = "https://scale.com/careers",
            tags = listOf("Frontier AI", "Enterprise Scale", "Remote & Hybrid")
        ),
        Company(
            id = 13,
            name = "Turing",
            description = "AI-powered talent cloud matching international software developers and AI training specialists with premier US Silicon Valley companies.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Wise", "Payoneer", "Direct Wire Transfer"),
            careerUrl = "https://www.turing.com",
            tags = listOf("Software Engineering", "Full-time Remote", "Long-term")
        ),
        Company(
            id = 14,
            name = "Remotasks",
            description = "Popular online micro-tasking and annotation community offering 2D/3D cuboid bounding, lidar tagging, and RLHF chat grading.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal", "AirTM"),
            careerUrl = "https://www.remotasks.com",
            tags = listOf("Microtasks", "LiDAR Annotation", "Beginner Friendly")
        ),
        Company(
            id = 15,
            name = "TELUS Digital",
            description = "Global digital solutions giant contracting remote AI Community Surfers, personalized search raters, media analysts, and transcribers.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Hyperwallet", "PayPal", "Direct Deposit"),
            careerUrl = "https://www.telusdigital.com/careers/ai-community",
            tags = listOf("Search Evaluator", "Part-time Flexible", "Established Brand")
        ),
        Company(
            id = 16,
            name = "OneForma",
            description = "Centific's crowdsourcing hub offering remote translation, audio recording, UHRS judging, LLM fine-tuning, and image tagging projects.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("Payoneer", "PayPal"),
            careerUrl = "https://www.oneforma.com",
            tags = listOf("UHRS Access", "Language Testing", "Frequent Tasks")
        ),
        Company(
            id = 17,
            name = "Welocalize",
            description = "Multinational localization and AI dataset vendor hiring Search Quality Raters, Ad Quality Evaluators, and bilingual transcribers.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Hyperwallet", "Direct Bank Deposit"),
            careerUrl = "https://jobs.welocalize.com",
            tags = listOf("Search & Ads Rating", "Bilingual Jobs", "W-2 / Freelance")
        ),
        Company(
            id = 18,
            name = "RWS TrainAI",
            description = "RWS's enterprise AI data services division deploying global freelance contributors for NLP validation, audio collection, and LLM safety.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("Direct Bank Wire", "Payoneer"),
            careerUrl = "https://www.rws.com/trainai",
            tags = listOf("AI Safety", "Linguistic Datasets", "Enterprise Clients")
        ),
        Company(
            id = 19,
            name = "Clickworker",
            description = "Massive European microtask platform giving international gig workers access to text creation, categorization, survey, and UHRS tasks.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("Payoneer", "PayPal", "SEPA"),
            careerUrl = "https://www.clickworker.com/clickworker",
            tags = listOf("UHRS Tasks", "Microtasks", "No Experience Required")
        ),
        Company(
            id = 20,
            name = "Toloka",
            description = "Global crowdsourcing and AI data platform engaging millions for computer vision labeling, audio validation, and LLM RLHF ranking.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("Payoneer", "Qiwi", "Papara"),
            careerUrl = "https://toloka.ai/crowd",
            tags = listOf("Instant Tasks", "Mobile App Available", "Fast Qualification")
        ),
        Company(
            id = 21,
            name = "Invisible Technologies",
            description = "Advanced operations platform combining synthetic intelligence with skilled remote knowledge operators for executive AI automation.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("Deel", "Wise"),
            careerUrl = "https://www.invisible.co/careers",
            tags = listOf("Advanced Ops", "Competitive Pay", "Structured Workflow")
        ),
        Company(
            id = 22,
            name = "Mercor",
            description = "Cutting-edge Silicon Valley talent platform vetting and placing top global software engineers, AI trainers, and researchers into top tech jobs.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Bank Transfer", "Stripe", "Wise"),
            careerUrl = "https://t.mercor.com/ZHGlm",
            tags = listOf("Top 1% Talent", "AI Interviews", "High Compensation")
        ),
        Company(
            id = 23,
            name = "Stellar AI",
            description = "Boutique AI research agency hiring specialized subject matter experts and developers to create benchmark-setting evaluation datasets.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("Wise", "Deel"),
            careerUrl = "https://www.stellar.tech",
            tags = listOf("Benchmarking", "Code Quality", "Subject Experts")
        ),
        Company(
            id = 24,
            name = "Mindrift (by Toloka)",
            description = "Writers and editors community crafting rich generative AI training prompts, synthetic literature, and detailed conversational dialogues.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("Payoneer"),
            careerUrl = "https://mindrift.ai",
            tags = listOf("Creative Writers", "Fact Checking", "Freelance Editor")
        ),
        Company(
            id = 25,
            name = "Soul AI",
            description = "Emerging remote platform engaging worldwide freelance contributors for model alignment, emotional intelligence tuning, and safety checks.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("PayPal", "Wise"),
            careerUrl = "https://www.soul.ai",
            tags = listOf("Model Alignment", "Red Teaming", "Flexible Shifts")
        ),
        Company(
            id = 26,
            name = "Pareto AI",
            description = "Human-in-the-loop data operations partner empowering remote professionals in developing markets with meaningful digital tasks.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Wise", "PayPal"),
            careerUrl = "https://hellopareto.com",
            tags = listOf("Data Sourcing", "Lead Generation", "Human Validation")
        ),
        Company(
            id = 27,
            name = "Sama",
            description = "Pioneer in ethical AI data labeling powering autonomous driving, biotech, and retail AI with verified living wages and fair work conditions.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Direct Bank Transfer", "Local Payroll"),
            careerUrl = "https://www.sama.com/careers",
            tags = listOf("Ethical B Corp", "Computer Vision", "Career Growth")
        ),
        Company(
            id = 28,
            name = "Defined.ai",
            description = "World's leading marketplace for ethically sourced, privacy-compliant AI training datasets with remote speech and text gigs.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://www.defined.ai/crowd",
            tags = listOf("Voice Collection", "Linguistic Corpus", "Micro Tasks")
        ),
        Company(
            id = 29,
            name = "LXT",
            description = "Global provider of customized AI training data for speech recognition, conversational AI, and natural language understanding.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal", "Direct Deposit"),
            careerUrl = "https://www.lxt.ai/careers",
            tags = listOf("Audio Recording", "Phonetic Tagging", "Over 750 Locales")
        ),
        Company(
            id = 30,
            name = "TransPerfect DataForce",
            description = "Worldwide community of annotators, transcribers, and study participants contributing to cutting-edge AI technologies across languages.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal", "Direct Deposit", "Gift Cards"),
            careerUrl = "https://www.transperfect.com/dataforce",
            tags = listOf("Field Studies", "Voice Gigs", "Multilingual")
        ),
        Company(
            id = 31,
            name = "Lionbridge",
            description = "Trusted global localization and AI testing enterprise contracting remote search raters, translators, and multimedia evaluators.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Direct Bank Deposit", "Wire Transfer"),
            careerUrl = "https://careers.lionbridge.com",
            tags = listOf("Media Quality", "Search Evaluation", "Longstanding Org")
        ),
        Company(
            id = 32,
            name = "iMerit",
            description = "Leading enterprise computer vision and NLP annotation partner employing tech talent for medical AI, robotics, and mapping.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Direct Bank Transfer"),
            careerUrl = "https://imerit.net/careers",
            tags = listOf("Autonomous Vehicles", "Medical Imaging", "Full-time Ops")
        ),
        Company(
            id = 33,
            name = "TaskUs",
            description = "High-growth BPO leader operating prominent hubs in the Philippines and worldwide, hiring for AI content moderation and LLM validation.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Direct Bank Deposit", "Local Payroll"),
            careerUrl = "https://www.taskus.com/careers",
            tags = listOf("PH Hubs", "Content Moderation", "Health Benefits")
        ),
        Company(
            id = 34,
            name = "Snorkel AI",
            description = "Pioneers of programmatic data development and weak supervision, hiring technical prompt engineers and machine learning contractors.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Deposit", "Wire Transfer"),
            careerUrl = "https://snorkel.ai/careers",
            tags = listOf("Programmatic Labeling", "ML Engineers", "Enterprise AI")
        ),
        Company(
            id = 35,
            name = "Labelbox",
            description = "The complete data factory platform for generative AI and computer vision, hiring professional labeling managers and domain reviewers.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Direct Bank Deposit", "Deel"),
            careerUrl = "https://labelbox.com/careers",
            tags = listOf("Data Platform", "Vision & NLP", "Model Diagnostics")
        ),
        Company(
            id = 36,
            name = "Encord",
            description = "Active learning and multimodal data curation platform hiring annotators and domain specialists to build trusted AI models.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Bank Wire", "Wise"),
            careerUrl = "https://encord.com/careers",
            tags = listOf("Microscopy / DICOM", "Video Segmentation", "Active Learning")
        ),
        Company(
            id = 37,
            name = "SuperAnnotate",
            description = "End-to-end annotation platform supporting fine-tuning LLMs and computer vision with global freelance annotation opportunities.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("PayPal", "Wise"),
            careerUrl = "https://www.superannotate.com/careers",
            tags = listOf("LiDAR Annotation", "RLHF Fine-Tuning", "Pixel Precision")
        ),
        Company(
            id = 38,
            name = "V7 Labs",
            description = "Leading vision AI platform enabling automated labeling for life sciences, healthcare, and robotics, regularly seeking expert reviewers.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Wise", "Bank Wire"),
            careerUrl = "https://www.v7labs.com/careers",
            tags = listOf("Biomedical Labeling", "Autonomous Systems", "SaaS Leader")
        ),
        Company(
            id = 39,
            name = "Dataloop",
            description = "Enterprise-grade data engine combining human labeling teams and automated pipelines for computer vision and generative AI.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Direct Bank Wire", "Payoneer"),
            careerUrl = "https://dataloop.ai/careers",
            tags = listOf("Data Pipelines", "Multimodal Datasets", "Enterprise Teams")
        ),
        Company(
            id = 40,
            name = "Hive (TheHive.ai)",
            description = "Cloud AI solutions provider offering remote labeling and moderation tasks via Hive Work with instant crypto and PayPal payouts.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal", "Bitcoin"),
            careerUrl = "https://thehive.ai",
            tags = listOf("Hive Work", "Instant Tasks", "Media Moderation")
        ),
        Company(
            id = 41,
            name = "Playment",
            description = "A TELUS International company specializing in high-precision sensor fusion and autonomous vehicle perception data labeling.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("PayPal", "Bank Transfer"),
            careerUrl = "https://www.playment.io",
            tags = listOf("3D Point Cloud", "Sensor Fusion", "Self-Driving Tech")
        ),
        Company(
            id = 42,
            name = "Surge AI",
            description = "The data labeling platform built for NLP and LLMs, paying elite hourly compensation to writers, coders, and language enthusiasts.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("PayPal", "Stripe"),
            careerUrl = "https://www.surgehq.ai/careers",
            tags = listOf("Elite Writing", "Hallucination Testing", "Top Hourly Rates")
        ),
        Company(
            id = 43,
            name = "Centific",
            description = "Global digital solutions partner deploying multilingual crowds for enterprise AI model evaluation, speech capture, and localization.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Payoneer", "Direct Wire Transfer"),
            careerUrl = "https://www.centific.com/careers",
            tags = listOf("Multilingual AI", "Global Crowd", "Enterprise IT")
        ),
        Company(
            id = 44,
            name = "Pactera EDGE",
            description = "AI and digitalization firm offering crowd engagements for machine translation quality estimation and audio transcription.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("Payoneer", "PayPal"),
            careerUrl = "https://www.pacteraedge.com/careers",
            tags = listOf("MT Quality", "Voice Recording", "Global Freelancers")
        ),
        Company(
            id = 45,
            name = "e2f",
            description = "Full-service data curation and localization firm hiring remote linguists, transcribers, and prompt evaluators worldwide.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal", "Wire Transfer"),
            careerUrl = "https://e2f.com/careers",
            tags = listOf("Linguistics", "Speech Audio", "Localization")
        ),
        Company(
            id = 46,
            name = "Andovar",
            description = "Global content localization firm contracting remote audio talents, voice actors, and translators for gaming, tech, and media.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("Wise", "PayPal", "Bank Transfer"),
            careerUrl = "https://andovar.com/careers",
            tags = listOf("Voiceover", "Game Localization", "Bilingual")
        ),
        Company(
            id = 47,
            name = "Moravia (RWS)",
            description = "Premier language services arm of RWS hiring digital product testers, linguistic reviewers, and AI corpus annotators.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Direct Deposit", "Wire Transfer"),
            careerUrl = "https://www.rws.com",
            tags = listOf("Linguistic QA", "Software Testing", "Enterprise")
        ),
        Company(
            id = 48,
            name = "Unbabel",
            description = "AI-powered language operations platform engaging global freelance bilingual editors to post-edit machine-translated customer support.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("PayPal", "Payoneer"),
            careerUrl = "https://unbabel.com",
            tags = listOf("Post-Editing", "Bilingual Support", "On-Demand Tasks")
        ),
        Company(
            id = 49,
            name = "Gengo",
            description = "Lionbridge company offering on-demand translation and transcription tasks for freelancers across dozens of language pairs.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal", "Payoneer"),
            careerUrl = "https://gengo.com/translators",
            tags = listOf("Translation Gigs", "Flexible Hours", "Language Pairs")
        ),
        Company(
            id = 50,
            name = "Translated",
            description = "Pioneer in AI-assisted translation providing freelance translators and language model evaluators steady remote workflow.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("PayPal", "Wire Transfer", "Payoneer"),
            careerUrl = "https://translated.com/translators",
            tags = listOf("Lara AI", "Language Modeling", "Prompt Tuning")
        ),

        // 51 - 100: Next 50 requested companies
        Company(
            id = 51,
            name = "Rev",
            description = "Top speech-to-text platform hiring freelance transcriptionists, captioners, and translators worldwide with weekly payouts.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://www.rev.com/freelancers",
            tags = listOf("Transcription", "Closed Captioning", "Weekly Payout")
        ),
        Company(
            id = 52,
            name = "TranscribeMe",
            description = "Transcription company offering flexible remote short-chunk audio typing gigs and medical/legal transcription specialization.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://www.transcribeme.com/jobs",
            tags = listOf("Audio Micro-chunks", "Work From Home", "Weekly PayPal")
        ),
        Company(
            id = 53,
            name = "Scribie",
            description = "Audio & video transcription service offering beginners and experienced transcribers short files, automated transcripts, and bonus incentives.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://scribie.com/freelance-transcription",
            tags = listOf("Beginner Friendly", "Short Audio", "Performance Bonus")
        ),
        Company(
            id = 54,
            name = "GoTranscript",
            description = "Global transcription provider hiring freelance transcribers, captioners, and editors in English and 50+ international languages.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal", "Payoneer"),
            careerUrl = "https://gotranscript.com/transcription-jobs",
            tags = listOf("Worldwide Freelancers", "Language Variety", "Weekly Payment")
        ),
        Company(
            id = 55,
            name = "CastingWords",
            description = "Online transcription workshop providing freelance transcribers and audio editors graded transcription tasks with quality grading bonuses.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://castingwords.com",
            tags = listOf("Transcription Workshop", "Quality Multiplier", "Flexible Shifts")
        ),
        Company(
            id = 56,
            name = "Speechpad",
            description = "Provider of high-accuracy audio transcription and video subtitling hiring freelance transcribers with bi-weekly PayPal payouts.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://www.speechpad.com/transcribers",
            tags = listOf("Captions", "Audio Typing", "Twice-Weekly Pay")
        ),
        Company(
            id = 57,
            name = "Verbit",
            description = "AI-powered captioning and transcription leader employing remote professional editors to clean up automated live and recorded transcripts.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal", "Stripe"),
            careerUrl = "https://verbit.ai/careers",
            tags = listOf("Acoustic Editing", "Live CART", "Legal & Academic")
        ),
        Company(
            id = 58,
            name = "3Play Media",
            description = "Premium media accessibility vendor contracting remote English and Spanish transcript editors and real-time captioners.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("Direct Deposit", "PayPal"),
            careerUrl = "https://www.3playmedia.com/company/jobs/contract-work",
            tags = listOf("Closed Captions", "Video Transcripts", "Media Accessibility")
        ),
        Company(
            id = 59,
            name = "Daily Transcription",
            description = "Specialized entertainment, corporate, and legal transcription company offering remote assignments for entertainment interviews and footage.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("Check", "Direct Deposit", "PayPal"),
            careerUrl = "https://dailytranscription.com/careers",
            tags = listOf("Hollywood / Media", "Timecode Sync", "Legal Transcripts")
        ),
        Company(
            id = 60,
            name = "Way With Words",
            description = "Established international audio-to-text service provider recruiting English speakers globally for nuanced corporate and legal transcription.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://waywithwords.net/transcription-jobs",
            tags = listOf("English Transcripts", "Global Applicants", "Accents")
        ),
        Company(
            id = 61,
            name = "Babbletype",
            description = "Market research transcription and content analysis firm hiring remote transcriptionists, proofreaders, and translators.",
            category = JobCategory.AUDIO_VOICE,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://babbletype.com",
            tags = listOf("Market Research", "Interview Audio", "Proofreading")
        ),
        Company(
            id = 62,
            name = "Crowdsurf",
            description = "Provider of on-demand closed captioning and transcription services for deaf and hard-of-hearing viewers with instant micro-tasks.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal", "WorkMarket"),
            careerUrl = "https://crowdsurfwork.com",
            tags = listOf("Short Video Clips", "Accessibility", "Instant Work")
        ),
        Company(
            id = 63,
            name = "Neevo",
            description = "Defined.ai's community app paying worldwide members for completing quick AI voice recordings, text validations, and image tagging.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://neevo.defined.ai",
            tags = listOf("Voice Tasks", "Mobile Friendly", "AI Validation")
        ),
        Company(
            id = 64,
            name = "Microworkers",
            description = "Crowdsourcing marketplace offering hundreds of international microjobs including app testing, data entry, survey completion, and labeling.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal", "Payoneer", "Skrill", "AirTM"),
            careerUrl = "https://www.microworkers.com",
            tags = listOf("Microtasks", "Global Gigs", "Fast Verification")
        ),
        Company(
            id = 65,
            name = "RapidWorkers",
            description = "Micro-freelancing platform featuring rapid online tasks such as social engagement, search verification, and basic data entry.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://rapidworkers.com",
            tags = listOf("Quick Tasks", "Simple Requirements", "Worldwide")
        ),
        Company(
            id = 66,
            name = "SproutGigs",
            description = "Online marketplace connecting employers with workers around the globe for micro-tasks, digital marketing gigs, and content curation.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("AirTM", "Litecoin", "Skrill"),
            careerUrl = "https://sproutgigs.com",
            tags = listOf("Crypto / AirTM", "Small Gigs", "Global Freelancers")
        ),
        Company(
            id = 67,
            name = "Amazon Mechanical Turk",
            description = "Amazon's legendary crowdsourcing marketplace allowing requesters to coordinate on-demand human intelligence tasks (HITs).",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("Amazon Payments", "Direct Deposit", "Gift Card"),
            careerUrl = "https://www.mturk.com",
            tags = listOf("HITs", "Amazon Ecosystem", "High Task Volume")
        ),
        Company(
            id = 68,
            name = "Prolific",
            description = "Ethical research participant platform paying high guaranteed hourly rates for scientific behavioral studies and AI model feedback.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://www.prolific.com",
            tags = listOf("Ethical Minimum Pay", "Academic Studies", "AI Benchmarking")
        ),
        Company(
            id = 69,
            name = "CloudResearch Connect",
            description = "Participant recruitment platform for academic surveys, market research, and LLM text evaluation with immediate payouts.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal", "Amazon Gift Card"),
            careerUrl = "https://connect.cloudresearch.com",
            tags = listOf("Academic Surveys", "Fast Payouts", "High Quality")
        ),
        Company(
            id = 70,
            name = "UserTesting",
            description = "Leading customer experience platform paying freelancers $10-$60 per test to record screen walkthroughs and voice feedback on websites/apps.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://www.usertesting.com/get-paid-to-test",
            tags = listOf("Usability Testing", "$10 - $60 Tests", "Screen & Voice")
        ),
        Company(
            id = 71,
            name = "Userlytics",
            description = "Global usability testing community testing websites, mobile apps, and prototypes with webcam/microphone feedback.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://www.userlytics.com/tester-signup",
            tags = listOf("App Walkthroughs", "User Experience", "Frequent Tests")
        ),
        Company(
            id = 72,
            name = "Trymata",
            description = "User testing platform (formerly TryMyUI) paying remote contributors to test websites and provide candid user experience thoughts.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://trymata.com/get-paid-to-test",
            tags = listOf("UX Feedback", "Weekly PayPal", "Screen Recording")
        ),
        Company(
            id = 73,
            name = "IntelliZoom",
            description = "UserZoom's participant network offering paid surveys and think-out-loud video studies for enterprise digital products.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://www.intellizoom.com",
            tags = listOf("Video Studies", "Enterprise UX", "Quick Studies")
        ),
        Company(
            id = 74,
            name = "Respondent",
            description = "High-paying qualitative research marketplace paying $50-$250/hr for business professionals, software developers, and consumers in video interviews.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("PayPal"),
            careerUrl = "https://www.respondent.io",
            tags = listOf("$50-$250 / Hour", "Tech Professionals", "1-on-1 Interviews")
        ),
        Company(
            id = 75,
            name = "User Interviews",
            description = "Research platform connecting participants with top tech companies (Figma, Spotify, Adobe) for paid remote user studies and focus groups.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal", "Gift Cards", "Direct Deposit"),
            careerUrl = "https://www.userinterviews.com/participants",
            tags = listOf("High Incentives", "Design Feedback", "Focus Groups")
        ),
        Company(
            id = 76,
            name = "Testbirds",
            description = "Crowdtesting company engaging freelance testers worldwide to find software bugs and usability feedback on real mobile devices.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("PayPal", "Bank Transfer"),
            careerUrl = "https://www.testbirds.com/en/become-a-tester",
            tags = listOf("Bug Bounty", "Device Variety", "Usability Testing")
        ),
        Company(
            id = 77,
            name = "uTest (Applause)",
            description = "The premier software testing community with over 1 million testers earning money discovering bugs and executing test cycles.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("PayPal", "Payoneer"),
            careerUrl = "https://www.utest.com",
            tags = listOf("QA Engineering", "Global Community", "Academy Training")
        ),
        Company(
            id = 78,
            name = "Test IO",
            description = "Leading crowdtesting platform paying freelance software testers for every verified defect, usability issue, and test run completed.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("PayPal", "Payoneer", "Bank Wire"),
            careerUrl = "https://test.io/become-a-tester",
            tags = listOf("Pay-per-bug", "QA Testing", "Global Remote")
        ),
        Company(
            id = 79,
            name = "Ferpection",
            description = "UX research agency hiring remote testers to explore mobile sites, apps, and ecommerce platforms and report structured user feedback.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal", "Amazon Gift Cards"),
            careerUrl = "https://ferpection.com/en/testers",
            tags = listOf("Mobile Sites", "Structured Feedback", "European & Global")
        ),
        Company(
            id = 80,
            name = "PingPong",
            description = "Remote user research video interview platform paying participants to talk with product designers and test web interfaces.",
            category = JobCategory.MICROTASKS,
            payoutMethods = listOf("PayPal", "TransferWise"),
            careerUrl = "https://hellopingpong.com/become-a-tester",
            tags = listOf("Live Interviews", "Product Design", "Fast Payment")
        ),
        Company(
            id = 81,
            name = "Andela",
            description = "Global private talent network connecting vetted software developers, data scientists, and cloud architects with global enterprises.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Bank Wire", "Deel", "Payoneer"),
            careerUrl = "https://www.andela.com/talent",
            tags = listOf("Senior Engineers", "Long-term Contracts", "Enterprise Scale")
        ),
        Company(
            id = 82,
            name = "Toptal",
            description = "Exclusive network of the top 3% of freelance software developers, designers, finance experts, and AI consultants in the world.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Bank Wire", "PayPal", "Payoneer"),
            careerUrl = "https://www.toptal.com/talent/apply",
            tags = listOf("Top 3% Talent", "Premier Rates", "Global Elite")
        ),
        Company(
            id = 83,
            name = "BairesDev",
            description = "Leading nearshore and offshore technology solutions company hiring top senior engineers and developers for US client contracts.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Bank Wire", "Payoneer"),
            careerUrl = "https://www.bairesdev.com/careers",
            tags = listOf("Top 1% Latin America & Global", "US Dollar Pay", "Full-time Remote")
        ),
        Company(
            id = 84,
            name = "Crossover",
            description = "Remote work platform recruiting elite software engineers and technology leaders for high-compensation, full-time contracts ($100k-$400k/yr).",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Payoneer", "Direct Deposit"),
            careerUrl = "https://www.crossover.com",
            tags = listOf("High Compensation", "Rigorous Testing", "40hrs/week Remote")
        ),
        Company(
            id = 85,
            name = "Lemon.io",
            description = "Curated freelance marketplace matching vetted startup-ready software engineers with fast-growing US and European startups.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Payoneer", "Wise"),
            careerUrl = "https://lemon.io/for-developers",
            tags = listOf("Startup Jobs", "Fast Matching", "Dev-first Culture")
        ),
        Company(
            id = 86,
            name = "Gun.io",
            description = "Elite developer-centric talent platform placing experienced software engineers into high-trust freelance and full-time remote roles.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Deposit", "Wire Transfer"),
            careerUrl = "https://www.gun.io",
            tags = listOf("Senior Devs", "Transparent Pay", "US Clients")
        ),
        Company(
            id = 87,
            name = "Arc.dev",
            description = "Remote career platform helping international developers get hired by US Silicon Valley tech companies and top global startups.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Deel", "Direct Bank Wire"),
            careerUrl = "https://arc.dev",
            tags = listOf("Permanent / Contract", "AI Matching", "Global Reach")
        ),
        Company(
            id = 88,
            name = "X-Team",
            description = "Elite international engineering community providing funded learning stipends and remote developer teams for top global brands (Fox, Riot).",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Bank Wire", "Deel"),
            careerUrl = "https://x-team.com/join",
            tags = listOf("Unleash Culture", "$2,500 Education Budget", "Long-term")
        ),
        Company(
            id = 89,
            name = "Proxify",
            description = "Network of 4,000+ top remote developers matching European and global tech businesses with senior developers across 100+ technologies.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Bank Wire", "Wise"),
            careerUrl = "https://proxify.io",
            tags = listOf("Stable Contracts", "Fast Onboarding", "European Clients")
        ),
        Company(
            id = 90,
            name = "Revelo",
            description = "Online platform connecting US tech companies with top pre-vetted remote software engineers from across Latin America and worldwide.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Deposit", "Deel", "Bank Wire"),
            careerUrl = "https://www.revelo.com",
            tags = listOf("Timezone Aligned", "Full-time Roles", "US Tech Scale")
        ),
        Company(
            id = 91,
            name = "Deel Talent",
            description = "Global payroll and hiring platform marketplace connecting worldwide remote contractors with international enterprises seeking tech talent.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Local Bank Transfer", "Deel Card", "Crypto", "Wise"),
            careerUrl = "https://www.deel.com",
            tags = listOf("Compliance & Payroll", "Global Scale", "Multiple Currencies")
        ),
        Company(
            id = 92,
            name = "Upwork",
            description = "The world's largest work marketplace offering millions of remote jobs in AI prompting, machine learning, coding, writing, and virtual assistance.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct to Local Bank (PHP)", "PayPal", "Payoneer", "Wire"),
            careerUrl = "https://www.upwork.com",
            tags = listOf("Huge Marketplace", "Direct Bank to PH", "Hourly & Fixed")
        ),
        Company(
            id = 93,
            name = "Fiverr",
            description = "Global gig marketplace enabling freelancers to sell specialized AI services, prompt packs, programming, voiceover, and data entry.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("PayPal", "Fiverr Revenue Card", "Bank Transfer"),
            careerUrl = "https://www.fiverr.com",
            tags = listOf("Productized Gigs", "AI Services", "Custom Orders")
        ),
        Company(
            id = 94,
            name = "Freelancer.com",
            description = "Global crowdsourcing marketplace featuring competitive project bidding and design/coding contests across 1,800+ skill categories.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Express Withdrawal (PH Banks)", "PayPal", "Skrill"),
            careerUrl = "https://www.freelancer.com",
            tags = listOf("Project Bidding", "Contests", "Global Crowd")
        ),
        Company(
            id = 95,
            name = "OnlineJobs.ph",
            description = "The Philippines' premier remote job board connecting thousands of Filipino virtual assistants, coders, writers, and AI raters to US employers.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("EasyPay", "PayPal", "Wise", "Direct Bank"),
            careerUrl = "https://www.onlinejobs.ph",
            tags = listOf("Dedicated PH Platform", "Work From Home", "Direct Client Contact")
        ),
        Company(
            id = 96,
            name = "VirtualStaff.ph",
            description = "Philippine remote talent marketplace providing verified full-time, part-time, and freelance remote jobs with secure payroll protection.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Bank Deposit (PHP)", "PayPal"),
            careerUrl = "https://www.virtualstaff.ph",
            tags = listOf("PH Focus", "Verified Employers", "Direct Hire")
        ),
        Company(
            id = 97,
            name = "RemoteStaff",
            description = "Specialized remote staffing agency sourcing Filipino talent for Australian and New Zealand SMEs with steady long-term contracts.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Bank Deposit (PH Banks)"),
            careerUrl = "https://www.remotestaff.ph",
            tags = listOf("AU / NZ Timezone", "BPO & Tech", "Health & Benefits")
        ),
        Company(
            id = 98,
            name = "Athena",
            description = "High-tier executive assistant and delegation partner training elite Filipino professionals to assist Silicon Valley and global tech founders.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Bank Deposit (PHP)", "Local Payroll"),
            careerUrl = "https://www.athena.com/careers",
            tags = listOf("Top Compensation", "HMO from Day 1", "Elite Training")
        ),
        Company(
            id = 99,
            name = "Magic",
            description = "On-demand remote workforce solution hiring remote virtual assistants, AI prompt operators, and tech specialists for US businesses.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Direct Bank Deposit", "PayPal"),
            careerUrl = "https://getmagic.com",
            tags = listOf("Flexible Work", "AI Integration", "Fast Hiring")
        ),
        Company(
            id = 100,
            name = "Boldr",
            description = "Purpose-driven BPO and digital services company with large hubs in Manila and Tacloban, supporting AI operations and data management.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Direct Bank Deposit", "Local Payroll"),
            careerUrl = "https://www.boldr.com/careers",
            tags = listOf("B-Corp Certified", "Manila & Tacloban", "Community Impact")
        ),

        // Additional requested referral & employer cards:
        Company(
            id = 101,
            name = "micro1 for Companies",
            description = "Hire vetted global AI developers, prompt engineers, and LLM trainers within 48 hours. Pre-screened with technical AI interviews.",
            category = JobCategory.TECH_CODING,
            payoutMethods = listOf("Enterprise Invoicing", "Credit Card", "Wire Transfer"),
            careerUrl = "https://micro1.ai",
            tags = listOf("For Employers", "Hire AI Talent", "48hr Matching"),
            hiringRegions = "Worldwide Employer Portal"
        ),
        Company(
            id = 102,
            name = "Spout",
            description = "Next-generation audio and voice AI platform engaging voice contributors, speech annotators, and AI speech trainers worldwide.",
            category = JobCategory.AI_TRAINING,
            payoutMethods = listOf("PayPal", "Stripe", "Direct Bank"),
            careerUrl = "https://withspout.com",
            tags = listOf("AI Audio", "Voice Data", "Speech Synthesis")
        ),
        Company(
            id = 103,
            name = "Visora",
            description = "Video operations and multi-modal AI data collection platform hiring remote contributors to record real-world tasks, screen workflows, and evaluate generative video models.",
            category = JobCategory.DATA_ANNOTATION,
            payoutMethods = listOf("Stripe", "Payoneer", "Direct Bank"),
            careerUrl = "https://visora.com/jobs",
            tags = listOf("Video AI", "Screen Recording", "Model Evaluation")
        )
    )
}
