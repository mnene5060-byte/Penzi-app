package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.DatingIntent
import com.example.model.DatingProfile
import com.example.ui.theme.KenyanSunsetOrange
import com.example.ui.theme.MaasaiBeadBlue
import com.example.ui.theme.SafariTerracotta
import com.example.ui.theme.SavannahGold
import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziFullScreenGradient

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreScreen(
    selectedIntent: DatingIntent?,
    profiles: List<DatingProfile>,
    likedProfileIds: Set<String> = emptySet(),
    onSelectIntent: (DatingIntent?) -> Unit,
    onProfileClick: (DatingProfile) -> Unit,
    onLikeProfile: (DatingProfile) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // -------------------------------------------------------------
            // SEAMLESS TOP HEADER WITH STATUS BAR
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PenziHeaderGradient)
                    .statusBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedIntent != null) {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .clickable { onSelectIntent(null) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (selectedIntent == null) "Explore Vibes" else "Vibe: ${selectedIntent.name}",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = selectedIntent?.emoji ?: "✨",
                                        fontSize = 18.sp
                                    )
                                }
                                Text(
                                    text = if (selectedIntent == null) {
                                        "Select a dating vibe to meet online singles"
                                    } else {
                                        selectedIntent.tagline
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Dynamic Live Count of online users in this view
                        val onlineCount = if (selectedIntent == null) {
                            profiles.count { it.isOnline }
                        } else {
                            profiles.count { it.intent == selectedIntent && it.isOnline }
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.22f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = "🟢 ", fontSize = 10.sp)
                                Text(
                                    text = "$onlineCount Online",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SavannahGold
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // MAIN VIEW CONTAINER (Categories Dashboard OR Online User List)
            // -------------------------------------------------------------
            if (selectedIntent == null) {
                // Display Categories List/Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(SavannahGold.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👋", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Niaje! Pick a Dating Vibe",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                    Text(
                                        text = "Only online singles in each vibe are displayed",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }
                        }
                    }

                    items(DatingIntent.values()) { intent ->
                        // Dynamically calculate online singles count for this category
                        val onlineCountForVibe = profiles.count { it.intent == intent && it.isOnline }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectIntent(intent) }
                                .testTag("category_card_${intent.name.lowercase()}"),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(intent.badgeColorHex).copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = intent.emoji,
                                                fontSize = 28.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = intent.label.substringBefore("💍").substringBefore("✨").substringBefore("☕").substringBefore("🏕️").substringBefore("🥂").trim(),
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF111827)
                                            )
                                            Text(
                                                text = intent.tagline,
                                                fontSize = 12.sp,
                                                color = Color(0xFF6B7280),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.Explore,
                                        contentDescription = null,
                                        tint = Color(intent.badgeColorHex).copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (onlineCountForVibe > 0) Color(0xFFDCFCE7) else Color(0xFFF3F4F6),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(if (onlineCountForVibe > 0) Color(0xFF22C55E) else Color(0xFF9CA3AF))
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "$onlineCountForVibe Online Now",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (onlineCountForVibe > 0) Color(0xFF15803D) else Color(0xFF4B5563)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Explore Vibe ➔",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(intent.badgeColorHex)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Display Online Users belonging to this category
                val onlineProfilesInCategory = profiles.filter { it.isOnline }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (onlineProfilesInCategory.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp, horizontal = 12.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 36.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        modifier = Modifier.size(64.dp),
                                        shape = CircleShape,
                                        color = Color(0xFFFEF2F2)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = "📴", fontSize = 32.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No one is online right now",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF111827),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "There are currently no active users online in this category. Try another vibe or check back soon!",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B7280),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(
                                        onClick = { onSelectIntent(null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(selectedIntent.badgeColorHex)),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("Back to Vibes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        items(onlineProfilesInCategory, key = { it.id }) { profile ->
                            ExploreProfileCard(
                                profile = profile,
                                onClick = { onProfileClick(profile) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreProfileCard(
    profile: DatingProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(235.dp)
            .clickable { onClick() }
            .testTag("explore_card_${profile.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = profile.imageUrls.firstOrNull() ?: "",
                contentDescription = profile.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Smooth Dark Bottom Gradient for readability over photos
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Intent Badge Top Left
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(profile.intent.badgeColorHex).copy(alpha = 0.95f),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "${profile.intent.emoji} ${profile.intent.label}",
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Bottom Profile Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${profile.name}, ${profile.age}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (profile.verified) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = MaasaiBeadBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                val locationParts = listOf(profile.neighborhood, profile.city).filter { it.isNotBlank() }
                if (locationParts.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 1.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = SavannahGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = locationParts.joinToString(", "),
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (profile.occupation.isNotBlank()) {
                    Text(
                        text = profile.occupation,
                        color = SavannahGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Realtime Online Activity Indicator: Green dot without border
            if (profile.isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )
            }
        }
    }
}
