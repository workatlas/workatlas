package com.example.reminder

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ReminderState(
    val isEnabled: Boolean = false,
    val hour: Int = 9,
    val minute: Int = 0
)

class ReminderPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("workatlas_reminder_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(
        ReminderState(
            isEnabled = prefs.getBoolean(KEY_IS_ENABLED, false),
            hour = prefs.getInt(KEY_HOUR, 9),
            minute = prefs.getInt(KEY_MINUTE, 0)
        )
    )
    val state: StateFlow<ReminderState> = _state.asStateFlow()

    fun setReminder(enabled: Boolean, hour: Int, minute: Int) {
        prefs.edit()
            .putBoolean(KEY_IS_ENABLED, enabled)
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()
        _state.value = ReminderState(enabled, hour, minute)
    }

    companion object {
        private const val KEY_IS_ENABLED = "key_reminder_enabled"
        private const val KEY_HOUR = "key_reminder_hour"
        private const val KEY_MINUTE = "key_reminder_minute"
    }
}
