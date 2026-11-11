package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.AtlasDarkNavy
import com.example.ui.theme.AtlasNavyBorder
import com.example.ui.theme.AtlasNavySurface
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReminderDialog(
    isOpen: Boolean,
    isReminderEnabled: Boolean,
    initialHour: Int,
    initialMinute: Int,
    onSave: (enabled: Boolean, hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    var isEnabled by remember(isReminderEnabled) { mutableStateOf(isReminderEnabled) }

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            onSave(true, timePickerState.hour, timePickerState.minute)
            Toast.makeText(
                context,
                "Daily reminder scheduled for ${formatTime(timePickerState.hour, timePickerState.minute)}",
                Toast.LENGTH_SHORT
            ).show()
            onDismiss()
        } else {
            isEnabled = false
            Toast.makeText(
                context,
                "Notification permission is needed for daily job reminders.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val handleSave = {
        if (isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onSave(true, timePickerState.hour, timePickerState.minute)
                Toast.makeText(
                    context,
                    "Daily reminder scheduled for ${formatTime(timePickerState.hour, timePickerState.minute)}",
                    Toast.LENGTH_SHORT
                ).show()
                onDismiss()
            }
        } else {
            onSave(false, timePickerState.hour, timePickerState.minute)
            Toast.makeText(context, "Daily reminder turned off", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AtlasNavySurface),
            border = BorderStroke(1.dp, AtlasNavyBorder),
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .testTag("daily_reminder_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon & Title
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Daily Job Reminder",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Get a daily nudge to explore new remote AI data, RLHF, and tech jobs on WorkAtlas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Switch Row
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AtlasDarkNavy,
                    border = BorderStroke(1.dp, AtlasNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Reminder",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isEnabled) {
                                    "Active at ${formatTime(timePickerState.hour, timePickerState.minute)}"
                                } else {
                                    "Notifications disabled"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isEnabled) EmeraldLight else TextMuted
                            )
                        }

                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AtlasDarkNavy,
                                checkedTrackColor = EmeraldPrimary,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = AtlasNavySurface
                            ),
                            modifier = Modifier.testTag("reminder_toggle_switch")
                        )
                    }
                }

                // TimePicker section
                AnimatedVisibility(
                    visible = isEnabled,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Choose Preferred Time",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        TimePicker(
                            state = timePickerState,
                            colors = TimePickerDefaults.colors(
                                clockDialColor = AtlasDarkNavy,
                                clockDialSelectedContentColor = AtlasDarkNavy,
                                clockDialUnselectedContentColor = TextPrimary,
                                selectorColor = EmeraldPrimary,
                                timeSelectorSelectedContainerColor = EmeraldPrimary.copy(alpha = 0.25f),
                                timeSelectorUnselectedContainerColor = AtlasDarkNavy,
                                timeSelectorSelectedContentColor = EmeraldLight,
                                timeSelectorUnselectedContentColor = TextPrimary,
                                periodSelectorSelectedContainerColor = EmeraldPrimary,
                                periodSelectorUnselectedContainerColor = AtlasDarkNavy,
                                periodSelectorSelectedContentColor = AtlasDarkNavy,
                                periodSelectorUnselectedContentColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("reminder_time_picker")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Cancel & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cancel_reminder_button")
                    ) {
                        Text(text = "Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = { handleSave() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = AtlasDarkNavy
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_reminder_button")
                    ) {
                        Text(
                            text = if (isEnabled) "Save Reminder" else "Turn Off",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm)
}
