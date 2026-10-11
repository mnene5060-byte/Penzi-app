package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.DatingProfile
import com.example.model.RelationType
import com.example.ui.theme.MaasaiBeadBlue
import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziFullScreenGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRelationListScreen(
    initialTab: RelationType = RelationType.FOLLOWING,
    relationType: RelationType? = null,
    titleOwnerName: String = "My Connections",
    followingList: List<DatingProfile>,
    followersList: List<DatingProfile>,
    friendsList: List<DatingProfile>,
    isFollowing: (String) -> Boolean,
    onToggleFollow: (DatingProfile) -> Unit,
    onProfileClick: (DatingProfile) -> Unit,
    onOpenChat: (DatingProfile) -> Unit,
    onBack: () -> Unit,
    onExploreClick: (() -> Unit)? = null
) {
    val fixedTab = relationType ?: initialTab
    var currentTab by remember(fixedTab) { mutableStateOf(fixedTab) }
    var searchQuery by remember { mutableStateOf("") }

    val activeList = when (currentTab) {
        RelationType.FOLLOWING -> followingList
        RelationType.FOLLOWERS -> followersList
        RelationType.FRIENDS -> friendsList
    }

    val filteredList = remember(activeList, searchQuery) {
        if (searchQuery.isBlank()) activeList
        else {
            val q = searchQuery.trim().lowercase()
            activeList.filter {
                it.name.lowercase().contains(q) ||
                it.occupation.lowercase().contains(q) ||
                it.city.lowercase().contains(q) ||
                it.bio.lowercase().contains(q)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PenziFullScreenGradient),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("relation_screen_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = when (currentTab) {
                                RelationType.FOLLOWING -> "Following"
                                RelationType.FOLLOWERS -> "Followers"
                                RelationType.FRIENDS -> "Friends"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (titleOwnerName.isNotBlank()) "$titleOwnerName • ${activeList.size} ${if (activeList.size == 1) "member" else "members"}"
                                   else "${activeList.size} ${if (activeList.size == 1) "member" else "members"}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // If opened as a specific single screen without tabs, show a clean header pill banner
            if (relationType == null) {
                // -------------------------------------------------------------
                // SEGMENTED TAB SELECTOR (Following | Followers | Friends)
                // -------------------------------------------------------------
                Surface(
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RelationTabPill(
                            title = "Following",
                            count = followingList.size,
                            isSelected = currentTab == RelationType.FOLLOWING,
                            onClick = { currentTab = RelationType.FOLLOWING },
                            modifier = Modifier.weight(1f)
                        )
                        RelationTabPill(
                            title = "Followers",
                            count = followersList.size,
                            isSelected = currentTab == RelationType.FOLLOWERS,
                            onClick = { currentTab = RelationType.FOLLOWERS },
                            modifier = Modifier.weight(1f)
                        )
                        RelationTabPill(
                            title = "Friends",
                            count = friendsList.size,
                            isSelected = currentTab == RelationType.FRIENDS,
                            onClick = { currentTab = RelationType.FRIENDS },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // Single dedicated screen banner
                Surface(
                    color = Color.White,
                    shadowElevation = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = when (currentTab) {
                                    RelationType.FOLLOWING -> Color(0xFFEFF6FF)
                                    RelationType.FOLLOWERS -> Color(0xFFFDF2F8)
                                    RelationType.FRIENDS -> Color(0xFFECFDF5)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = when (currentTab) {
                                            RelationType.FOLLOWING -> Icons.Default.Favorite
                                            RelationType.FOLLOWERS -> Icons.Default.PersonAdd
                                            RelationType.FRIENDS -> Icons.Default.Group
                                        },
                                        contentDescription = null,
                                        tint = when (currentTab) {
                                            RelationType.FOLLOWING -> Color(0xFF2563EB)
                                            RelationType.FOLLOWERS -> Color(0xFFD97706)
                                            RelationType.FRIENDS -> Color(0xFF059669)
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = when (currentTab) {
                                    RelationType.FOLLOWING -> "Profiles you follow"
                                    RelationType.FOLLOWERS -> "Members who follow you"
                                    RelationType.FRIENDS -> "Mutual connections"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF3F4F6),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = "${activeList.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SEARCH BAR
            // -------------------------------------------------------------
            Surface(
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name, city, or bio...", fontSize = 13.sp, color = Color(0xFF9CA3AF)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        singleLine = true
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // USERS LIST / EMPTY STATE
            // -------------------------------------------------------------
            if (filteredList.isEmpty()) {
                RelationEmptyState(
                    tab = currentTab,
                    searchQuery = searchQuery,
                    onExploreClick = onExploreClick
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(filteredList, key = { it.id }) { userProfile ->
                        val userIsFollowed = isFollowing(userProfile.id)
                        val isMutual = friendsList.any { it.id == userProfile.id }

                        UserRelationItemCard(
                            profile = userProfile,
                            isFollowing = userIsFollowed,
                            isMutualFriend = isMutual,
                            currentTab = currentTab,
                            onProfileClick = { onProfileClick(userProfile) },
                            onToggleFollow = { onToggleFollow(userProfile) },
                            onOpenChat = { onOpenChat(userProfile) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RelationTabPill(
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF1E293B) else Color(0xFFF3F4F6),
        modifier = modifier.height(40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF4B5563)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
                shape = CircleShape,
                color = if (isSelected) Color(0xFFB91C1C) else Color(0xFFE5E7EB),
                modifier = Modifier.size(18.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$count",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF4B5563)
                    )
                }
            }
        }
    }
}

@Composable
private fun UserRelationItemCard(
    profile: DatingProfile,
    isFollowing: Boolean,
    isMutualFriend: Boolean,
    currentTab: RelationType,
    onProfileClick: () -> Unit,
    onToggleFollow: () -> Unit,
    onOpenChat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with online status
            Box(modifier = Modifier.size(54.dp)) {
                AsyncImage(
                    model = profile.imageUrls.firstOrNull() ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500",
                    contentDescription = profile.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(1.5.dp, Color(0xFFE5E7EB), CircleShape),
                    contentScale = ContentScale.Crop
                )
                if (profile.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${profile.name}, ${profile.age}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // CRITICAL: Verified badge ONLY if profile.verified is true!
                    if (profile.verified) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Member",
                            tint = MaasaiBeadBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (profile.occupation.isNotBlank()) "${profile.occupation} • ${profile.city}" else profile.city,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Intent pill badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(profile.intent.badgeColorHex).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${profile.intent.emoji} ${profile.intent.label}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(profile.intent.badgeColorHex),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Button Column
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // If mutual friend or on Friends tab, show direct chat button
                if (isMutualFriend || currentTab == RelationType.FRIENDS) {
                    IconButton(
                        onClick = onOpenChat,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Message",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Follow / Following / Unfollow Button
                if (isFollowing) {
                    OutlinedButton(
                        onClick = onToggleFollow,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFF9FAFB)
                        ),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "Following",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151)
                        )
                    }
                } else {
                    Button(
                        onClick = onToggleFollow,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB91C1C)
                        ),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Follow",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RelationEmptyState(
    tab: RelationType,
    searchQuery: String,
    onExploreClick: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = when (tab) {
                RelationType.FOLLOWING -> Color(0xFFEFF6FF)
                RelationType.FOLLOWERS -> Color(0xFFFFF7ED)
                RelationType.FRIENDS -> Color(0xFFECFDF5)
            },
            modifier = Modifier.size(76.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (tab) {
                        RelationType.FOLLOWING -> Icons.Default.Favorite
                        RelationType.FOLLOWERS -> Icons.Default.PersonAdd
                        RelationType.FRIENDS -> Icons.Default.Group
                    },
                    contentDescription = null,
                    tint = when (tab) {
                        RelationType.FOLLOWING -> Color(0xFF2563EB)
                        RelationType.FOLLOWERS -> Color(0xFFD97706)
                        RelationType.FRIENDS -> Color(0xFF059669)
                    },
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (searchQuery.isNotEmpty()) "No matches found for \"$searchQuery\""
            else when (tab) {
                RelationType.FOLLOWING -> "You are not following anyone yet"
                RelationType.FOLLOWERS -> "No followers yet"
                RelationType.FRIENDS -> "No mutual friends yet"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF1F2937)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (searchQuery.isNotEmpty()) "Try searching for a different name or location."
            else when (tab) {
                RelationType.FOLLOWING -> "Discover interesting Kenyan profiles and follow them to stay connected!"
                RelationType.FOLLOWERS -> "Complete your profile and upload quality photos to attract followers."
                RelationType.FRIENDS -> "When you follow someone and they follow you back, they will appear here as your Friends."
            },
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (onExploreClick != null && searchQuery.isEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onExploreClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text("Explore Profiles", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
