package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.OpenInNew
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ApplicationStatus
import com.example.data.Company
import com.example.ui.theme.AtlasDarkNavy
import com.example.ui.theme.AtlasNavyBorder
import com.example.ui.theme.AtlasNavySurface
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.SavedJobsCsvExporter

@OptIn(ExperimentalLayoutApi::class)
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
    val context = LocalContext.current

    val filteredList = if (selectedFilterStatus == null) {
        bookmarkedCompanies
    } else {
        bookmarkedCompanies.filter { (statuses[it.id] ?: ApplicationStatus.SAVED) == selectedFilterStatus }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasDarkNavy)
            .testTag("saved_tracker_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(key = "tracker_header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        Text(
                            text = "Application Pipeline",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${bookmarkedCompanies.size} Saved",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
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
                                )
                            }
                        }
                    }
                }

                if (bookmarkedCompanies.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
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
                        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("export_csv_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Export Pipeline CSV",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
        item(key = "tracker_filter_row") {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(key = "filter_all") {
                    val isSelected = selectedFilterStatus == null
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilterStatus = null },
                        label = {
                            Text(
                                text = "All (${bookmarkedCompanies.size})",
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = AtlasDarkNavy,
                            containerColor = AtlasNavySurface,
                            labelColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) EmeraldPrimary else AtlasNavyBorder
                        )
                    )
                }

                items(
                    items = ApplicationStatus.entries,
                    key = { it.name }
                ) { st ->
                    val count = bookmarkedCompanies.count {
                        (statuses[it.id] ?: ApplicationStatus.SAVED) == st
                    }
                    val isSelected = selectedFilterStatus == st
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilterStatus = st },
                        label = {
                            Text(
                                text = "${st.displayName} ($count)",
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = AtlasDarkNavy,
                            containerColor = AtlasNavySurface,
                            labelColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) EmeraldPrimary else AtlasNavyBorder
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (filteredList.isEmpty()) {
            item(key = "empty_tracker_state") {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AtlasNavySurface),
                    border = BorderStroke(1.dp, AtlasNavyBorder),
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
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(
                items = filteredList,
                key = { it.id }
            ) { company ->
                val currentStatus = statuses[company.id] ?: ApplicationStatus.SAVED
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AtlasNavySurface),
                    border = BorderStroke(1.dp, AtlasNavyBorder),
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
                            IconButton(onClick = { onRemoveBookmark(company.id) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Remove saved company",
                                    tint = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ApplicationStatus.entries.forEach { statusOption ->
                                val isSelected = currentStatus == statusOption
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onStatusChange(company.id, statusOption) },
                                    label = {
                                        Text(
                                            text = statusOption.displayName,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EmeraldPrimary,
                                        selectedLabelColor = AtlasDarkNavy,
                                        containerColor = AtlasDarkNavy,
                                        labelColor = TextSecondary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) EmeraldPrimary else AtlasNavyBorder
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onApplyClick(company) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = AtlasDarkNavy
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Open Application Portal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                softWrap = false
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
}
