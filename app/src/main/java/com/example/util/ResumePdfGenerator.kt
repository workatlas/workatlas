package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ResumePdfGenerator {

    /**
     * Generates a clean, ATS-compliant PDF document from candidate details,
     * saves it to the app cache directory, and opens the system Share/Save chooser.
     */
    fun generateAndSharePdf(
        context: Context,
        fullName: String,
        email: String,
        location: String,
        languages: String,
        skills: String,
        experience: String,
        targetRole: String = "Remote AI Trainer & Specialist"
    ) {
        try {
            val pdfDocument = PdfDocument()
            // Standard A4 dimensions at 72 dpi: 595 x 842 pt
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val titlePaint = Paint().apply {
                color = Color.parseColor("#0A192F")
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val subtitlePaint = Paint().apply {
                color = Color.parseColor("#059669")
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val contactPaint = Paint().apply {
                color = Color.parseColor("#4B5563")
                textSize = 10f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }

            val sectionHeaderPaint = Paint().apply {
                color = Color.parseColor("#0A192F")
                textSize = 13f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val linePaint = Paint().apply {
                color = Color.parseColor("#10B981")
                strokeWidth = 2f
            }

            val bodyPaint = Paint().apply {
                color = Color.parseColor("#1F2937")
                textSize = 10.5f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }

            var y = 50f
            val leftMargin = 45f
            val rightMargin = 550f

            // Full Name Header
            canvas.drawText(if (fullName.isNotBlank()) fullName else "Candidate Name", leftMargin, y, titlePaint)
            y += 18f

            // Target Role
            canvas.drawText(targetRole.uppercase(), leftMargin, y, subtitlePaint)
            y += 16f

            // Contact Bar
            val contactInfo = listOfNotNull(
                if (email.isNotBlank()) email else null,
                if (location.isNotBlank()) location else null,
                if (languages.isNotBlank()) "Languages: $languages" else null
            ).joinToString("  •  ")

            canvas.drawText(contactInfo, leftMargin, y, contactPaint)
            y += 24f

            // Helper to draw clean sections
            fun drawSection(title: String, content: String) {
                canvas.drawText(title.uppercase(), leftMargin, y, sectionHeaderPaint)
                y += 5f
                canvas.drawLine(leftMargin, y, rightMargin, y, linePaint)
                y += 16f

                // Word wrap content
                val words = content.split(" ", "\n")
                var currentLine = ""
                for (word in words) {
                    if (word == "\n") {
                        canvas.drawText(currentLine, leftMargin, y, bodyPaint)
                        y += 15f
                        currentLine = ""
                        continue
                    }
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    if (bodyPaint.measureText(testLine) > (rightMargin - leftMargin)) {
                        canvas.drawText(currentLine, leftMargin, y, bodyPaint)
                        y += 15f
                        currentLine = word
                    } else {
                        currentLine = testLine
                    }
                }
                if (currentLine.isNotEmpty()) {
                    canvas.drawText(currentLine, leftMargin, y, bodyPaint)
                    y += 20f
                }
            }

            // Professional Summary
            drawSection(
                "Professional Summary",
                "Dedicated, quality-focused professional specialized in Human-in-the-Loop AI training, data labeling, and technical evaluation. Known for strict instruction-following, rigorous fact-checking, and high-standard unassisted original reasoning."
            )

            // Technical & Domain Skills
            drawSection(
                "Technical & Domain Competencies",
                if (skills.isNotBlank()) skills else "RLHF, SFT, Factuality Verification, Multi-Turn Conversation Grading, Prompt Engineering, Data Annotation"
            )

            // Languages Spoken
            if (languages.isNotBlank()) {
                drawSection(
                    "Languages Spoken",
                    languages
                )
            }

            // Experience & Project History
            drawSection(
                "Experience & Background",
                if (experience.isNotBlank()) experience else "Freelance Contributor • Remote Data Annotation & Evaluation\n- Evaluated frontier LLM outputs against strict helpfulness, honesty, and safety rubrics.\n- Performed meticulous quality checks and edge-case testing for global client benchmarks."
            )

            pdfDocument.finishPage(page)

            // Save PDF to cache directory
            val cachePath = File(context.cacheDir, "resumes")
            cachePath.mkdirs()
            val fileName = "Resume_${fullName.replace(" ", "_").ifEmpty { "WorkAtlas" }}.pdf"
            val file = File(cachePath, fileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            // Open Share / Save intent via FileProvider or simple share
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "$fullName - Tailored Remote Resume")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Save or Share Resume PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })

        } catch (e: Exception) {
            Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
