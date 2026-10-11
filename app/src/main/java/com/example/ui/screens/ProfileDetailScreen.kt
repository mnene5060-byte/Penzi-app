package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.People
import com.example.model.RelationType
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.DatingProfile
import com.example.model.Gender
import com.example.ui.theme.KenyanSunsetOrange
import com.example.ui.theme.MaasaiBeadBlue
import com.example.ui.theme.SafariTerracotta
import com.example.ui.theme.SavannahGold
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileDetailScreen(
    profile: DatingProfile,
    onBack: () -> Unit,
    onChat: () -> Unit,
    isFollowed: Boolean = false,
    onFollow: () -> Unit = {},
    onOpenRelation: ((RelationType) -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showOptionsMenu by remember { mutableStateOf(false) }
    var isFullScreenViewerOpen by remember { mutableStateOf(false) }
    var fullScreenViewerPageIndex by remember { mutableStateOf(0) }

    // Dynamic base counts based on profile ID hash for deterministic realistic counts
    val baseFollowing = remember(profile.id) { 10 + (Math.abs(profile.id.hashCode()) % 25) }
    val baseFollowers = remember(profile.id) { 15 + (Math.abs(profile.id.hashCode()) % 40) }
    val baseFriends = remember(profile.id) { 4 + (Math.abs(profile.id.hashCode()) % 12) }

    val currentFollowersCount = baseFollowers + (if (isFollowed) 1 else 0)
    val currentFriendsCount = baseFriends + (if (isFollowed) 1 else 0)

    val images = remember(profile.imageUrls) {
        if (profile.imageUrls.isNotEmpty()) profile.imageUrls
        else listOf("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600&auto=format&fit=crop&q=80")
    }

    val pagerState = rememberPagerState(pageCount = { images.size })
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Calculate scroll progress for the sticky top bar
    val scrollProgress = (scrollState.value.toFloat() / 300f).coerceIn(0f, 1f)

    val userDisplayId = remember(profile.id) {
        val hash = kotlin.math.abs(profile.id.hashCode())
        "59" + hash.toString().take(7).padStart(7, '0')
    }

    // Intercept Back Press: If full screen image viewer is open, back takes user back to profile details
    BackHandler(enabled = isFullScreenViewerOpen) {
        isFullScreenViewerOpen = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
    ) {
        // -------------------------------------------------------------
        // SCROLLABLE CONTENT (SCROLLS UNDER THE STICKY TOP BAR)
        // -------------------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
        ) {
            // -------------------------------------------------------------
            // HERO IMAGE CAROUSEL / PAGER
            // -------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = images[page],
                        contentDescription = "${profile.name} photo $page",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                fullScreenViewerPageIndex = page
                                isFullScreenViewerOpen = true
                            },
                        contentScale = ContentScale.Crop
                    )
                }

                // Cinematic Multi-Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.90f)
                                )
                            )
                        )
                )

                // Top Story-style Step Indicators (positioned cleanly below top bar)
                if (images.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(top = 64.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(images.size) { index ->
                            val isActive = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.5.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isActive) Color.White else Color.White.copy(alpha = 0.35f)
                                    )
                                    .clickable {
                                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                    }
                            )
                        }
                    }
                }

                // Profile Name, Verified & Status Badges Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${profile.name}, ${profile.age}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                        if (profile.verified) {
                            Surface(
                                shape = CircleShape,
                                color = MaasaiBeadBlue
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Profile",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .padding(2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Vibrant Pills Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gender & Age Pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (profile.gender == Gender.FEMALE) Color(0xFFFF2D55) else Color(0xFF007AFF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (profile.gender == Gender.FEMALE) "♀" else "♂",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "${profile.age} yrs",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Intent Pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(profile.intent.badgeColorHex)
                        ) {
                            Text(
                                text = "${profile.intent.emoji} ${profile.intent.label}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Online Status Pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF10B981)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Text(
                                    text = "Online",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // -------------------------------------------------------------
            // PENZI ID, FOLLOW & STATS CARD
            // -------------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ID badge with copy action
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF3F4F6),
                            modifier = Modifier.clickable {
                                clipboardManager.setText(AnnotatedString(userDisplayId))
                                Toast.makeText(context, "Penzi ID $userDisplayId copied!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Penzi ID: $userDisplayId",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF374151)
                                )
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy ID",
                                    tint = Color(0xFF6B7280),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        // Follow Button with gradient or following state
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isFollowed) Brush.linearGradient(listOf(Color(0xFFF3F4F6), Color(0xFFE5E7EB)))
                                    else Brush.horizontalGradient(listOf(Color(0xFFEA580C), Color(0xFFB91C1C)))
                                )
                                .clickable {
                                    onFollow()
                                    Toast.makeText(
                                        context,
                                        if (!isFollowed) "You are now following ${profile.name}!" else "Unfollowed ${profile.name}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFollowed) Icons.Default.Check else Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = if (isFollowed) Color(0xFF059669) else Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = if (isFollowed) "Following" else "+ Follow",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isFollowed) Color(0xFF374151) else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // STATS ROW (Following / Followers / Friends)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileStatItem(
                            count = baseFollowing.toString(),
                            label = "Following",
                            onClick = { onOpenRelation?.invoke(RelationType.FOLLOWING) }
                        )

                        Divider(
                            modifier = Modifier
                                .height(28.dp)
                                .width(1.dp),
                            color = Color(0xFFE5E7EB)
                        )

                        ProfileStatItem(
                            count = currentFollowersCount.toString(),
                            label = "Followers",
                            onClick = { onOpenRelation?.invoke(RelationType.FOLLOWERS) }
                        )

                        Divider(
                            modifier = Modifier
                                .height(28.dp)
                                .width(1.dp),
                            color = Color(0xFFE5E7EB)
                        )

                        ProfileStatItem(
                            count = currentFriendsCount.toString(),
                            label = "Friends",
                            onClick = { onOpenRelation?.invoke(RelationType.FRIENDS) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // -------------------------------------------------------------
            // ABOUT ME / BIO CARD
            // -------------------------------------------------------------
            if (profile.bio.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFF3E0),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = "About Me",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = profile.bio,
                            fontSize = 14.sp,
                            color = Color(0xFF374151),
                            lineHeight = 21.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // -------------------------------------------------------------
            // NAIROBI SHENG ICEBREAKER CARD
            // -------------------------------------------------------------
            if (profile.shengIcebreaker.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🇰🇪", fontSize = 16.sp)
                            Text(
                                text = "Sheng Icebreaker",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "\"${profile.shengIcebreaker}\"",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF78350F),
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // -------------------------------------------------------------
            // INTERESTS & VIBES
            // -------------------------------------------------------------
            if (profile.interests.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Interests & Passions",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            profile.interests.forEach { interest ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFF3F4F6),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                                ) {
                                    Text(
                                        text = interest,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF374151),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // -------------------------------------------------------------
            // PERSONAL DETAILS CARD
            // -------------------------------------------------------------
            val hasLocation = profile.city.isNotBlank() || profile.neighborhood.isNotBlank()
            val hasOccupation = profile.occupation.isNotBlank()
            val hasHeight = profile.heightCm > 0
            val hasIntent = profile.intent != null

            if (hasLocation || hasOccupation || hasHeight || hasIntent) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Personal Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )

                        if (hasLocation) {
                            val locationString = listOfNotNull(
                                profile.neighborhood.takeIf { it.isNotBlank() },
                                profile.city.takeIf { it.isNotBlank() }
                            ).joinToString(", ")
                            DetailItemRow(
                                icon = Icons.Default.LocationOn,
                                iconTint = Color(0xFFEF4444),
                                label = "Location",
                                value = locationString
                            )
                        }

                        if (hasOccupation) {
                            DetailItemRow(
                                icon = Icons.Default.Work,
                                iconTint = Color(0xFF3B82F6),
                                label = "Occupation",
                                value = profile.occupation
                            )
                        }

                        if (hasHeight) {
                            DetailItemRow(
                                icon = Icons.Default.Height,
                                iconTint = Color(0xFF10B981),
                                label = "Height",
                                value = "${profile.heightCm} cm"
                            )
                        }

                        if (hasIntent) {
                            DetailItemRow(
                                icon = Icons.Default.Shield,
                                iconTint = Color(0xFF8B5CF6),
                                label = "Dating Intent",
                                value = "${profile.intent.emoji} ${profile.intent.tagline}"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // -------------------------------------------------------------
            // TRUST & SAFETY BADGE (Conditional on profile.verified)
            // -------------------------------------------------------------
            if (profile.verified) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFECFDF5),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Verified Kenyan Profile",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                            Text(
                                text = "Photos and identity verified to prevent catfishing & scammers.",
                                fontSize = 11.sp,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFFBEB),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Unverified Profile",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = "This member has not yet completed Penzi selfie identity verification.",
                                fontSize = 11.sp,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // STICKY FLOATING APP BAR (BACK, TITLE ON SCROLL & 3 DOTS MENU)
        // -------------------------------------------------------------
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            color = Color.White.copy(alpha = scrollProgress),
            shadowElevation = if (scrollProgress > 0.85f) 4.dp else 0.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Back Button (Translucent dark on hero, sleek pill on scrolled white header)
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { onBack() }
                        .testTag("profile_detail_back_button"),
                    color = if (scrollProgress > 0.6f) Color(0xFFF3F4F6) else Color.Black.copy(alpha = 0.45f),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (scrollProgress > 0.6f) Color(0xFF111827) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Profile Name & Verified Badge (Smoothly fades in when scrolled past hero photo)
                AnimatedVisibility(
                    visible = scrollProgress > 0.5f,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 54.dp)
                    ) {
                        Text(
                            text = "${profile.name}, ${profile.age}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (profile.verified) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = MaasaiBeadBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Top Right Action Buttons (Three Dots)
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Three Dots Menu (MoreVert) Button
                    Box {
                        Surface(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable { showOptionsMenu = true }
                                .testTag("profile_detail_more_button"),
                            color = if (scrollProgress > 0.6f) Color(0xFFF3F4F6) else Color.Black.copy(alpha = 0.45f),
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More Options",
                                    tint = if (scrollProgress > 0.6f) Color(0xFF111827) else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Dropdown Menu for Three Dots
                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Copy Profile ID", fontWeight = FontWeight.Medium) },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF4B5563)) },
                                onClick = {
                                    showOptionsMenu = false
                                    clipboardManager.setText(AnnotatedString(userDisplayId))
                                    Toast.makeText(context, "Copied ID: $userDisplayId", Toast.LENGTH_SHORT).show()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Report Profile", color = Color(0xFFDC2626), fontWeight = FontWeight.Medium) },
                                leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFDC2626)) },
                                onClick = {
                                    showOptionsMenu = false
                                    Toast.makeText(context, "Report submitted for ${profile.name}. Our safety team will review.", Toast.LENGTH_LONG).show()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Block User", color = Color(0xFFDC2626), fontWeight = FontWeight.Medium) },
                                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFDC2626)) },
                                onClick = {
                                    showOptionsMenu = false
                                    Toast.makeText(context, "Blocked ${profile.name}. You won't see them again.", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // BOTTOM ACTION BAR (Chat | Call | Video)
        // -------------------------------------------------------------
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call Button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                        .clickable {
                            Toast.makeText(context, "Calling ${profile.name} on Safaricom audio...", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Audio Call",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Video Call Button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
                            )
                        )
                        .clickable {
                            Toast.makeText(context, "Starting video call with ${profile.name}...", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Primary Say Hi / Chat Button (Large Radiant Gradient Pill)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(25.dp))
                        .clip(RoundedCornerShape(25.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF1F2937),
                                    Color(0xFF374151),
                                    Color(0xFF111827)
                                )
                            )
                        )
                        .clickable { onChat() }
                        .testTag("profile_detail_chat_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "💬", fontSize = 16.sp)
                        Text(
                            text = "Chat with ${profile.name.split(" ").firstOrNull() ?: profile.name}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.2.sp
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // FULL-SCREEN PHOTO GALLERY VIEWER OVERLAY
        // -------------------------------------------------------------
        if (isFullScreenViewerOpen) {
            val fsPagerState = rememberPagerState(
                initialPage = fullScreenViewerPageIndex,
                pageCount = { images.size }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable(enabled = false) {} // block clicks to content below
            ) {
                // Main Photo Swiper
                HorizontalPager(
                    state = fsPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = images[page],
                            contentDescription = "${profile.name} full screen photo $page",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Top Controls: Close Button and Page Counter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isFullScreenViewerOpen = false },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Full Screen Gallery",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "${fsPagerState.currentPage + 1} / ${images.size}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                shape = CircleShape,
                color = iconTint.copy(alpha = 0.12f),
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280)
            )
        }

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827),
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun ProfileStatItem(
    count: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = count,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF111827)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B7280)
        )
    }
}
