package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ChatConversation
import com.example.model.ChatMessage
import com.example.model.DatingProfile
import com.example.ui.components.InAppGalleryPickerSheet
import com.example.ui.theme.KenyanRed
import com.example.ui.theme.KenyanSunsetOrange
import com.example.ui.theme.MaasaiBeadBlue
import com.example.ui.theme.SafariTerracotta
import com.example.ui.theme.SavannahGold
import com.example.ui.theme.PenziHeaderGradient
import com.example.ui.theme.PenziFullScreenGradient

@Composable
fun ChatScreen(
    conversations: List<ChatConversation>,
    matchedProfiles: List<DatingProfile>,
    activePartnerId: String?,
    onOpenConversation: (String) -> Unit,
    onCloseConversation: () -> Unit,
    onSendMessage: (partnerId: String, text: String, imageUrl: String?) -> Unit,
    onRefreshConversations: () -> Unit = {}
) {
    if (activePartnerId != null) {
        val convo = conversations.find { it.matchProfile.id == activePartnerId }
        val partner = convo?.matchProfile ?: matchedProfiles.find { it.id == activePartnerId }
        if (partner != null) {
            ChatDetailView(
                partner = partner,
                messages = convo?.messages ?: emptyList(),
                onBack = onCloseConversation,
                onSendMessage = { text, imgUrl -> onSendMessage(partner.id, text, imgUrl) }
            )
        } else {
            ChatListView(
                conversations = conversations,
                matchedProfiles = matchedProfiles,
                onSelectConversation = onOpenConversation,
                onRefresh = onRefreshConversations
            )
        }
    } else {
        ChatListView(
            conversations = conversations,
            matchedProfiles = matchedProfiles,
            onSelectConversation = onOpenConversation,
            onRefresh = onRefreshConversations
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListView(
    conversations: List<ChatConversation>,
    matchedProfiles: List<DatingProfile>,
    onSelectConversation: (String) -> Unit,
    onRefresh: () -> Unit = {}
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // -------------------------------------------------------------
            // SEAMLESS TOP APP BAR / HEADER (Fixed / Static)
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
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Chats",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "💬",
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // PULL TO REFRESH UNDER HEADER (Conversation List)
            // -------------------------------------------------------------
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    onRefresh()
                    coroutineScope.launch {
                        delay(800)
                        isRefreshing = false
                    }
                },
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = rememberPullToRefreshState(),
                        isRefreshing = isRefreshing,
                        containerColor = Color.White,
                        color = SavannahGold,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    // ---------------------------------------------------------
                    // CONVERSATIONS
                    // ---------------------------------------------------------
                    if (conversations.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp),
                                shape = RoundedCornerShape(0.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = null,
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        modifier = Modifier.size(68.dp),
                                        shape = CircleShape,
                                        color = Color(0xFFFFF3E0)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = "💬", fontSize = 32.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No active conversations yet",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF111827)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Discover Kenyan singles on Home & Party, send likes or instant chats to start talking!",
                                        fontSize = 13.sp,
                                        color = Color(0xFF6B7280),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(conversations, key = { it.matchProfile.id }) { convo ->
                            ConversationItem(
                                conversation = convo,
                                onClick = { onSelectConversation(convo.matchProfile.id) }
                            )
                            Divider(
                                color = Color(0xFFF3F4F6),
                                thickness = 0.8.dp,
                                modifier = Modifier.padding(start = 82.dp, end = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchStoryItem(
    profile: DatingProfile,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .width(64.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            Color(0xFF1F2937),
                            Color(0xFF374151),
                            Color(0xFFBE123C),
                            Color(0xFF1F2937)
                        )
                    )
                )
                .padding(2.5.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(1.5.dp)
            ) {
                AsyncImage(
                    model = profile.imageUrls.firstOrNull() ?: "",
                    contentDescription = profile.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = profile.name.split(" ").firstOrNull() ?: profile.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E1E1E),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ConversationItem(
    conversation: ChatConversation,
    onClick: () -> Unit
) {
    val partner = conversation.matchProfile
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("conversation_${partner.id}"),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with Online indicator
            Box(
                modifier = Modifier.size(56.dp)
            ) {
                AsyncImage(
                    model = partner.imageUrls.firstOrNull() ?: "",
                    contentDescription = partner.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Green Online Indicator Dot (Accurate realtime indicator)
                if (partner.isOnline) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = partner.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF111827)
                        )
                        if (partner.verified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = MaasaiBeadBlue,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Text(
                        text = conversation.lastTimestamp,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier
                                .size(14.dp)
                                .padding(end = 2.dp)
                        )
                        Text(
                            text = conversation.lastMessage,
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatDetailView(
    partner: DatingProfile,
    messages: List<ChatMessage>,
    onBack: () -> Unit,
    onSendMessage: (String, String?) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as? android.app.Activity)?.window
        if (window != null) {
            DisposableEffect(Unit) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                val originalLightStatusBars = insetsController.isAppearanceLightStatusBars
                insetsController.isAppearanceLightStatusBars = true
                onDispose {
                    insetsController.isAppearanceLightStatusBars = originalLightStatusBars
                }
            }
        }
    }
    var inputText by remember { mutableStateOf("") }
    var showGallerySheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
    ) {
        // -------------------------------------------------------------
        // SEAMLESS TOP APP BAR / HEADER
        // -------------------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PenziHeaderGradient)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Partner Avatar with Online Green Dot
                Box(
                    modifier = Modifier.size(42.dp)
                ) {
                    AsyncImage(
                        model = partner.imageUrls.firstOrNull() ?: "",
                        contentDescription = partner.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    // Online Dot (Only shown if genuinely online)
                    if (partner.isOnline) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = partner.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (partner.verified) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = SavannahGold,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Text(
                        text = if (partner.isOnline) "Active now • ${partner.neighborhood}" else "${partner.neighborhood}, ${partner.city}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (partner.isOnline) Color(0xFF86EFAC) else Color.White.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Top Action Icons (Audio Call & Video Call)
                IconButton(
                    onClick = {
                        Toast.makeText(context, "Calling ${partner.name} on Safaricom audio...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(38.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        Toast.makeText(context, "Starting video call with ${partner.name}...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(38.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Video Call",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // CHAT STREAM AREA
        // -------------------------------------------------------------
        LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Match Card at the start of conversation
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Date Chip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE5E7EB),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "Today",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4B5563),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Match Profile Card
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth(0.92f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier.size(72.dp)) {
                                    AsyncImage(
                                        model = partner.imageUrls.firstOrNull() ?: "",
                                        contentDescription = partner.name,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(
                                                2.dp,
                                                Brush.sweepGradient(
                                                    listOf(Color(0xFF1F2937), Color(0xFF374151), Color(0xFFBE123C), Color(0xFF1F2937))
                                                ),
                                                CircleShape
                                            ),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "You matched with ${partner.name}!",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF111827)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(partner.intent.badgeColorHex).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "${partner.intent.emoji} ${partner.intent.label}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(partner.intent.badgeColorHex),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    if (partner.city.isNotBlank()) {
                                        Text(
                                            text = "• ${partner.city}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                }

                                if (partner.shengIcebreaker.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFFFFBEB),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "💬 \"${partner.shengIcebreaker}\"",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF92400E),
                                            modifier = Modifier.padding(10.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Quick Kenyan Icebreaker Tap Chips
                                Text(
                                    text = "Tap to break the ice:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF9CA3AF)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(
                                        "👋 Sasa! Uko aje?",
                                        "☕ Nairobi coffee date?",
                                        "🍕 Pizza & vibe check?",
                                        "🇰🇪 Sema maze!"
                                    ).forEach { prompt ->
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color(0xFFF3F4F6),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                            modifier = Modifier.clickable { onSendMessage(prompt, null) }
                                        ) {
                                            Text(
                                                text = prompt,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF374151),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Chat Messages
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg, partnerAvatar = partner.imageUrls.firstOrNull() ?: "")
                }
            }

            // -------------------------------------------------------------
            // BOTTOM BAR & ACTIONS DOCK
            // -------------------------------------------------------------
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime)),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Quick Action Pills Row (Photo, Call, Video, Gift)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Photo Pill -> Opens in-app Gallery Screen (NO popup)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFFE2E8F0)
                            ),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showGallerySheet = true }
                                .testTag("chat_photo_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChatPhoto3DIcon(size = 20.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Photo", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                            }
                        }

                        // Call Pill (Fresh Green)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF0FDF4),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFFBBF7D0)
                            ),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    Toast.makeText(context, "Calling ${partner.name}...", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChatCall3DIcon(size = 20.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                            }
                        }

                        // Video Pill (Royal Blue)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFFBFDBFE)
                            ),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    Toast.makeText(context, "Video calling ${partner.name}...", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChatVideo3DIcon(size = 20.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Video", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                            }
                        }

                        // Gift Pill (Golden Amber)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFFBEB),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFFFDE68A)
                            ),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSendMessage("🎁 Sent a virtual gift!", null) }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ChatGift3DIcon(size = 20.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gift", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            }
                        }
                    }

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFFF3F4F6),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "😊",
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .clickable {
                                            inputText += "❤️ "
                                        }
                                        .padding(end = 6.dp)
                                )

                                OutlinedTextField(
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    placeholder = {
                                        Text(
                                            "Message ${partner.name.split(" ").firstOrNull() ?: partner.name}...",
                                            color = Color(0xFF9CA3AF),
                                            fontSize = 14.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("chat_message_input"),
                                    maxLines = 3,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black
                                    )
                                )
                            }
                        }

                        // Send / Voice Record Button with 3D Liquid Glass styling
                        if (inputText.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .shadow(6.dp, CircleShape, spotColor = Color(0xFFDC2626))
                                    .clip(CircleShape)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color(0xFFEA580C),
                                                Color(0xFFDC2626),
                                                Color(0xFF991B1B)
                                            )
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.5.dp,
                                            Brush.linearGradient(
                                                listOf(Color.White, SavannahGold, Color.White.copy(alpha = 0.3f))
                                            )
                                        ),
                                        CircleShape
                                    )
                                    .clickable {
                                        onSendMessage(inputText, null)
                                        inputText = ""
                                    }
                                    .testTag("send_chat_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .shadow(6.dp, CircleShape, spotColor = SavannahGold)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color(0xFFFFD166),
                                                Color(0xFFF59E0B),
                                                Color(0xFFD97706)
                                            )
                                        )
                                    )
                                    .border(
                                        BorderStroke(
                                            1.5.dp,
                                            Brush.linearGradient(
                                                listOf(Color.White, Color(0xFFFFE3A3), Color.White.copy(alpha = 0.4f))
                                            )
                                        ),
                                        CircleShape
                                    )
                                    .clickable {
                                        Toast.makeText(context, "Hold to record audio note...", Toast.LENGTH_SHORT).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Note",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // In-App Gallery Screen (Full-Screen Overlay, NO POPUP)
        androidx.compose.animation.AnimatedVisibility(
            visible = showGallerySheet,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(initialOffsetY = { it }),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                InAppGalleryPickerSheet(
                    onDismiss = { showGallerySheet = false },
                    onPhotoSelected = { selectedUri ->
                        showGallerySheet = false
                        onSendMessage("", selectedUri)
                    }
                )
            }
        }
    }

@Composable
fun ChatBubble(
    message: ChatMessage,
    partnerAvatar: String = ""
) {
    val isMe = message.isFromMe

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMe && partnerAvatar.isNotBlank()) {
            AsyncImage(
                model = partnerAvatar,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .padding(bottom = 2.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        if (isMe) {
            // Outgoing Message
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = 18.dp,
                    bottomEnd = 4.dp
                ),
                color = Color(0xFF1F2937),
                modifier = Modifier
                    .widthIn(max = 290.dp)
                    .shadow(2.dp, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp))
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = if (message.imageUrl != null) 6.dp else 14.dp,
                        vertical = if (message.imageUrl != null) 6.dp else 9.dp
                    )
                ) {
                    if (message.imageUrl != null) {
                        AsyncImage(
                            model = message.imageUrl,
                            contentDescription = "Sent Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        if (message.text.isNotBlank() && message.text != "📷 Photo" && message.text != "📷 Sent a photo") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = message.text,
                                fontSize = 14.sp,
                                color = Color.White,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                    } else {
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = Color.White,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = message.timestamp,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Delivered",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        } else {
            // Incoming Message
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 18.dp
                ),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                modifier = Modifier
                    .widthIn(max = 290.dp)
                    .shadow(1.dp, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp))
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = if (message.imageUrl != null) 6.dp else 14.dp,
                        vertical = if (message.imageUrl != null) 6.dp else 9.dp
                    )
                ) {
                    if (message.imageUrl != null) {
                        AsyncImage(
                            model = message.imageUrl,
                            contentDescription = "Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        if (message.text.isNotBlank() && message.text != "📷 Photo" && message.text != "📷 Sent a photo") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = message.text,
                                fontSize = 14.sp,
                                color = Color(0xFF1F2937),
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                    } else {
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = Color(0xFF1F2937),
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = message.timestamp,
                        fontSize = 10.sp,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 4.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3D QUICK ACTION ICONS FOR CHAT
// -------------------------------------------------------------
@Composable
fun ChatPhoto3DIcon(modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 20.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // 3D Camera Body
        val bodyRect = androidx.compose.ui.geometry.RoundRect(
            left = w * 0.12f,
            top = h * 0.22f,
            right = w * 0.88f,
            bottom = h * 0.88f,
            cornerRadius = CornerRadius(5f, 5f)
        )
        drawPath(
            path = Path().apply { addRoundRect(bodyRect) },
            brush = Brush.verticalGradient(listOf(Color(0xFF64748B), Color(0xFF334155), Color(0xFF0F172A)))
        )

        // Top Flash Bump
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF94A3B8), Color(0xFF475569))),
            topLeft = Offset(w * 0.28f, h * 0.12f),
            size = Size(w * 0.26f, h * 0.14f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // 3D Camera Lens
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF075985)),
                center = Offset(w * 0.50f, h * 0.55f),
                radius = w * 0.24f
            ),
            radius = w * 0.24f,
            center = Offset(w * 0.50f, h * 0.55f)
        )
        // Specular Lens Reflection
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = 2.5f,
            center = Offset(w * 0.44f, h * 0.48f)
        )
    }
}

@Composable
fun ChatCall3DIcon(modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 20.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // 3D Telephone Handset
        val phonePath = Path().apply {
            moveTo(w * 0.25f, h * 0.20f)
            cubicTo(w * 0.40f, h * 0.16f, w * 0.60f, h * 0.16f, w * 0.75f, h * 0.20f)
            lineTo(w * 0.75f, h * 0.40f)
            lineTo(w * 0.60f, h * 0.42f)
            cubicTo(w * 0.56f, h * 0.50f, w * 0.50f, h * 0.56f, w * 0.42f, h * 0.60f)
            lineTo(w * 0.40f, h * 0.75f)
            lineTo(w * 0.20f, h * 0.75f)
            close()
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF4ADE80), Color(0xFF22C55E), Color(0xFF15803D)),
                center = Offset(w * 0.45f, h * 0.45f),
                radius = w * 0.44f
            ),
            radius = w * 0.44f,
            center = Offset(w * 0.5f, h * 0.5f)
        )

        // Specular Highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.6f),
            radius = 3.5f,
            center = Offset(w * 0.35f, h * 0.35f)
        )
    }
}

@Composable
fun ChatVideo3DIcon(modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 20.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // 3D Camcorder Main Box
        val camRect = androidx.compose.ui.geometry.RoundRect(
            left = w * 0.12f,
            top = h * 0.25f,
            right = w * 0.64f,
            bottom = h * 0.78f,
            cornerRadius = CornerRadius(5f, 5f)
        )
        drawPath(
            path = Path().apply { addRoundRect(camRect) },
            brush = Brush.verticalGradient(listOf(Color(0xFF60A5FA), Color(0xFF2563EB), Color(0xFF1E40AF)))
        )

        // Camcorder Cone / Projector Lens
        val conePath = Path().apply {
            moveTo(w * 0.64f, h * 0.40f)
            lineTo(w * 0.88f, h * 0.24f)
            lineTo(w * 0.88f, h * 0.80f)
            lineTo(w * 0.64f, h * 0.64f)
            close()
        }
        drawPath(
            path = conePath,
            brush = Brush.verticalGradient(listOf(Color(0xFF93C5FD), Color(0xFF3B82F6), Color(0xFF1D4ED8)))
        )

        // Specular reflection
        drawCircle(
            color = Color.White.copy(alpha = 0.7f),
            radius = 2.5f,
            center = Offset(w * 0.28f, h * 0.38f)
        )
    }
}

@Composable
fun ChatGift3DIcon(modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 20.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // 3D Gift Box Base
        val boxRect = androidx.compose.ui.geometry.RoundRect(
            left = w * 0.16f,
            top = h * 0.40f,
            right = w * 0.84f,
            bottom = h * 0.86f,
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawPath(
            path = Path().apply { addRoundRect(boxRect) },
            brush = Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFFDC2626), Color(0xFF7F1D1D)))
        )

        // Gift Box Lid
        val lidRect = androidx.compose.ui.geometry.RoundRect(
            left = w * 0.12f,
            top = h * 0.28f,
            right = w * 0.88f,
            bottom = h * 0.42f,
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawPath(
            path = Path().apply { addRoundRect(lidRect) },
            brush = Brush.verticalGradient(listOf(Color(0xFFFDBA74), Color(0xFFB91C1C)))
        )

        // Golden Ribbon (Vertical)
        drawRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFFFFBEB), Color(0xFFFBBF24), Color(0xFFD97706))),
            topLeft = Offset(w * 0.44f, h * 0.28f),
            size = Size(w * 0.12f, h * 0.58f)
        )

        // Golden Ribbon Bow Loops
        drawCircle(color = Color(0xFFFDE047), radius = 3.2f, center = Offset(w * 0.40f, h * 0.22f))
        drawCircle(color = Color(0xFFFBBF24), radius = 3.2f, center = Offset(w * 0.60f, h * 0.22f))
    }
}
