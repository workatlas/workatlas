package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.ui.platform.LocalContext
import com.example.util.SavedJobsCsvExporter
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ApplicationStatus
import com.example.data.Company
import com.example.ui.theme.AtlasDarkNavy
import com.example.ui.theme.AtlasNavy
import com.example.ui.theme.AtlasNavyBorder
import com.example.ui.theme.AtlasNavySurface
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SavedTrackerScreen(
    bookmarkedCompanies: List<Company>,
    statuses: Map<Int, ApplicationStatus>,
    onStatusChange: (Int, ApplicationStatus) -> Unit,
    onRemoveBookmark: (Int) -> Unit,
    onApplyClick: (Company) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilterStatus by remember { mutableStateOf<ApplicationStatus?>(null) }

    val filteredList = if (selectedFilterStatus == null) {
        bookmarkedCompanies
    } else {
        bookmarkedCompanies.filter { (statuses[it.id] ?: ApplicationStatus.SAVED) == selectedFilterStatus }
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasDarkNavy)
            .testTag("saved_tracker_screen")
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentTurnedIn,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Application Pipeline Tracker",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "ROOM SYNCED",
                                    color = EmeraldLight,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                       }
                        }
                        Text(
                            text = "${bookmarkedCompanies.size} Saved Opportunities • Local SQLite Synced",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Export Saved Jobs Button
                    val context = LocalContext.current
                    OutlinedButton(
                        onClick = {
                            SavedJobsCsvExporter.exportAndShareCsv(
                                context = context,
                                savedCompanies = bookmarkedCompanies,
                                statuses = statuses
                            )
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = AtlasNavySurface,
                            contentColor = EmeraldLight
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("export_csv_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Export CSV",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        // Status Filter Chips (scrolls edge-to-edge cleanly without clipping)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilterStatus == null,
                    onClick = { selectedFilterStatus = null },
                    label = { Text("All (${bookmarkedCompanies.size})", maxLines = 1, softWrap = false) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = AtlasDarkNavy
                    ),
                    shape = RoundedCornerShape(18.dp)
                )

                ApplicationStatus.values().forEach { st ->
                    val count = bookmarkedCompanies.count { (statuses[it.id] ?: ApplicationStatus.SAVED) == st }
                    FilterChip(
                        selected = selectedFilterStatus == st,
                        onClick = { selectedFilterStatus = st },
                        label = { Text("${st.displayName} ($count)", maxLines = 1, softWrap = false) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = AtlasDarkNavy
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (filteredList.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AtlasNavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AtlasNavyBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No saved companies in this stage",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Explore the Jobs tab to save opportunities and update your application stage.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                   }
                }
            }
        } // <-- Keep ONLY this single closing brace for `if (filteredList.isEmpty())`

        items(
            items = filteredList,
            key = { it.id }
        ) { company ->

                val currentStatus = statuses[company.id] ?: ApplicationStatus.SAVED
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AtlasNavySurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AtlasNavyBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = company.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = company.category.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldLight
                                )
                            }

                            // Remove bookmark
                            IconButton(onClick = { onRemoveBookmark(company.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Remove",
                                    tint = EmeraldPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status Stage Selector
                        Text(
                            text = "Application Stage:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ApplicationStatus.values().forEach { stage ->
                                val isStage = stage == currentStatus
                                Surface(
                                    onClick = { onStatusChange(company.id, stage) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isStage) EmeraldPrimary else AtlasDarkNavy,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isStage) EmeraldPrimary else AtlasNavyBorder
                                    )
                                ) {
                                    Text(
                                        text = stage.displayName,
                                        color = if (isStage) AtlasDarkNavy else TextSecondary,
                                        fontWeight = if (isStage) FontWeight.Bold else FontWeight.Medium,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { onApplyClick(company) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AtlasNavyBorder,
                                contentColor = EmeraldLight
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Open Career Portal")
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
