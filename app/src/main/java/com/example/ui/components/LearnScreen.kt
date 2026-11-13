package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LearnCategory
import com.example.data.LearnLesson
import com.example.ui.theme.AtlasDarkNavy
import com.example.ui.theme.AtlasNavy
import com.example.ui.theme.AtlasNavyBorder
import com.example.ui.theme.AtlasNavySurface
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LearnScreen(
    lessons: List<LearnLesson>,
    completedIds: Set<String>,
    selectedCategory: LearnCategory?,
    onCategorySelected: (LearnCategory?) -> Unit,
    onLessonClick: (LearnLesson) -> Unit,
    onToggleCompleted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalLessons = lessons.size
    val completedCount = completedIds.size
    val progress = if (totalLessons > 0) completedCount.toFloat() / totalLessons.toFloat() else 0f

    val filteredLessons = if (selectedCategory == null) {
        lessons
    } else {
        lessons.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasDarkNavy)
            .testTag("learn_screen_list"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(key = "learn_header") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(AtlasDarkNavy, AtlasNavy)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "E-Learning & Interview Prep",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Master AI training, rubrics, and high-paying gigs",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AtlasNavySurface,
                        border = BorderStroke(1.dp, AtlasNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = EmeraldLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Training Track Completion",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = "$completedCount of $totalLessons (${(progress * 100).toInt()}%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = EmeraldPrimary,
                                trackColor = AtlasDarkNavy
                                                     )
                        }
                    }
                }
            }
        }

               item(key = "learn_category_filters") {
            val options = listOf(
                BentoFilterOption<LearnCategory?>(value = null, label = "All Topics")
            ) + LearnCategory.entries.map { category ->
                BentoFilterOption<LearnCategory?>(value = category, label = category.displayName)
            }
            BentoFilterGrid(
                options = options,
                selectedValue = selectedCategory,
                onSelect = onCategorySelected
            )
        }

        items(
            items = filteredLessons,
            key = { it.id }
        ) { lesson ->

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun LessonReaderBottomSheet(
    lesson: LearnLesson,
    isCompleted: Boolean,
    onToggleCompleted: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AtlasNavySurface,
        modifier = Modifier.testTag("lesson_reader_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = lesson.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldLight,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = lesson.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${lesson.readTimeMinutes} min read • Comprehensive Guide",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AtlasDarkNavy,
                border = BorderStroke(1.dp, AtlasNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Key Takeaways",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    lesson.keyTakeaways.forEach { takeaway ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Text(text = "• ", color = EmeraldLight, fontWeight = FontWeight.Bold)
                            Text(
                                text = takeaway,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            lesson.fullContent.split("\n\n").forEach { paragraph ->
                if (paragraph.startsWith("# ")) {
                    Text(
                        text = paragraph.removePrefix("# "),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                    )
                } else if (paragraph.startsWith("### ")) {
                    Text(
                        text = paragraph.removePrefix("### "),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldLight,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                    )
                } else {
                    Text(
                        text = paragraph,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onToggleCompleted()
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleted) AtlasNavyBorder else EmeraldPrimary,
                    contentColor = if (isCompleted) TextPrimary else AtlasDarkNavy
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("mark_lesson_completed_btn")
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCompleted) "Marked as Completed ✓" else "Mark Lesson as Completed",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
