package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProductivityTool
import com.example.data.ProductivityToolCategory
import com.example.data.ProductivityToolsData
import com.example.ui.theme.AtlasDarkNavy
import com.example.ui.theme.AtlasNavy
import com.example.ui.theme.AtlasNavyBorder
import com.example.ui.theme.AtlasNavySurface
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AdMobManager
import com.example.util.CustomTabHelper
import com.example.util.ResumePdfGenerator
import com.example.util.findActivity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiToolsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Resume Builder Form State
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var languages by remember { mutableStateOf("") }
    var selectedSkills by remember {
        mutableStateOf(
            setOf("AI Training & RLHF", "Prompt Evaluation", "Factuality & Verification", "Writing & Editing")
        )
    }
    var customSkillsInput by remember { mutableStateOf("Python, Fast Typing, Audio Transcription") }
    var experienceInput by remember {
        mutableStateOf(
            "Freelance Contributor • Remote Data Annotation & Evaluation\n" +
            "- Evaluated frontier LLM outputs against strict helpfulness, honesty, and safety rubrics.\n" +
            "- Provided comprehensive, evidence-based justifications comparing dual model responses.\n" +
            "- Performed high-accuracy text tagging, audio transcription, and data classification."
        )
    }

    var generatedResumeText by remember { mutableStateOf("") }
    var showPreview by remember { mutableStateOf(false) }
    var copiedToClipboard by remember { mutableStateOf(false) }

    // Curated Tools Filter State
    var selectedToolCategory by remember { mutableStateOf(ProductivityToolCategory.ALL) }

    val filteredTools = if (selectedToolCategory == ProductivityToolCategory.ALL) {
        ProductivityToolsData.tools
    } else {
        ProductivityToolsData.tools.filter { it.category == selectedToolCategory }
    }

    val availableSkillsList = listOf(
        "AI Training & RLHF",
        "Prompt Evaluation",
        "Factuality & Verification",
        "Writing & Editing",
        "Coding & Logic (Python/JS)",
        "Math & Quantitative",
        "Audio & Speech Transcription",
        "Customer Support & BPO",
        "Computer Vision Bounding"
    )

   LazyColumn(
    modifier = modifier
        .fillMaxSize()
        .background(AtlasDarkNavy)
        .testTag("ai_tools_screen")
) {

        // SECTION 1 HEADER: SMART RESUME BUILDER
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        EmeraldPrimary.copy(alpha = 0.35f),
                                        CyanNeon.copy(alpha = 0.2f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                       Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Smart Resume Builder",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = EmeraldPrimary.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "ATS OPTIMIZED",
                    color = EmeraldLight,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

                        Text(
                            text = "Format details for micro1, Alignerr, and AnnoTara AI in seconds",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // RESUME FORM CARD
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AtlasNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AtlasNavyBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "1. Personal & Contact Information",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLight
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name (e.g., Maria Santos)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = outlinedFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = outlinedFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Location
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location & Remote Availability") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = outlinedFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Languages
                    OutlinedTextField(
                        value = languages,
                        onValueChange = { languages = it },
                        label = { Text("Languages Spoken & Fluency") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = outlinedFieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // SKILLS SECTION
                    Text(
                        text = "2. Technical & Domain Skills (Tap to select)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availableSkillsList.forEach { skill ->
                            val isSelected = selectedSkills.contains(skill)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val updated = selectedSkills.toMutableSet()
                                    if (isSelected) updated.remove(skill) else updated.add(skill)
                                    selectedSkills = updated
                                },
                                label = { Text(skill, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = AtlasDarkNavy,
                                    containerColor = AtlasDarkNavy,
                                    labelColor = TextSecondary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) EmeraldPrimary else AtlasNavyBorder
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customSkillsInput,
                        onValueChange = { customSkillsInput = it },
                        label = { Text("Other Skills / Tools (Comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = outlinedFieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // WORK EXPERIENCE
                    Text(
                        text = "3. Work Experience & Background",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = experienceInput,
                        onValueChange = { experienceInput = it },
                        label = { Text("Experience bullet points, past roles, or degrees") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 6,
                        shape = RoundedCornerShape(10.dp),
                        colors = outlinedFieldColors()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // GENERATE BUTTON
                    Button(
                        onClick = {
                            val activity = context.findActivity()
                            val generateResumeAction = {
                                val combinedSkills = (selectedSkills + customSkillsInput.split(",").map { it.trim() })
                                    .filter { it.isNotBlank() }
                                    .joinToString(" • ")

                              val candidateName = fullName.ifBlank { "Remote Candidate" }
                              val candidateEmail = email.ifBlank { "candidate@email.com" }

                                generatedResumeText = """
============================================================
${candidateName.uppercase()}
$candidateEmail | $location
Languages: $languages
Target Focus: Remote AI Trainer, Data Annotation & Prompt Evaluator
============================================================

PROFESSIONAL SUMMARY
Dedicated and analytical remote professional specialized in Human-in-the-Loop AI training, data labeling, and technical evaluation. Experienced in evaluating complex LLM outputs for factuality, tone, helpfulness, and safety. Strict adherence to client rubrics and unassisted high-integrity originality.

TECHNICAL & DOMAIN SKILLS
$combinedSkills

EXPERIENCE & PROJECTS
$experienceInput

KEY REMOTE COMPETENCIES
- Prompt-response alignment and multi-turn dialogue ranking
- Verifiable factual cross-referencing and hallucination mitigation
- Noise-free audio capture and clean/verbatim transcription
- W-8BEN certified for international freelance remote contractor payouts
============================================================
                                """.trimIndent()

                                showPreview = true
                                copiedToClipboard = false
                            }

                            if (activity != null) {
                                AdMobManager.showInterstitialAd(activity, generateResumeAction)
                            } else {
                                generateResumeAction()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = AtlasDarkNavy
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_resume_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generate Tailored Resume",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    // RESUME PREVIEW & ACTION BUTTONS
                    AnimatedVisibility(
                        visible = showPreview,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(top = 18.dp)) {
                            Text(
                                text = "ATS-Friendly Preview",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AtlasDarkNavy,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = generatedResumeText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // One-Tap Copy Text Button
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(generatedResumeText))
                                        copiedToClipboard = true
                                        Toast.makeText(context, "Resume copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (copiedToClipboard) EmeraldPrimary else TextPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .testTag("copy_resume_btn")
                                ) {
                                    Icon(
                                        imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = if (copiedToClipboard) "Copied!" else "Copy Text")
                                }

                                // Share / Save as PDF Button
                                Button(
                                    onClick = {
                                        val combinedSkills = (selectedSkills + customSkillsInput.split(",").map { it.trim() })
                                            .filter { it.isNotBlank() }
                                            .joinToString(", ")

                                        ResumePdfGenerator.generateAndSharePdf(
                                            context = context,
                                            fullName = fullName.ifBlank { "Maria Santos" },
                                            email = email.ifBlank { "candidate@email.com" },
                                            location = location,
                                            languages = languages,
                                            skills = combinedSkills,
                                            experience = experienceInput
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldPrimary,
                                        contentColor = AtlasDarkNavy
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(44.dp)
                                        .testTag("share_pdf_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Share / Save PDF",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                                                    OutlinedButton(
                            onClick = {
                                val candidateName = fullName.ifBlank { "Remote Candidate" }
                                val skillsSummary = (selectedSkills + customSkillsInput.split(",").map { it.trim() })
                                    .filter { it.isNotBlank() }
                                    .joinToString(", ")
                                    .ifBlank { "AI Training, Prompt Evaluation, and Data Annotation" }
                                val pitchText = """
                                    Hi Hiring Team,

                                    My name is $candidateName, and I am applying for a remote AI & Data contributor role. I bring hands-on experience in $skillsSummary, with a strong focus on accuracy, clear communication, and following detailed quality guidelines.

                                    I have a reliable high-speed setup and am ready to begin immediately. Thank you for reviewing my application!
                                """.trimIndent()

                                clipboardManager.setText(AnnotatedString(pitchText))
                                Toast.makeText(context, "Application pitch copied! Paste it into your job form.", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Copy Application Pitch",
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                val candidateName = fullName.ifBlank { "Remote Candidate" }
                                val candidateContact = email.ifBlank { "Available upon request" }
                                val skillsSummary = (selectedSkills + customSkillsInput.split(",").map { it.trim() })
                                    .filter { it.isNotBlank() }
                                    .joinToString(", ")
                                    .ifBlank { "AI Training, Prompt Evaluation, and Data Annotation" }
                                val fullEmailText = """
                                    Subject: Remote AI & Data Contributor Application - $candidateName

                                    Dear Hiring Team,

                                    I am writing to express my interest in remote contributor opportunities with your team. With practical background in $skillsSummary, I specialize in producing accurate, rubric-aligned evaluations and clear written rationales.

                                    Highlights of my workflow:
                                    • Strong attention to detail and strict adherence to project guidelines
                                    • Reliable high-speed internet and quiet dedicated workspace
                                    • Ready to complete onboarding assessments immediately

                                    Thank you for your time and consideration. I look forward to the opportunity to contribute.

                                    Best regards,
                                    $candidateName
                                    $candidateContact
                                """.trimIndent()

                                clipboardManager.setText(AnnotatedString(fullEmailText))
                                Toast.makeText(context, "Full application email (Subject + Body) copied!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                           Text(
                                text = "Copy Full Application Email",
                                color = CyanNeon,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    } // 

        // VOICE & AUDIO NARRATOR AUDITION PRACTICE CARD
        item {
               
            val sampleScripts = listOf(
                "Conversational Warmth: \"Hey there! Thanks for reaching out today. Let me walk you through two quick options so you can pick the one that fits your schedule best.\"",
                "Clear Technical Narration: \"When evaluating two AI responses, verify every factual claim independently before scoring helpfulness, tone, and instruction following.\"",
                "Expressive Storytelling: \"Under a canopy of glowing stars, the explorer adjusted her compass, realizing the uncharted path ahead led straight to the summit.\""
            )
            var scriptIndex by remember { mutableStateOf(0) }
            var isSpeaking by remember { mutableStateOf(false) }

            val ttsEngine = remember {
                var engine: android.speech.tts.TextToSpeech? = null
                engine = android.speech.tts.TextToSpeech(context) { status ->
                    if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                        engine?.language = java.util.Locale.US
                    }
                }
                engine
            }

            androidx.compose.runtime.DisposableEffect(Unit) {
                onDispose {
                    ttsEngine?.stop()
                    ttsEngine?.shutdown()
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AtlasNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Voice & Narrator Audition Practice",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyanNeon.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "VOICE PREP",
                                color = CyanNeon,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Practice pacing and clarity for voice platforms (Babel Audio, AppTek) with sample scripts and reference audio.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AtlasDarkNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AtlasNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = sampleScripts[scriptIndex],
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                ttsEngine?.stop()
                                isSpeaking = false
                                scriptIndex = (scriptIndex + 1) % sampleScripts.size
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Next Script",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Button(
                            onClick = {
                                if (isSpeaking) {
                                    ttsEngine?.stop()
                                    isSpeaking = false
                                } else {
                                    val spokenText = sampleScripts[scriptIndex].substringAfter(": ").trim('"')
                                    ttsEngine?.speak(
                                        spokenText,
                                        android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                                        null,
                                        "audition_sample"
                                    )
                                    isSpeaking = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = AtlasDarkNavy
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isSpeaking) "Stop Reference" else "Listen Reference",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
        // SECTION 2 HEADER: CURATED AI LEARNING & PRODUCTIVITY TOOLS
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        CyanNeon.copy(alpha = 0.3f),
                                        EmeraldPrimary.copy(alpha = 0.2f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Curated AI Learning & Productivity Tools",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Free study aids, guidelines analyzers, and ethical usage rules",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

               // Category Filter Chips for Productivity Tools
                item {
            val options = ProductivityToolCategory.entries.map { cat ->
                BentoFilterOption(value = cat, label = cat.displayName)
            }
            BentoFilterGrid(
                options = options,
                selectedValue = selectedToolCategory,
                onSelect = { selectedToolCategory = it }
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = AtlasDarkNavy,
                            containerColor = AtlasNavySurface,
                            labelColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) EmeraldPrimary else AtlasNavyBorder
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }


        // List of Curated Tools with Ethical Guides
        items(
            items = filteredTools,
            key = { it.id }
        ) { tool ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AtlasNavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AtlasNavyBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tool.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${tool.provider} • ${tool.category.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldLight
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "FREE TIER",
                                color = EmeraldPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = tool.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AtlasDarkNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AtlasNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Ethical Study & Workflow Tip:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = tool.ethicalUseGuide,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { CustomTabHelper.openCustomTab(context, tool.url) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AtlasNavyBorder,
                            contentColor = CyanNeon
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Open Official Tool",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EmeraldPrimary,
    unfocusedBorderColor = AtlasNavyBorder,
    focusedContainerColor = AtlasDarkNavy,
    unfocusedContainerColor = AtlasDarkNavy,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = EmeraldLight,
    unfocusedLabelColor = TextSecondary
)

