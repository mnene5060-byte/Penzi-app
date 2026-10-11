package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.supabase.SupabaseClaimTaskRequest
import com.example.data.supabase.SupabaseClaimTaskResponse
import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.SupabaseConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class TaskClaimResult(
    val success: Boolean,
    val alreadyClaimed: Boolean,
    val coinsReward: Int,
    val newStreak: Int,
    val message: String,
    val isServerVerified: Boolean
)

/**
 * Manages daily task collection states linked to calendar timezone and Supabase Edge Functions.
 * Resets automatically each calendar day at 00:00.
 */
class TaskManager(context: Context, val userId: String = "") {
    private val prefs: SharedPreferences = context.getSharedPreferences("penzi_tasks_prefs", Context.MODE_PRIVATE)

    companion object {
        const val TASK_DAILY_CHECKIN = "daily_checkin"
        const val TASK_IDENTITY_CHECK = "identity_check"
        const val TASK_FIRST_RECHARGE = "first_recharge"
        const val TASK_PROFILE_BIO = "profile_bio"
        const val KEY_STREAK_COUNT = "streak_count"
        const val KEY_LAST_STREAK_DATE = "last_streak_date"
    }

    private fun getTaskKey(taskId: String): String {
        return if (userId.isNotBlank()) "user_${userId}_task_${taskId}_last_date" else "task_${taskId}_last_date"
    }

    private fun getStreakCountKey(): String {
        return if (userId.isNotBlank()) "user_${userId}_streak_count" else KEY_STREAK_COUNT
    }

    private fun getLastStreakDateKey(): String {
        return if (userId.isNotBlank()) "user_${userId}_last_streak_date" else KEY_LAST_STREAK_DATE
    }

    /**
     * Formats current device date formatted as yyyy-MM-dd using device's current Calendar / Timezone.
     */
    fun getTodayDateKey(): String {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.timeZone = calendar.timeZone
        return sdf.format(calendar.time)
    }

    /**
     * Formats yesterday's date formatted as yyyy-MM-dd using device's current Calendar / Timezone.
     */
    private fun getYesterdayDateKey(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.timeZone = calendar.timeZone
        return sdf.format(calendar.time)
    }

    /**
     * Checks whether a task has already been collected/completed for today's calendar date for THIS specific user.
     */
    fun isTaskCollectedToday(taskId: String): Boolean {
        val today = getTodayDateKey()
        val lastCollectedDate = prefs.getString(getTaskKey(taskId), null)
        return lastCollectedDate == today
    }

    /**
     * Executes secure task claim through Supabase Edge Function with local fallback.
     */
    suspend fun claimTaskWithEdgeFunction(
        taskId: String,
        authToken: String? = null
    ): TaskClaimResult = withContext(Dispatchers.IO) {
        val today = getTodayDateKey()
        val clientTz = Calendar.getInstance().timeZone.id

        // Check if already claimed locally
        if (isTaskCollectedToday(taskId)) {
            return@withContext TaskClaimResult(
                success = false,
                alreadyClaimed = true,
                coinsReward = 0,
                newStreak = getCurrentStreak(),
                message = "Task already collected for today. Resets at midnight.",
                isServerVerified = false
            )
        }

        // Try calling the Supabase Edge Function if available
        try {
            val authHeader = if (!authToken.isNullOrBlank()) "Bearer $authToken" else "Bearer ${SupabaseConfig.ANON_KEY}"
            val response = SupabaseClient.api.claimTask(
                authorization = authHeader,
                body = SupabaseClaimTaskRequest(
                    taskId = taskId,
                    clientTimezone = clientTz
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val serverBody: SupabaseClaimTaskResponse = response.body()!!
                if (serverBody.success) {
                    val serverDate = serverBody.serverDate ?: today
                    prefs.edit().putString(getTaskKey(taskId), serverDate).apply()
                    if (taskId == TASK_DAILY_CHECKIN) {
                        prefs.edit()
                            .putInt(getStreakCountKey(), serverBody.newStreak)
                            .putString(getLastStreakDateKey(), serverDate)
                            .apply()
                    }
                    return@withContext TaskClaimResult(
                        success = true,
                        alreadyClaimed = false,
                        coinsReward = serverBody.coinsReward,
                        newStreak = serverBody.newStreak,
                        message = serverBody.message ?: "Claimed successfully",
                        isServerVerified = true
                    )
                } else if (serverBody.alreadyClaimed) {
                    prefs.edit().putString(getTaskKey(taskId), today).apply()
                    return@withContext TaskClaimResult(
                        success = false,
                        alreadyClaimed = true,
                        coinsReward = 0,
                        newStreak = getCurrentStreak(),
                        message = serverBody.message ?: "Task already claimed for today.",
                        isServerVerified = true
                    )
                }
            }
        } catch (e: Exception) {
            Log.w("TaskManager", "Edge function call unreachable, utilizing client calendar: ${e.message}")
        }

        // Secure fallback via device calendar
        val claimed = claimTaskToday(taskId)
        if (claimed) {
            val streak = getCurrentStreak()
            val dayCoins = listOf(10, 10, 10, 15, 20, 25, 30)
            val reward = when (taskId) {
                TASK_DAILY_CHECKIN -> dayCoins.getOrElse(streak - 1) { 20 }
                TASK_IDENTITY_CHECK -> 50
                TASK_FIRST_RECHARGE -> 100
                TASK_PROFILE_BIO -> 15
                else -> 20
            }
            TaskClaimResult(
                success = true,
                alreadyClaimed = false,
                coinsReward = reward,
                newStreak = streak,
                message = "🎉 Claimed +$reward Free Coins successfully!",
                isServerVerified = false
            )
        } else {
            TaskClaimResult(
                success = false,
                alreadyClaimed = true,
                coinsReward = 0,
                newStreak = getCurrentStreak(),
                message = "Already collected today! Resets at midnight.",
                isServerVerified = false
            )
        }
    }

    /**
     * Records task completion for today locally for this account.
     */
    fun claimTaskToday(taskId: String): Boolean {
        val today = getTodayDateKey()
        val lastCollectedDate = prefs.getString(getTaskKey(taskId), null)
        if (lastCollectedDate == today) {
            return false // Already claimed today
        }

        // Save new claim date
        prefs.edit().putString(getTaskKey(taskId), today).apply()

        // If daily check-in, update streak
        if (taskId == TASK_DAILY_CHECKIN) {
            updateStreakOnClaim(today)
        }

        return true
    }

    /**
     * Returns current consecutive streak days (1..7).
     */
    fun getCurrentStreak(): Int {
        val today = getTodayDateKey()
        val yesterday = getYesterdayDateKey()
        val lastDate = prefs.getString(getLastStreakDateKey(), null)
        val streak = prefs.getInt(getStreakCountKey(), 1)

        return when (lastDate) {
            today -> streak
            yesterday -> streak // Still active from yesterday, ready for today
            else -> 1 // Streak broken or new user
        }
    }

    private fun updateStreakOnClaim(today: String) {
        val yesterday = getYesterdayDateKey()
        val lastDate = prefs.getString(getLastStreakDateKey(), null)
        var currentStreak = prefs.getInt(getStreakCountKey(), 0)

        currentStreak = if (lastDate == yesterday) {
            (currentStreak % 7) + 1
        } else {
            1
        }

        prefs.edit()
            .putInt(getStreakCountKey(), currentStreak)
            .putString(getLastStreakDateKey(), today)
            .apply()
    }

    /**
     * Gets user-friendly current timezone and date string
     */
    fun getTimezoneInfo(): String {
        val tz = Calendar.getInstance().timeZone
        val displayName = tz.getDisplayName(false, TimeZone.SHORT, Locale.getDefault())
        val id = tz.id
        return "Timezone: $displayName ($id) • Server & Calendar Resets at 00:00"
    }
}
