package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.ApplicationStatus
import com.example.data.Company
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SavedJobsCsvExporter {

    /**
     * Exports bookmarked companies with their current application pipeline stage
     * into a standard CSV spreadsheet file and opens the Android Share/Save sheet.
     */
    fun exportAndShareCsv(
        context: Context,
        savedCompanies: List<Company>,
        statuses: Map<Int, ApplicationStatus>
    ) {
        if (savedCompanies.isEmpty()) {
            Toast.makeText(context, "No saved jobs to export. Bookmark companies first!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val exportsDir = File(context.cacheDir, "exports")
            exportsDir.mkdirs()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(exportsDir, "WorkAtlas_Saved_Jobs_$timestamp.csv")

            val writer = OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8)

            // UTF-8 BOM so Excel and spreadsheet apps render characters and accents perfectly
            writer.write("\uFEFF")

            // CSV Header
            writer.write("Company Name,Application Stage,Category,Payout Methods,Hiring Scope,Official Career URL,Description\n")

            savedCompanies.forEach { company ->
                val stage = statuses[company.id]?.displayName ?: ApplicationStatus.SAVED.displayName
                val nameEscaped = escapeCsv(company.name)
                val stageEscaped = escapeCsv(stage)
                val categoryEscaped = escapeCsv(company.category.displayName)
                val payoutEscaped = escapeCsv(company.payoutMethods.joinToString(" | "))
                val regionsEscaped = escapeCsv(company.hiringRegions)
                val urlEscaped = escapeCsv(company.careerUrl)
                val descEscaped = escapeCsv(company.description)

                writer.write("$nameEscaped,$stageEscaped,$categoryEscaped,$payoutEscaped,$regionsEscaped,$urlEscaped,$descEscaped\n")
            }

            writer.flush()
            writer.close()

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "WorkAtlas Saved Jobs Export")
                putExtra(Intent.EXTRA_TEXT, "Here is my exported remote jobs tracker from WorkAtlas (${savedCompanies.size} companies).")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Export Saved Jobs CSV").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })

            Toast.makeText(context, "Exported ${savedCompanies.size} companies to CSV!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export CSV: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun escapeCsv(value: String): String {
        val clean = value.replace("\"", "\"\"")
        return "\"$clean\""
    }
}
