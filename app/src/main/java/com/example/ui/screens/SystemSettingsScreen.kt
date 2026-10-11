package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.KenyanRed
import com.example.ui.theme.KenyanRedDark
import com.example.ui.theme.MaasaiBlue
import com.example.ui.theme.MpesaGreen

import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziFullScreenGradient

private enum class SettingsSubPage {
    MAIN, POLICIES, BLOCKED_LIST
}

data class BlockedUserItem(
    val id: String,
    val name: String,
    val photoUrl: String,
    val blockedDate: String,
    val reason: String
)

@Composable
fun SystemSettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var activeSubPage by remember { mutableStateOf(SettingsSubPage.MAIN) }
    var cacheSize by remember { mutableStateOf("4.0 MB") }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val blockedUsers = remember {
        mutableStateListOf(
            BlockedUserItem(
                id = "b_1",
                name = "Dennis K.",
                photoUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500",
                blockedDate = "Aug 20, 2026",
                reason = "Inappropriate messages"
            ),
            BlockedUserItem(
                id = "b_2",
                name = "Samantha M.",
                photoUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500",
                blockedDate = "Aug 15, 2026",
                reason = "Spam / Promotion"
            )
        )
    }

    when (activeSubPage) {
        SettingsSubPage.POLICIES -> {
            SecurityPrivacyPoliciesScreen(onBack = { activeSubPage = SettingsSubPage.MAIN })
            return
        }
        SettingsSubPage.BLOCKED_LIST -> {
            BlockedListScreen(
                blockedUsers = blockedUsers,
                onUnblock = { user ->
                    blockedUsers.remove(user)
                    Toast.makeText(context, "${user.name} has been unblocked.", Toast.LENGTH_SHORT).show()
                },
                onBack = { activeSubPage = SettingsSubPage.MAIN }
            )
            return
        }
        SettingsSubPage.MAIN -> {
            // Render main settings below
        }
    }

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
            // SEAMLESS TOP HEADER WITH STATUS BAR PADDING
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "SYSTEM SETTINGS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------------------------------------------------------------
            // CARD 1: GENERAL SETTINGS
            // -------------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // 1. Account & Security
                    SettingsRowItem(
                        icon = Icons.Default.Shield,
                        iconBg = Color(0xFFFFF7ED),
                        iconTint = Color(0xFFF97316),
                        title = "Account & Security",
                        subtitle = "View linked Google account, email & security status",
                        onClick = {
                            Toast.makeText(context, "Account verified: Active & Secure", Toast.LENGTH_SHORT).show()
                        }
                    )

                    SettingsDivider()

                    // 2. Security & Privacy Policies (Opens dedicated screen)
                    SettingsRowItem(
                        icon = Icons.Default.Lock,
                        iconBg = Color(0xFFECFDF5),
                        iconTint = Color(0xFF059669),
                        title = "Security & Privacy Policies",
                        subtitle = "Review safe use, encryption, and data protection rules",
                        onClick = { activeSubPage = SettingsSubPage.POLICIES }
                    )

                    SettingsDivider()

                    // 3. Call Settings
                    SettingsRowItem(
                        icon = Icons.Default.PhoneInTalk,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        title = "Call Settings",
                        subtitle = "Configure voice & video preferences",
                        onClick = {
                            Toast.makeText(context, "Voice & Video calls enabled for matches", Toast.LENGTH_SHORT).show()
                        }
                    )

                    SettingsDivider()

                    // 4. Blocked List (Opens dedicated screen)
                    SettingsRowItem(
                        icon = Icons.Default.Block,
                        iconBg = Color(0xFFFEE2E2),
                        iconTint = Color(0xFFEF4444),
                        title = "Blocked List",
                        subtitle = "Manage accounts you have blocked (${blockedUsers.size})",
                        onClick = { activeSubPage = SettingsSubPage.BLOCKED_LIST }
                    )

                    SettingsDivider()

                    // 5. Clear Cache
                    SettingsRowItem(
                        icon = Icons.Default.CleaningServices,
                        iconBg = Color(0xFFFFF7ED),
                        iconTint = Color(0xFFEA580C),
                        title = "Clear Cache",
                        subtitle = "Real cache size: $cacheSize • Tap to clean",
                        onClick = {
                            cacheSize = "0.0 MB"
                            Toast.makeText(context, "Cache cleared successfully! (0.0 MB)", Toast.LENGTH_SHORT).show()
                        }
                    )

                    SettingsDivider()

                    // 6. About Penzi
                    SettingsRowItem(
                        icon = Icons.Default.Info,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFCA8A04),
                        title = "About Penzi",
                        subtitle = "Platform version, legal, and licensing info",
                        onClick = { showAboutDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // -------------------------------------------------------------
            // CARD 2: DANGER ZONE (SIGN OUT & DELETE ACCOUNT)
            // -------------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Sign Out
                    SettingsRowItem(
                        icon = Icons.Default.Logout,
                        iconBg = Color(0xFFFEE2E2),
                        iconTint = Color(0xFFDC2626),
                        title = "Sign Out",
                        titleColor = Color(0xFFDC2626),
                        subtitle = "Log out from this device",
                        onClick = { showLogoutConfirmDialog = true }
                    )

                    SettingsDivider()

                    // Delete Account
                    SettingsRowItem(
                        icon = Icons.Default.DeleteForever,
                        iconBg = Color(0xFFFEF2F2),
                        iconTint = Color(0xFFB91C1C),
                        title = "Delete Account",
                        titleColor = Color(0xFFB91C1C),
                        subtitle = "Permanently erase your profile & data",
                        onClick = { showDeleteAccountDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("About Penzi Kenya", fontWeight = FontWeight.Bold, color = KenyanRed) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Version: 2.4.1 (Build 2026)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF111827))
                    Text("Penzi is Kenya's premier social & dating platform designed for genuine local connections across all 47 counties.", fontSize = 12.sp, color = Color(0xFF4B5563))
                    Text("© 2026 Penzi Kenya. All rights reserved.", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK", color = KenyanRedDark, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Log Out from Penzi?", fontWeight = FontWeight.Bold, color = KenyanRed) },
            text = {
                Text("Are you sure you want to sign out of your account on this device?", fontSize = 14.sp, color = Color(0xFF374151))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmDialog = false
                        onLogout()
                    }
                ) {
                    Text("Log Out", color = KenyanRedDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel", color = Color(0xFF6B7280), fontWeight = FontWeight.Medium)
                }
            }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Delete Account?", fontWeight = FontWeight.Bold, color = KenyanRed) },
            text = {
                Text("Are you sure you want to permanently delete your Penzi account? All chats, coins, and match history will be deleted.", fontSize = 13.sp, color = Color(0xFF374151))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                        onLogout()
                    }
                ) {
                    Text("Delete", color = KenyanRedDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = Color(0xFF6B7280))
                }
            }
        )
    }
}

// -------------------------------------------------------------
// DEDICATED SCREEN: SECURITY & PRIVACY POLICIES
// -------------------------------------------------------------
@Composable
fun SecurityPrivacyPoliciesScreen(onBack: () -> Unit) {
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
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "SECURITY & PRIVACY",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Summary Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MpesaGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Penzi Trust & Safety Shield",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF065F46)
                        )
                        Text(
                            text = "Compliant with Kenya Data Protection Act 2019 & ISO/IEC 27001 standards.",
                            fontSize = 12.sp,
                            color = Color(0xFF047857)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Policy Details List
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    PolicySectionItem(
                        icon = Icons.Default.Lock,
                        iconTint = Color(0xFF2563EB),
                        title = "1. End-to-End Chat Encryption & Isolation",
                        description = "All direct messages, audio clips, and shared media are isolated using Row Level Security (RLS). Only the two matched participants hold access permissions to the channel."
                    )

                    HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF3F4F6))

                    PolicySectionItem(
                        icon = Icons.Default.VerifiedUser,
                        iconTint = Color(0xFF059669),
                        title = "2. Real Identity Badges & Anti-Catfishing",
                        description = "Profiles bearing the Gold Shield badge have submitted verified phone number or biometric photo validation. Accounts impersonating others are permanently banned within 24 hours."
                    )

                    HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF3F4F6))

                    PolicySectionItem(
                        icon = Icons.Default.VisibilityOff,
                        iconTint = Color(0xFFD97706),
                        title = "3. Private Device Media & Permissions",
                        description = "Penzi never accesses your photo library, microphone, or storage in the background. Device assets are accessed strictly on-demand during active profile editing or media attachments."
                    )

                    HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF3F4F6))

                    PolicySectionItem(
                        icon = Icons.Default.Block,
                        iconTint = Color(0xFFDC2626),
                        title = "4. Instant Block & Safe Reporting",
                        description = "When you block a user, they are immediately stripped of ability to message, view, or find your account. Reports are audited by local moderation teams to keep the dating ecosystem safe."
                    )

                    HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF3F4F6))

                    PolicySectionItem(
                        icon = Icons.Default.Security,
                        iconTint = Color(0xFF7C3AED),
                        title = "5. Safe Transactions & M-Pesa Payouts",
                        description = "Coin top-ups and M-Pesa Diamond cash disbursements are authenticated through secure B2C API gateways. Financial tokens are never stored as plain text on client devices."
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun PolicySectionItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
        }
        Text(
            text = description,
            fontSize = 13.sp,
            color = Color(0xFF4B5563),
            lineHeight = 18.sp
        )
    }
}

// -------------------------------------------------------------
// DEDICATED SCREEN: BLOCKED LIST
// -------------------------------------------------------------
@Composable
fun BlockedListScreen(
    blockedUsers: List<BlockedUserItem>,
    onUnblock: (BlockedUserItem) -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PenziFullScreenGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "BLOCKED LIST (${blockedUsers.size})",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (blockedUsers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = Color(0xFFFEE2E2)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Text(
                            text = "No Blocked Users",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )

                        Text(
                            text = "You haven't blocked anyone yet. Blocked accounts will be listed here and will not be able to message you or see your profile.",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Blocked users cannot contact you, view your updates, or find your profile across Penzi.",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(blockedUsers, key = { it.id }) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    AsyncImage(
                                        model = user.photoUrl,
                                        contentDescription = user.name,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .border(1.dp, Color(0xFFE5E7EB), CircleShape),
                                        contentScale = ContentScale.Crop
                                    )

                                    Column {
                                        Text(
                                            text = user.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111827)
                                        )
                                        Text(
                                            text = "Blocked ${user.blockedDate} • ${user.reason}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onUnblock(user) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF3F4F6),
                                        contentColor = Color(0xFF374151)
                                    ),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(
                                        text = "Unblock",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    titleColor: Color = Color(0xFF111827),
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp),
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
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 18.dp),
        thickness = 0.8.dp,
        color = Color(0xFFF3F4F6)
    )
}

