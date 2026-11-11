package com.example.reminder

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object DailyReminderScheduler {
    const val UNIQUE_WORK_NAME = "workatlas_daily_reminder_work"

    /**
     * Calculates delay in milliseconds until the next occurrence of [targetHour]:[targetMinute].
     * If the time has already passed today, schedules for tomorrow.
     */
    fun calculateInitialDelay(
        targetHour: Int,
        targetMinute: Int,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {
        val now = Calendar.getInstance().apply {
            timeInMillis = nowMillis
        }
        val target = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    /**
     * Enqueues a reliable daily periodic worker with WorkManager starting at the target time.
     */
    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        val initialDelay = calculateInitialDelay(hour, minute)

        val reminderRequest = PeriodicWorkRequestBuilder<DailyJobReminderWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .addTag("daily_job_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            reminderRequest
        )
    }

    /**
     * Cancels any scheduled daily job reminder.
     */
    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }
}
