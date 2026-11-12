   // Category Filter Chips for Productivity Tools (scrolls edge-to-edge without clipping)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProductivityToolCategory.values().forEach { cat ->
                    val isSelected = selectedToolCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedToolCategory = cat },
                        label = {
                            Text(
                                text = cat.displayName,
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

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ApplicationStatus.values().forEach { statusOption ->
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
                                        selectedLabelColor = AtlasDarkNavy
                                    ),
                                    shape = RoundedCornerShape(12.dp)
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
}
