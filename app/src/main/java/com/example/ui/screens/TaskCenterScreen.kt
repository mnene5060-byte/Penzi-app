package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskManager
import com.example.ui.theme.KenyanSunsetOrange
import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziFullScreenGradient
import kotlinx.coroutines.launch

@Composable
fun TaskCenterScreen(
    onBack: () -> Unit,
    userId: String = "",
    authToken: String? = null,
    onCoinsEarned: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val taskManager = remember(userId) { TaskManager(context, userId) }

    var isClaimingCheckin by remember { mutableStateOf(false) }
    var isDailyCheckinClaimed by remember { mutableStateOf(taskManager.isTaskCollectedToday(TaskManager.TASK_DAILY_CHECKIN)) }
    var currentStreak by remember { mutableStateOf(taskManager.getCurrentStreak()) }

    var isIdentityClaimed by remember { mutableStateOf(taskManager.isTaskCollectedToday(TaskManager.TASK_IDENTITY_CHECK)) }
    var isFirstRechargeClaimed by remember { mutableStateOf(taskManager.isTaskCollectedToday(TaskManager.TASK_FIRST_RECHARGE)) }
    var isProfileBioClaimed by remember { mutableStateOf(taskManager.isTaskCollectedToday(TaskManager.TASK_PROFILE_BIO)) }

    var showInfoDialog by remember { mutableStateOf(false) }

    // Coin reward amounts for 7 days
    val dayCoins = listOf(10, 10, 10, 15, 20, 25, 30)
    val todayReward = dayCoins.getOrElse((currentStreak - 1).coerceIn(0, 6)) { 20 }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PenziFullScreenGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // -------------------------------------------------------------
            // SEAMLESS TOP APP BAR WITH STATUS BAR PADDING
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("task_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Task Center",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Task Rules",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------------------------------------
            // HERO CARD: DAILY CHECK-IN
            // -------------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0284C7), // Sky Blue
                                    Color(0xFF2563EB), // Deep Royal Blue
                                    Color(0xFF4F46E5)  // Indigo
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Header info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Daily check-in",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Collect free coins daily",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            // Streak Badge
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("🔥", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$currentStreak Day Streak",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // INNER WHITE CONTAINER WITH 7-DAY STREAK GRID
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                // Row 1: Day 1, Day 2, Day 3, Day 4
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    (1..4).forEach { dayNum ->
                                        val isCollected = dayNum < currentStreak || (dayNum == currentStreak && isDailyCheckinClaimed)
                                        val isTodayActive = dayNum == currentStreak && !isDailyCheckinClaimed
                                        
                                        DayStreakItem(
                                            dayText = "${dayNum}day",
                                            coinsText = "${dayCoins[dayNum - 1]}",
                                            isCollected = isCollected,
                                            isTodayActive = isTodayActive,
                                            isSpecial = false,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (dayNum < 4) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Row 2: Day 5, Day 6, Day 7
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    (5..7).forEach { dayNum ->
                                        val isSpecial = dayNum == 7
                                        val isCollected = dayNum < currentStreak || (dayNum == currentStreak && isDailyCheckinClaimed)
                                        val isTodayActive = dayNum == currentStreak && !isDailyCheckinClaimed

                                        DayStreakItem(
                                            dayText = "${dayNum}day",
                                            coinsText = "${dayCoins[dayNum - 1]}",
                                            isCollected = isCollected,
                                            isTodayActive = isTodayActive,
                                            isSpecial = isSpecial,
                                            modifier = Modifier.weight(if (isSpecial) 1.6f else 1f)
                                        )
                                        if (dayNum < 7) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // CLAIM BUTTON
                                Button(
                                    onClick = {
                                        if (!isDailyCheckinClaimed) {
                                            scope.launch {
                                                isClaimingCheckin = true
                                                val result = taskManager.claimTaskWithEdgeFunction(
                                                    taskId = TaskManager.TASK_DAILY_CHECKIN,
                                                    authToken = authToken
                                                )
                                                if (result.success) {
                                                    isDailyCheckinClaimed = true
                                                    currentStreak = result.newStreak
                                                    onCoinsEarned(result.coinsReward)
                                                    val prefix = if (result.isServerVerified) "🔒 [Edge Verified] " else ""
                                                    Toast.makeText(
                                                        context,
                                                        "$prefix${result.message}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    if (result.alreadyClaimed) {
                                                        isDailyCheckinClaimed = true
                                                    }
                                                    Toast.makeText(
                                                        context,
                                                        result.message,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                isClaimingCheckin = false
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Collected today! Available again tomorrow.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    enabled = !isDailyCheckinClaimed && !isClaimingCheckin,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("claim_daily_button"),
                                    shape = RoundedCornerShape(25.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDailyCheckinClaimed) Color(0xFFF1F5F9) else Color(0xFFFFB703),
                                        contentColor = if (isDailyCheckinClaimed) Color(0xFF64748B) else Color(0xFF1E293B),
                                        disabledContainerColor = Color(0xFFF1F5F9),
                                        disabledContentColor = Color(0xFF64748B)
                                    )
                                ) {
                                    if (isClaimingCheckin) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = Color(0xFF1E293B),
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            if (isDailyCheckinClaimed) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color(0xFF10B981),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Collected",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF64748B)
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.MonetizationOn,
                                                    contentDescription = null,
                                                    tint = Color(0xFF78350F),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Claim +$todayReward Free Coins",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF78350F)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // -------------------------------------------------------------
            // MISSION CENTER SECTION
            // -------------------------------------------------------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAILY MISSIONS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.6.sp
                    )

                    Text(
                        text = "Resets Daily",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Card for missions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Mission 1: Identity Check
                        MissionRowItem(
                            icon = Icons.Default.Shield,
                            iconBg = Color(0xFFFFF7ED),
                            iconTint = KenyanSunsetOrange,
                            title = "Identity Check",
                            subtitle = "Verify your face with AI",
                            rewardCoins = 50,
                            isCollected = isIdentityClaimed,
                            onClick = {
                                if (!isIdentityClaimed) {
                                    scope.launch {
                                        val result = taskManager.claimTaskWithEdgeFunction(
                                            taskId = TaskManager.TASK_IDENTITY_CHECK,
                                            authToken = authToken
                                        )
                                        if (result.success) {
                                            isIdentityClaimed = true
                                            onCoinsEarned(result.coinsReward)
                                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                        } else {
                                            if (result.alreadyClaimed) isIdentityClaimed = true
                                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Collected today! Available again tomorrow.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.8.dp,
                            color = Color(0xFFF1F5F9)
                        )

                        // Mission 2: First Recharge
                        MissionRowItem(
                            icon = Icons.Default.MonetizationOn,
                            iconBg = Color(0xFFFEF3C7),
                            iconTint = Color(0xFFD97706),
                            title = "Daily Recharge",
                            subtitle = "Recharge or verify today's coin pack",
                            rewardCoins = 100,
                            isCollected = isFirstRechargeClaimed,
                            onClick = {
                                if (!isFirstRechargeClaimed) {
                                    scope.launch {
                                        val result = taskManager.claimTaskWithEdgeFunction(
                                            taskId = TaskManager.TASK_FIRST_RECHARGE,
                                            authToken = authToken
                                        )
                                        if (result.success) {
                                            isFirstRechargeClaimed = true
                                            onCoinsEarned(result.coinsReward)
                                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                        } else {
                                            if (result.alreadyClaimed) isFirstRechargeClaimed = true
                                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Collected today! Available again tomorrow.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.8.dp,
                            color = Color(0xFFF1F5F9)
                        )

                        // Mission 3: Profile Bio
                        MissionRowItem(
                            icon = Icons.Default.Star,
                            iconBg = Color(0xFFEEF2FF),
                            iconTint = Color(0xFF4F46E5),
                            title = "Complete Profile Bio",
                            subtitle = "Update or review your bio details",
                            rewardCoins = 15,
                            isCollected = isProfileBioClaimed,
                            onClick = {
                                if (!isProfileBioClaimed) {
                                    scope.launch {
                                        val result = taskManager.claimTaskWithEdgeFunction(
                                            taskId = TaskManager.TASK_PROFILE_BIO,
                                            authToken = authToken
                                        )
                                        if (result.success) {
                                            isProfileBioClaimed = true
                                            onCoinsEarned(result.coinsReward)
                                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                        } else {
                                            if (result.alreadyClaimed) isProfileBioClaimed = true
                                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Collected today! Available again tomorrow.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    "Task Center Rules",
                    fontWeight = FontWeight.Bold,
                    color = KenyanSunsetOrange
                )
            },
            text = {
                Text(
                    "• Each reward can ONLY be collected once per calendar day.\n• Once collected, tasks show 'Collected' and reset at 00:00 midnight.\n• Reset timing is accurately synchronized with your phone's calendar timezone (${taskManager.getTimezoneInfo()}).",
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Got it", color = KenyanSunsetOrange, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

/**
 * 7-Day Streak Item:
 * ONLY ticks the checkmark for days that are ALREADY COLLECTED!
 */
@Composable
private fun DayStreakItem(
    dayText: String,
    coinsText: String,
    isCollected: Boolean,
    isTodayActive: Boolean,
    isSpecial: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isTodayActive) 1.06f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = modifier
            .height(98.dp)
            .clip(RoundedCornerShape(16.dp)),
        color = when {
            isCollected -> Color(0xFFF1F5F9)
            isTodayActive -> Color(0xFFEFF6FF)
            isSpecial -> Color(0xFFFEF3C7)
            else -> Color(0xFFF8FAFC)
        },
        border = when {
            isTodayActive -> androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF0284C7))
            isSpecial -> androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
            else -> androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dayText,
                fontSize = 11.sp,
                fontWeight = if (isTodayActive) FontWeight.Bold else FontWeight.SemiBold,
                color = when {
                    isCollected -> Color(0xFF94A3B8)
                    isTodayActive -> Color(0xFF0284C7)
                    else -> Color(0xFF64748B)
                }
            )

            // Center Icon
            when {
                // 1. ONLY SHOW TICK IF COLLECTED!
                isCollected -> {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Collected",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 2. ACTIVE TODAY (Waiting to be claimed - NOT ticked)
                isTodayActive -> {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color(0xFFFFB703)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Claim Today",
                            tint = Color(0xFF78350F),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 3. SPECIAL DAY 7 (Treasure / Double coins - NOT ticked)
                isSpecial -> {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Special Reward",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 4. FUTURE / LOCKED (NOT ticked)
                else -> {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Upcoming",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Bottom Coin Pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = when {
                    isCollected -> Color(0xFFE2E8F0)
                    isTodayActive -> Color(0xFF0284C7)
                    isSpecial -> Color(0xFFD97706)
                    else -> Color(0xFFE2E8F0)
                }
            ) {
                Text(
                    text = if (isCollected) "✓" else "+$coinsText",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isCollected -> Color(0xFF64748B)
                        isTodayActive || isSpecial -> Color.White
                        else -> Color(0xFF475569)
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun MissionRowItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    rewardCoins: Int,
    isCollected: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isCollected) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isCollected) Color(0xFFF1F5F9) else iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isCollected) Color(0xFF94A3B8) else iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCollected) Color(0xFF94A3B8) else Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = if (isCollected) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isCollected) Color(0xFFF1F5F9) else KenyanSunsetOrange.copy(alpha = 0.12f),
            border = if (isCollected) null else androidx.compose.foundation.BorderStroke(1.dp, KenyanSunsetOrange.copy(alpha = 0.25f))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                if (isCollected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Collected",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                } else {
                    Text(
                        text = "+$rewardCoins COINS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = KenyanSunsetOrange
                    )
                }
            }
        }
    }
}
